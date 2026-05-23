package com.fitradar.frontend.dashboard.view;

import com.fitradar.frontend.dashboard.dto.DashboardResponse;
import com.fitradar.frontend.dashboard.service.DashboardService;
import com.fitradar.frontend.risk.dto.RiskPredictionResponse;
import com.fitradar.frontend.shared.view.MainLayout;
import com.fitradar.frontend.training.dto.TrainingSessionResponse;
import com.fitradar.frontend.user.dto.UserResponse;
import com.fitradar.frontend.user.service.AuthService;
import com.fitradar.frontend.wellness.dto.WellnessResponse;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | FitRadar")
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;
    private final AuthService authService;

    public DashboardView(DashboardService dashboardService, AuthService authService) {
        this.dashboardService = dashboardService;
        this.authService = authService;

        setSizeFull();
        setPadding(false);
        setSpacing(true);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        getStyle()
                .set("background", "#020817").set("color", "white")
                .set("padding", "32px 40px").set("box-sizing", "border-box")
                .set("overflow-y", "auto");

        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        String password = (String) UI.getCurrent().getSession().getAttribute("password");

        if (username == null || password == null) {
            UI.getCurrent().navigate("");
            return;
        }

        try {
            DashboardResponse d = dashboardService.getDashboard(username, password);

            UserResponse user = null;
            try { user = authService.getUser(username, password); } catch (Exception ignored) {}

            add(buildWelcomeBanner(d), buildStatsRow(d), buildMiddleRow(d, user), buildBottomRow(d));
        } catch (Exception e) {
            Notification.show("Error al cargar el dashboard", 4000, Notification.Position.MIDDLE);
        }
    }

    private HorizontalLayout buildWelcomeBanner(DashboardResponse d) {
        HorizontalLayout banner = new HorizontalLayout();
        banner.setWidthFull();
        banner.setAlignItems(FlexComponent.Alignment.CENTER);
        banner.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        banner.getStyle()
                .set("background", "linear-gradient(135deg,rgba(6,78,59,0.45),rgba(15,23,42,0.95))")
                .set("border", "1px solid rgba(20,224,161,0.18)")
                .set("border-radius", "18px").set("padding", "24px")
                .set("box-sizing", "border-box");

        VerticalLayout textBlock = new VerticalLayout();
        textBlock.setPadding(false);
        textBlock.setSpacing(false);

        Paragraph greeting = new Paragraph(greeting() + ", " + firstName(safe(d.getFullName(), d.getUsername())));
        greeting.getStyle().set("margin", "0").set("font-size", "1rem")
                .set("font-weight", "700").set("color", "#14e0a1");

        H2 title = new H2(mainMessage(d));
        title.getStyle().set("margin", "6px 0").set("font-size", "1.8rem")
                .set("font-weight", "800").set("color", "white");

        Paragraph sub = new Paragraph(subMessage(d));
        sub.getStyle().set("margin", "0").set("font-size", "1rem").set("color", "#cbd5e1");

        textBlock.add(greeting, title, sub);

        HorizontalLayout quickBtns = new HorizontalLayout();
        quickBtns.setSpacing(true);
        quickBtns.add(quickBtn("+ Entrenamiento", "training", "#10b981"),
                quickBtn("+ Wellness", "wellness", "#f472b6"));

        VerticalLayout right = new VerticalLayout();
        right.setPadding(false);
        right.setSpacing(true);
        right.setAlignItems(FlexComponent.Alignment.END);
        right.add(quickBtns);

        banner.add(textBlock, right);
        return banner;
    }

    private Button quickBtn(String label, String route, String color) {
        Button btn = new Button(label, e -> UI.getCurrent().navigate(route));
        btn.getStyle()
                .set("height", "40px").set("padding", "0 16px").set("border-radius", "10px")
                .set("background", color + "22").set("color", color)
                .set("border", "1px solid " + color + "44")
                .set("font-weight", "600").set("cursor", "pointer");
        return btn;
    }

    private HorizontalLayout buildStatsRow(DashboardResponse d) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        String acwrStr   = d.getLatestAcwr() != null ? String.format(Locale.US, "%.2f", d.getLatestAcwr()) : "—";
        String acwrColor = acwrColor(d.getLatestAcwr());

        row.add(
                statCard("Sesiones esta semana", String.valueOf(safeInt(d.getWeeklyTrainingSessions())), "semana actual", "#14e0a1", VaadinIcon.CLUSTER),
                statCard("Tiempo esta semana",   formatDuration(safeInt(d.getWeeklyTrainingMinutes())), "minutos activos", "#60a5fa", VaadinIcon.TIMER),
                statCard("Calorías esta semana", String.valueOf(safeInt(d.getWeeklyCalories())), "kcal quemadas", "#fb923c", VaadinIcon.FIRE),
                statCardColored("ACWR actual", acwrStr, "carga aguda/crónica", acwrColor, VaadinIcon.CHART)
        );
        return row;
    }

    private HorizontalLayout buildMiddleRow(DashboardResponse d, UserResponse user) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        WellnessResponse w = d.getLatestWellness();
        row.add(wideCard("Wellness reciente",
                w != null ? formatDate(w.getRecordDate()) : "Sin registros hoy",
                w != null ? String.format(Locale.US, "%.0f%%", calcWellness(w)) : "—",
                "#f472b6", VaadinIcon.HEART_O));

        RiskPredictionResponse r = d.getLatestRiskPrediction();
        row.add(wideCardColored("Riesgo de lesión",
                r != null ? riskLabel(r.getRiskLevel()) : "Sin predicción",
                r != null ? String.format(Locale.US, "%.0f%%", safeDouble(r.getRiskScore()) * 100.0) : "—",
                r != null ? riskColor(r.getRiskLevel()) : "#64748b", VaadinIcon.SHIELD));

        // IMC — si hay datos muestra el valor, si no muestra aviso para completar perfil
        if (user != null && user.getHeightCm() != null && user.getWeightKg() != null) {
            row.add(imcCard(user.getHeightCm(), user.getWeightKg()));
        } else {
            row.add(wideCard("Índice de masa corporal",
                    "Añade altura y peso en tu perfil",
                    "—", "#64748b", VaadinIcon.SPECIALIST));
        }

        return row;
    }

    private HorizontalLayout imcCard(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        double imc     = weightKg / (heightM * heightM);
        String imcStr  = String.format(Locale.US, "%.1f", imc);

        String category;
        String color;
        if (imc < 16.0)       { category = "Delgadez severa"; color = "#fb7185"; }
        else if (imc < 18.5)  { category = "Bajo peso";       color = "#facc15"; }
        else if (imc < 25.0)  { category = "Peso normal";     color = "#14e0a1"; }
        else if (imc < 30.0)  { category = "Sobrepeso";       color = "#fb923c"; }
        else                  { category = "Obesidad";         color = "#fb7185"; }

        return wideCardColored("Índice de masa corporal",
                category + " · " + String.format(Locale.US, "%.0f", weightKg) + " kg / "
                        + String.format(Locale.US, "%.0f", heightCm) + " cm",
                imcStr, color, VaadinIcon.SPECIALIST);
    }

    private HorizontalLayout buildBottomRow(DashboardResponse d) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);

        TrainingSessionResponse t = d.getLatestTrainingSession();
        row.add(wideCard("Última actividad",
                t != null ? safe(t.getTitle(), "Entrenamiento") + " · " + formatDate(t.getSessionDate()) : "Sin actividades recientes",
                t != null && t.getDurationMinutes() != null ? t.getDurationMinutes() + "m" : "—",
                "#14e0a1", VaadinIcon.WRENCH));

        int samples = safeInt(d.getSamplesCollected());
        boolean personalized = samples >= 15;
        row.add(wideCard("Estado del modelo ML",
                personalized ? "Modelo personalizado activo ✅" : samples + " / 15 sesiones para personalizar",
                safeInt(d.getActiveInjuries()) + " lesión" + (safeInt(d.getActiveInjuries()) == 1 ? "" : "es"),
                personalized ? "#60a5fa" : "#94a3b8", VaadinIcon.COG));

        return row;
    }

    // ── Componentes ───────────────────────────────────────────────────────────

    private VerticalLayout statCard(String t, String v, String b, String accent, VaadinIcon icon) {
        return statCardColored(t, v, b, accent, icon);
    }

    private VerticalLayout statCardColored(String titleText, String valueText,
                                           String badgeText, String accent, VaadinIcon icon) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("25%");
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("background", "#0f172a").set("border-radius", "18px")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("padding", "22px").set("box-sizing", "border-box");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        top.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon iconEl = icon.create();
        iconEl.getStyle()
                .set("background", "rgba(255,255,255,0.06)").set("border-radius", "12px")
                .set("padding", "10px").set("color", accent).set("font-size", "20px");

        Paragraph badge = new Paragraph(badgeText);
        badge.getStyle().set("margin", "0").set("font-size", "0.8rem")
                .set("font-weight", "600").set("color", accent);

        Paragraph sub = new Paragraph(titleText);
        sub.getStyle().set("margin", "10px 0 4px 0").set("font-size", "0.9rem").set("color", "#64748b");

        H3 value = new H3(valueText);
        value.getStyle().set("margin", "0").set("font-size", "2rem")
                .set("font-weight", "900").set("color", accent);

        top.add(iconEl, badge);
        card.add(top, sub, value);
        return card;
    }

    private HorizontalLayout wideCard(String title, String subtitle, String value, String accent, VaadinIcon icon) {
        return wideCardColored(title, subtitle, value, accent, icon);
    }

    private HorizontalLayout wideCardColored(String titleText, String subtitleText,
                                             String valueText, String accent, VaadinIcon icon) {
        HorizontalLayout card = new HorizontalLayout();
        card.setWidthFull();
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        card.getStyle()
                .set("background", "#0f172a").set("border-radius", "18px")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("padding", "22px").set("box-sizing", "border-box");

        HorizontalLayout left = new HorizontalLayout();
        left.setAlignItems(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        Icon iconEl = icon.create();
        iconEl.getStyle()
                .set("background", "rgba(255,255,255,0.06)").set("border-radius", "14px")
                .set("padding", "12px").set("color", accent).set("font-size", "22px");

        VerticalLayout textBlock = new VerticalLayout();
        textBlock.setPadding(false);
        textBlock.setSpacing(false);

        H4 title = new H4(titleText);
        title.getStyle().set("margin", "0").set("font-size", "1rem")
                .set("font-weight", "700").set("color", "white");

        Paragraph sub = new Paragraph(subtitleText);
        sub.getStyle().set("margin", "3px 0 0 0").set("font-size", "0.85rem").set("color", "#64748b");

        textBlock.add(title, sub);
        left.add(iconEl, textBlock);

        H2 value = new H2(valueText);
        value.getStyle().set("margin", "0").set("font-size", "1.8rem")
                .set("font-weight", "900").set("color", accent);

        card.add(left, value);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String greeting() {
        int h = LocalTime.now().getHour();
        if (h < 12) return "Buenos días";
        if (h < 20) return "Buenas tardes";
        return "Buenas noches";
    }

    private String mainMessage(DashboardResponse d) {
        if (safeInt(d.getWeeklyTrainingSessions()) > 0 && d.getLatestWellness() != null) return "Tu panel deportivo está al día";
        if (safeInt(d.getWeeklyTrainingSessions()) > 0) return "Registra tu wellness de hoy";
        if (d.getLatestWellness() != null) return "¿Listo para entrenar hoy?";
        return "Bienvenido a FitRadar";
    }

    private String subMessage(DashboardResponse d) {
        if (d.getLatestTrainingSession() != null)
            return "Última actividad: " + safe(d.getLatestTrainingSession().getTitle(), "Entrenamiento registrado");
        return "Registra entrenamientos y wellness para activar todas las métricas.";
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "atleta";
        return fullName.trim().split("\\s+")[0];
    }

    private double calcWellness(WellnessResponse w) {
        double m  = safeScore(w.getGeneralFeeling());
        double e  = safeScore(w.getRecoveryFeeling());
        double sl = safeScore(w.getSleepQuality());
        double st = 6 - safeScore(w.getStress());
        return ((m + e + sl + st) / 4.0 / 5.0) * 100.0;
    }

    private String riskLabel(String level) {
        return switch (safe(level, "")) {
            case "HIGH" -> "Riesgo alto"; case "MEDIUM" -> "Riesgo moderado";
            case "LOW"  -> "Riesgo bajo"; default -> "Sin nivel";
        };
    }

    private String riskColor(String level) {
        return switch (safe(level, "")) {
            case "HIGH" -> "#fb7185"; case "MEDIUM" -> "#facc15";
            case "LOW"  -> "#14e0a1"; default -> "#64748b";
        };
    }

    private String acwrColor(Double acwr) {
        if (acwr == null) return "#64748b";
        if (acwr > 1.5)  return "#fb7185";
        if (acwr > 1.3)  return "#fb923c";
        if (acwr > 0.8)  return "#14e0a1";
        return "#facc15";
    }

    private String formatDuration(int mins) {
        int h = mins / 60, m = mins % 60;
        if (h == 0) return m + "m"; if (m == 0) return h + "h"; return h + "h " + m + "m";
    }

    private String formatDate(LocalDate d) {
        if (d == null) return "—";
        return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "ES")));
    }

    private int safeInt(Integer v)      { return v != null ? v : 0; }
    private double safeDouble(Double v) { return v != null ? v : 0.0; }
    private double safeScore(Integer v) { return v != null ? (double) v : 0.0; }
    private String safe(String v, String fallback) { return v != null && !v.isBlank() ? v : fallback; }
}