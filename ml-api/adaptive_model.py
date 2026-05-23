"""
UserAdaptiveRiskModel — modelo de riesgo de lesión con personalización por usuario.
Este fichero debe estar junto al main.py para que joblib pueda deserializar el .joblib.
"""

import numpy as np
import pandas as pd
import joblib
from sklearn.linear_model import LogisticRegression
from datetime import datetime


def get_proba_positive(model, X_data):
    """Probabilidad de clase positiva para cualquier estimador sklearn."""
    if hasattr(model, "predict_proba"):
        return model.predict_proba(X_data)[:, 1]
    scores = model.decision_function(X_data)
    return 1 / (1 + np.exp(-scores))


def find_best_threshold(y_true, y_proba, metric="f0.5"):
    from sklearn.metrics import fbeta_score
    thresholds = np.linspace(0.05, 0.95, 181)
    best_t, best_score = 0.5, -1
    for t in thresholds:
        y_pred = (y_proba >= t).astype(int)
        score = fbeta_score(y_true, y_pred, beta=0.5, zero_division=0)
        if score > best_score:
            best_score = score
            best_t = t
    return float(best_t)


class UserAdaptiveRiskModel:
    """
    Modelo de riesgo de lesión con personalización progresiva por usuario.

    Capa 1 (global):   XGBoost calibrado entrenado sobre todos los datasets.
    Capa 2 (personal): Regresión logística por usuario entrenada sobre
                       (probabilidad_global, resultado_real) de sus sesiones.
    """

    VERSION = "1.0.0"

    def __init__(
            self,
            global_model,
            global_threshold: float,
            features: list,
            min_samples: int = 15,
            min_class_balance: float = 0.05,
    ):
        self.global_model      = global_model
        self.global_threshold  = global_threshold
        self.features          = features
        self.min_samples       = min_samples
        self.min_class_balance = min_class_balance

        self._user_data:        dict = {}
        self._user_calibrators: dict = {}
        self._user_thresholds:  dict = {}

    # ── API pública ──────────────────────────────────────────────────────────

    def predict(self, input_data: dict, user_id: str = None) -> dict:
        row = self._to_frame(input_data)
        global_prob = float(get_proba_positive(self.global_model, row)[0])

        if user_id and user_id in self._user_calibrators:
            cal           = self._user_calibrators[user_id]
            personal_prob = float(cal.predict_proba([[global_prob]])[0, 1])
            threshold     = self._user_thresholds.get(user_id, self.global_threshold)
            model_used    = "personalized"
        else:
            personal_prob = global_prob
            threshold     = self.global_threshold
            model_used    = "global"

        prediction = int(personal_prob >= threshold)
        label      = self._risk_label(personal_prob)
        samples    = len(self._user_data.get(user_id, {}).get("y", [])) if user_id else 0

        return {
            "risk_probability":   round(personal_prob, 4),
            "global_probability": round(global_prob, 4),
            "risk_prediction":    prediction,
            "risk_label":         label,
            "threshold_used":     round(threshold, 4),
            "model_used":         model_used,
            "samples_collected":  samples,
        }

    def update(self, input_data: dict, actual_outcome: int, user_id: str) -> dict:
        row         = self._to_frame(input_data)
        global_prob = float(get_proba_positive(self.global_model, row)[0])

        if user_id not in self._user_data:
            self._user_data[user_id] = {"X": [], "y": []}

        self._user_data[user_id]["X"].append([global_prob])
        self._user_data[user_id]["y"].append(int(actual_outcome))

        n         = len(self._user_data[user_id]["y"])
        retrained = False

        if n >= self.min_samples:
            retrained = self._retrain_personal_layer(user_id)

        return {
            "user_id":                       user_id,
            "samples_collected":             n,
            "personalized":                  user_id in self._user_calibrators,
            "retrained":                     retrained,
            "samples_until_personalization": max(0, self.min_samples - n),
        }

    def get_user_stats(self, user_id: str) -> dict:
        if user_id not in self._user_data:
            return {
                "user_id":                       user_id,
                "samples":                       0,
                "personalized":                  False,
                "samples_until_personalization": self.min_samples,
                "injury_rate":                   0.0,
                "personal_threshold":            self.global_threshold,
            }
        n = len(self._user_data[user_id]["y"])
        y = np.array(self._user_data[user_id]["y"])
        return {
            "user_id":                       user_id,
            "samples":                       n,
            "personalized":                  user_id in self._user_calibrators,
            "samples_until_personalization": max(0, self.min_samples - n),
            "injury_rate":                   round(float(y.mean()), 3) if n > 0 else 0.0,
            "personal_threshold":            round(
                self._user_thresholds.get(user_id, self.global_threshold), 4),
        }

    def reset_user(self, user_id: str):
        for store in [self._user_data, self._user_calibrators, self._user_thresholds]:
            store.pop(user_id, None)

    def save(self, path):
        package = {
            "version":           self.VERSION,
            "trained_at":        datetime.now().isoformat(),
            "global_model":      self.global_model,
            "global_threshold":  self.global_threshold,
            "features":          self.features,
            "min_samples":       self.min_samples,
            "min_class_balance": self.min_class_balance,
            "user_data":         self._user_data,
            "user_calibrators":  self._user_calibrators,
            "user_thresholds":   self._user_thresholds,
        }
        joblib.dump(package, path)

    @classmethod
    def load(cls, path) -> "UserAdaptiveRiskModel":
        pkg  = joblib.load(path)
        inst = cls(
            global_model      = pkg["global_model"],
            global_threshold  = pkg["global_threshold"],
            features          = pkg["features"],
            min_samples       = pkg.get("min_samples", 15),
            min_class_balance = pkg.get("min_class_balance", 0.05),
        )
        inst._user_data        = pkg.get("user_data", {})
        inst._user_calibrators = pkg.get("user_calibrators", {})
        inst._user_thresholds  = pkg.get("user_thresholds", {})
        return inst

    # ── Privado ───────────────────────────────────────────────────────────────

    def _to_frame(self, input_data: dict) -> pd.DataFrame:
        row = pd.DataFrame([input_data])
        for col in self.features:
            if col not in row.columns:
                row[col] = np.nan
        return row[self.features]

    def _retrain_personal_layer(self, user_id: str) -> bool:
        X_u = np.array(self._user_data[user_id]["X"])
        y_u = np.array(self._user_data[user_id]["y"])

        unique, counts = np.unique(y_u, return_counts=True)
        if len(unique) < 2:
            return False
        if min(counts) / len(y_u) < self.min_class_balance:
            return False

        cal = LogisticRegression(class_weight="balanced", max_iter=1000, random_state=42)
        cal.fit(X_u, y_u)
        self._user_calibrators[user_id] = cal

        probas = cal.predict_proba(X_u)[:, 1]
        best_t = find_best_threshold(y_u, probas, metric="f0.5")
        self._user_thresholds[user_id] = best_t
        return True

    @staticmethod
    def _risk_label(prob: float) -> str:
        if prob < 0.35:  return "Riesgo bajo"
        if prob < 0.65:  return "Riesgo medio"
        return "Riesgo alto"