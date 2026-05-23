"""
FitRadar — API del modelo de predicción de riesgo de lesión
Endpoints:
  POST /predict              → predicción para una sesión
  POST /update/{pred_id}     → feedback del usuario (aprendizaje progresivo)
  GET  /health               → estado de la API
  GET  /user/{user_id}       → estadísticas de personalización de un usuario
"""

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional
import os
import logging

from adaptive_model import UserAdaptiveRiskModel

# ── Logging ───────────────────────────────────────────────────────────────────
logging.basicConfig(level=logging.INFO)
log = logging.getLogger(__name__)

# ── Cargar el modelo ──────────────────────────────────────────────────────────
MODEL_PATH = os.getenv("MODEL_PATH", "fitradar_risk_calculator.joblib")

try:
    adaptive_model = UserAdaptiveRiskModel.load(MODEL_PATH)
    log.info(f"Modelo cargado correctamente desde {MODEL_PATH}")
    log.info(f"Versión: {adaptive_model.VERSION}")
    log.info(f"Umbral global: {adaptive_model.global_threshold}")
except Exception as e:
    log.error(f"Error cargando el modelo: {e}")
    raise RuntimeError(f"No se pudo cargar el modelo desde {MODEL_PATH}: {e}")

# ── App ───────────────────────────────────────────────────────────────────────
app = FastAPI(
    title="FitRadar ML API",
    description="API de predicción de riesgo de lesión deportiva",
    version="1.0.0",
)

# ── Schemas ───────────────────────────────────────────────────────────────────

class PredictRequest(BaseModel):
    age:                Optional[float] = None
    bmi:                Optional[float] = None
    previous_injury:    Optional[float] = None
    training_duration:  Optional[float] = None
    training_intensity: Optional[float] = None
    training_load:      Optional[float] = None
    heart_rate:         Optional[float] = None
    respiratory_rate:   Optional[float] = None
    skin_temp:          Optional[float] = None
    spo2:               Optional[float] = None
    sleep:              Optional[float] = None
    recovery:           Optional[float] = None
    fatigue:            Optional[float] = None
    rest_period:        Optional[float] = None
    impact_force:       Optional[float] = None
    biomechanical_load: Optional[float] = None
    acwr:               Optional[float] = None
    source_id:          Optional[float] = -1


class UpdateRequest(BaseModel):
    outcome:  int   # 0 = no lesión, 1 = lesión/molestia
    user_id:  str


class PredictResponse(BaseModel):
    risk_probability:   float
    global_probability: float
    risk_prediction:    int
    risk_label:         str
    threshold_used:     float
    model_used:         str
    samples_collected:  int


class UpdateResponse(BaseModel):
    user_id:                        str
    samples_collected:              int
    personalized:                   bool
    retrained:                      bool
    samples_until_personalization:  int


class UserStatsResponse(BaseModel):
    user_id:                        str
    samples:                        int
    personalized:                   bool
    samples_until_personalization:  int
    injury_rate:                    float
    personal_threshold:             float


class HealthResponse(BaseModel):
    status:         str
    model_version:  str
    threshold:      float
    total_users:    int

# ── Endpoints ─────────────────────────────────────────────────────────────────

@app.get("/health", response_model=HealthResponse)
def health():
    """Comprueba que la API y el modelo están operativos."""
    return HealthResponse(
        status="ok",
        model_version=adaptive_model.VERSION,
        threshold=adaptive_model.global_threshold,
        total_users=len(adaptive_model._user_data),
    )


@app.post("/predict", response_model=PredictResponse)
def predict(request: PredictRequest, user_id: Optional[str] = None):
    """
    Predice el riesgo de lesión para una sesión de entrenamiento.
    Si se pasa user_id y el usuario tiene historial suficiente,
    se activa la capa de personalización.
    """
    try:
        input_data = request.model_dump()
        result     = adaptive_model.predict(input_data, user_id=user_id)

        log.info(
            f"Predicción — user={user_id} "
            f"prob={result['risk_probability']} label={result['risk_label']}"
        )

        return PredictResponse(**result)

    except Exception as e:
        log.error(f"Error en /predict: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/update/{prediction_id}", response_model=UpdateResponse)
def update(prediction_id: int, request: UpdateRequest):
    """
    Registra el resultado real de una sesión y actualiza
    la capa de calibración personal del usuario.
    Llamar cuando el usuario confirma si hubo lesión o no.
    """
    if request.outcome not in (0, 1):
        raise HTTPException(
            status_code=400,
            detail="outcome debe ser 0 (sin lesión) o 1 (lesión/molestia)"
        )

    try:
        dummy_input = {f: None for f in adaptive_model.features}

        result = adaptive_model.update(
            input_data    = dummy_input,
            actual_outcome= request.outcome,
            user_id       = request.user_id,
        )

        # Persistir el modelo tras cada actualización
        adaptive_model.save(MODEL_PATH)

        log.info(
            f"Feedback — user={request.user_id} "
            f"outcome={request.outcome} samples={result['samples_collected']}"
        )

        return UpdateResponse(**result)

    except Exception as e:
        log.error(f"Error en /update/{prediction_id}: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/user/{user_id}", response_model=UserStatsResponse)
def user_stats(user_id: str):
    """Estadísticas de personalización de un usuario."""
    try:
        stats = adaptive_model.get_user_stats(user_id)
        return UserStatsResponse(**stats)
    except Exception as e:
        log.error(f"Error en /user/{user_id}: {e}")
        raise HTTPException(status_code=500, detail=str(e))