package com.fitradar.frontend.risk.view;

import com.fitradar.frontend.risk.dto.RiskPredictionResponse;
import com.fitradar.frontend.risk.service.RiskPredictionService;
import com.fitradar.frontend.shared.view.MainLayout;
import com.fitradar.frontend.training.dto.TrainingSessionResponse;
import com.fitradar.frontend.training.service.TrainingSessionService;
import com.fitradar.frontend.wellness.dto.WellnessResponse;
import com.fitradar.frontend.wellness.service.WellnessService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "risk", layout = MainLayout.class)
@PageTitle("Riesgo de lesión | FitRadar")
public class RiskPredictionView extends VerticalLayout {

    private final RiskPredictionService riskPredictionService;
    private final TrainingSessionService trainingSessionService;
    private final WellnessService wellnessService;

    private final String username;
    private final String password;

    private final VerticalLayout leftCol  = new VerticalLayout();
    private final VerticalLayout rightCol = new VerticalLayout();

    public RiskPredictionView(RiskPredictionService riskPredictionService,
                              TrainingSessionService trainingSessionService,
                              WellnessService wellnessService) {
        this.riskPredictionService  = riskPredictionService;
        this.trainingSessionService = trainingSessionService;
        this.wellnessService        = wellnessService;
        this.username = (String) UI.getCurrent().getSession().getAttribute("username");
        this.password = (String) UI.getCurrent().getSession().getAttribute("password");

        if (username == null || password == null) {
            UI.getCurrent().navigate("");
            return;
        }

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#020817").set("overflow-y", "auto");

        add(buildHeader());

        HorizontalLayout cols = new HorizontalLayout();
        cols.setSizeFull();
        cols.setPadding(false);
        cols.setSpacing(false);
        cols.getStyle()
                .set("padding", "0 32px 32px 32px")
                .set("gap", "20px")
                .set("box-sizing", "border-box");

        leftCol.setWidth("58%");
        leftCol.setPadding(false);
        leftCol.setSpacing(true);

        rightCol.setWidth("42%");
        rightCol.setPadding(false);
        rightCol.setSpacing(true);

        cols.add(leftCol, rightCol);
        add(cols);

        refresh();
    }

    private HorizontalLayout buildHeader() {
        H2 title = new H2("Medidor de Riesgo");
        title.getStyle().set("margin", "0").set("font-size", "2rem")
                .set("font-weight", "800").set("color", "white");

        Paragraph subtitle = new Paragraph(
                "Predicción generada automáticamente por el modelo ML tras cada entrenamiento");
        subtitle.getStyle()
                .set("margin", "4px 0 0 0").set("font-size", "1rem").set("color", "#94a3b8");

        VerticalLayout textBlock = new VerticalLayout(title, subtitle);
        textBlock.setPadding(false);
        textBlock.setSpacing(false);

        Button refreshBtn = new Button("↻ Actualizar");
        refreshBtn.addClickListener(e -> refresh());
        refreshBtn.getStyle()
                .set("height", "44px").set("padding", "0 20px").set("border-radius", "12px")
                .set("background", "rgba(255,255,255,0.05)").set("color", "#94a3b8")
                .set("border", "1px solid rgba(255,255,255,0.10)").set("cursor", "pointer");

        HorizontalLayout header = new HorizontalLayout(textBlock, refreshBtn);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.getStyle()
                .set("padding", "28px 32px 12px 32px")
                .set("box-sizing", "border-box");
        return header;
    }

    private void refresh() {
        leftCol.removeAll();
        rightCol.removeAll();
        try {
            List<RiskPredictionResponse> predictions = riskPredictionService
                    .getPredictions(username, password)
                    .stream()
                    .sorted(Comparator.comparing(
                            RiskPredictionResponse::getPredictionDate,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            Optional<RiskPredictionResponse> latest = predictions.isEmpty()
                    ? Optional.empty() : Optional.of(predictions.get(0));

            buildLeft(latest, predictions);
            buildRight(latest, predictions);

        } catch (Exception e) {
            Notification.show("Error al cargar predicciones", 4000, Notification.Position.MIDDLE);
        }
    }

    private void buildLeft(Optional<RiskPredictionResponse> latest,
                           List<RiskPredictionResponse> all) {
        if (latest.isEmpty()) {
            leftCol.add(buildEmptyState());
        } else {
            leftCol.add(buildGaugeCard(latest.get()));
            leftCol.add(buildFeedbackCard(latest.get()));
        }
        leftCol.add(buildContextCard());
    }

    private VerticalLayout buildGaugeCard(RiskPredictionResponse r) {
        int scoreInt = percent(r.getRiskScore());
        String color  = riskColor(r.getRiskLevel());
        String bg     = riskBg(r.getRiskLevel());
        String border = riskBorder(r.getRiskLevel());

        VerticalLayout card = card();
        card.getStyle()
                .set("background", "linear-gradient(135deg,#0f1b38 0%,#0f2a20 100%)")
                .set("border", "1px solid " + border);
        card.setAlignItems(Alignment.CENTER);

        HorizontalLayout badges = new HorizontalLayout();
        badges.setWidthFull();
        badges.setAlignItems(Alignment.CENTER);
        badges.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Paragraph dateStr = new Paragraph(formatDateTime(r.getPredictionDate()));
        dateStr.getStyle().set("margin", "0").set("font-size", "0.85rem").set("color", "#64748b");

        HorizontalLayout badgeRow = new HorizontalLayout();
        badgeRow.setSpacing(true);

        Span modelBadge = new Span(
                "personalized".equals(r.getModelUsed()) ? "⚡ Personalizado" : "🌐 Global");
        modelBadge.getStyle()
                .set("padding", "4px 10px").set("border-radius", "8px")
                .set("font-size", "0.8rem").set("font-weight", "700")
                .set("background", "personalized".equals(r.getModelUsed())
                        ? "rgba(96,165,250,0.15)" : "rgba(148,163,184,0.10)")
                .set("color", "personalized".equals(r.getModelUsed()) ? "#60a5fa" : "#94a3b8")
                .set("border", "1px solid " + ("personalized".equals(r.getModelUsed())
                        ? "rgba(96,165,250,0.25)" : "rgba(148,163,184,0.15)"));

        Span versionBadge = new Span("v" + safe(r.getModelVersion()));
        versionBadge.getStyle()
                .set("padding", "4px 8px").set("border-radius", "8px")
                .set("font-size", "0.75rem")
                .set("background", "rgba(255,255,255,0.05)").set("color", "#475569");

        badgeRow.add(modelBadge, versionBadge);
        badges.add(dateStr, badgeRow);

        Div gauge = new Div();
        gauge.getStyle()
                .set("width", "200px").set("height", "100px")
                .set("border-radius", "200px 200px 0 0")
                .set("background", "conic-gradient(from 270deg, " + color + " 0deg, "
                        + color + " " + (scoreInt * 1.8) + "deg, #1e293b "
                        + (scoreInt * 1.8) + "deg, #1e293b 180deg)")
                .set("margin-top", "20px");

        H2 scoreText = new H2(scoreInt + "%");
        scoreText.getStyle()
                .set("margin", "-70px 0 0 0").set("font-size", "2.8rem")
                .set("font-weight", "900").set("color", color);

        Span levelBadge = new Span(riskLabel(r.getRiskLevel()).toUpperCase(Locale.ROOT));
        levelBadge.getStyle()
                .set("margin-top", "16px").set("padding", "10px 22px")
                .set("border-radius", "12px").set("font-size", "1rem")
                .set("font-weight", "800").set("background", bg)
                .set("color", color).set("border", "1px solid " + border);

        Paragraph rec = new Paragraph(safe(r.getRecommendation()));
        rec.getStyle()
                .set("margin", "16px 0 0 0").set("font-size", "1rem")
                .set("color", "#cbd5e1").set("text-align", "center")
                .set("line-height", "1.5").set("max-width", "420px");

        card.add(badges, gauge, scoreText, levelBadge, rec);
        return card;
    }

    private VerticalLayout buildFeedbackCard(RiskPredictionResponse r) {
        VerticalLayout card = card();
        card.getStyle().set("border", "1px solid rgba(250,204,21,0.15)");

        H3 title = new H3("¿Cómo fue después de este entrenamiento?");
        title.getStyle()
                .set("margin", "0 0 4px 0").set("font-size", "1.1rem")
                .set("font-weight", "700").set("color", "white");

        // Muestra la fecha del entrenamiento para que el usuario sepa a cuál se refiere
        String dateRef = r.getPredictionDate() != null
                ? "Predicción del " + formatDateTime(r.getPredictionDate())
                : "Última predicción";
        Paragraph sub = new Paragraph(dateRef + ". Tu respuesta mejora el modelo y lo adapta a ti."
                + (r.getSamplesCollected() != null
                ? " " + r.getSamplesCollected() + " sesiones registradas." : ""));
        sub.getStyle()
                .set("margin", "0 0 14px 0").set("font-size", "0.85rem").set("color", "#64748b");

        Button okBtn = new Button("✅ Sin lesión ni molestia");
        okBtn.getStyle()
                .set("flex", "1").set("height", "46px").set("border-radius", "12px")
                .set("background", "rgba(20,224,161,0.10)").set("color", "#14e0a1")
                .set("border", "1px solid rgba(20,224,161,0.25)").set("font-weight", "600")
                .set("cursor", "pointer");

        Button injuredBtn = new Button("🩹 Tuve molestia o lesión");
        injuredBtn.getStyle()
                .set("flex", "1").set("height", "46px").set("border-radius", "12px")
                .set("background", "rgba(251,113,133,0.10)").set("color", "#fb7185")
                .set("border", "1px solid rgba(251,113,133,0.25)").set("font-weight", "600")
                .set("cursor", "pointer");

        HorizontalLayout btnRow = new HorizontalLayout(okBtn, injuredBtn);
        btnRow.setWidthFull();
        btnRow.setSpacing(true);

        // Mensaje de confirmación tras dar feedback — oculto inicialmente
        Paragraph doneMsg = new Paragraph("✅ Feedback enviado. ¡Gracias!");
        doneMsg.getStyle()
                .set("margin", "0").set("font-size", "0.95rem")
                .set("font-weight", "700").set("color", "#14e0a1");
        doneMsg.setVisible(false);

        // Al pulsar se ocultan los botones y aparece el mensaje
        okBtn.addClickListener(e -> {
            sendFeedback(r, 0);
            btnRow.setVisible(false);
            doneMsg.setVisible(true);
        });

        injuredBtn.addClickListener(e -> {
            sendFeedback(r, 1);
            btnRow.setVisible(false);
            doneMsg.setVisible(true);
        });

        card.add(title, sub, btnRow, doneMsg);
        return card;
    }

    private VerticalLayout buildContextCard() {
        VerticalLayout card = card();

        H3 title = new H3("Datos usados en la última predicción");
        title.getStyle()
                .set("margin", "0 0 12px 0").set("font-size", "1.1rem")
                .set("font-weight", "700").set("color", "white");

        card.add(title);

        try {
            List<TrainingSessionResponse> sessions =
                    trainingSessionService.getSessions(username, password);
            if (!sessions.isEmpty()) {
                TrainingSessionResponse last = sessions.stream()
                        .filter(s -> s.getSessionDate() != null)
                        .max(Comparator.comparing(TrainingSessionResponse::getSessionDate))
                        .orElse(sessions.get(0));

                String acwrStr = last.getAcwr() != null
                        ? String.format(Locale.US, "%.2f", last.getAcwr()) : "—";

                card.add(contextRow("🏃 Último entrenamiento",
                        safe(last.getTitle()) + " · "
                                + safeInt(last.getDurationMinutes()) + " min · RPE "
                                + safeInt(last.getRpe()) + " · ACWR " + acwrStr));
            }
        } catch (Exception ignored) {}

        try {
            wellnessService.getRecords(username, password).stream()
                    .filter(w -> w.getRecordDate() != null)
                    .max(Comparator.comparing(WellnessResponse::getRecordDate))
                    .ifPresent(w -> {
                        String wText = "Ánimo " + safeInt(w.getGeneralFeeling())
                                + " · Energía " + safeInt(w.getRecoveryFeeling())
                                + " · Estrés " + safeInt(w.getStress())
                                + " · Sueño " + safeInt(w.getSleepQuality());
                        card.add(contextRow("💚 Último wellness", wText));
                    });
        } catch (Exception ignored) {}

        return card;
    }

    private Div contextRow(String label, String value) {
        Div row = new Div();
        row.getStyle()
                .set("padding", "10px 14px").set("border-radius", "12px")
                .set("background", "rgba(255,255,255,0.03)")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("margin-bottom", "8px");
        row.getElement().setProperty("innerHTML",
                "<div style='font-size:0.8rem;color:#64748b;margin-bottom:3px'>"
                        + label + "</div>" +
                        "<div style='font-size:0.9rem;color:#cbd5e1;font-weight:500'>"
                        + value + "</div>"
        );
        return row;
    }

    private VerticalLayout buildEmptyState() {
        VerticalLayout card = card();
        card.setAlignItems(Alignment.CENTER);
        card.getStyle().set("border", "1px solid rgba(255,255,255,0.06)");

        Paragraph emoji = new Paragraph("🤖");
        emoji.getStyle().set("margin", "8px 0").set("font-size", "3rem").set("text-align", "center");

        H3 title = new H3("Aún no hay predicciones");
        title.getStyle()
                .set("margin", "0").set("font-size", "1.3rem")
                .set("font-weight", "700").set("color", "white").set("text-align", "center");

        Paragraph msg = new Paragraph(
                "El modelo ML genera una predicción automáticamente cada vez que registras "
                        + "un entrenamiento. Registra tu primera sesión para ver tu riesgo.");
        msg.getStyle()
                .set("margin", "8px 0 16px 0").set("font-size", "1rem")
                .set("color", "#64748b").set("text-align", "center").set("max-width", "400px");

        Button goBtn = new Button("→ Ir a Actividades");
        goBtn.addClickListener(e -> UI.getCurrent().navigate("training"));
        goBtn.getStyle()
                .set("height", "46px").set("padding", "0 24px").set("border-radius", "12px")
                .set("background", "#10b981").set("color", "white")
                .set("font-weight", "700").set("border", "none").set("cursor", "pointer");

        card.add(emoji, title, msg, goBtn);
        return card;
    }

    private void buildRight(Optional<RiskPredictionResponse> latest,
                            List<RiskPredictionResponse> all) {
        rightCol.add(buildPersonalizationCard(latest.orElse(null)));
        rightCol.add(buildWeekChart(all));
        rightCol.add(buildHistoryList(all));
    }

    private VerticalLayout buildPersonalizationCard(RiskPredictionResponse latest) {
        VerticalLayout card = card();
        card.getStyle().set("border", "1px solid rgba(96,165,250,0.12)");

        H3 title = new H3("Personalización del modelo");
        title.getStyle()
                .set("margin", "0 0 8px 0").set("font-size", "1.1rem")
                .set("font-weight", "700").set("color", "white");

        int samples = latest != null && latest.getSamplesCollected() != null
                ? latest.getSamplesCollected() : 0;
        int pct = Math.min(100, (int) ((samples / 15.0) * 100));
        boolean personalized = "personalized".equals(latest != null ? latest.getModelUsed() : "");

        Paragraph status = new Paragraph(personalized
                ? "✅ Modelo personalizado activo"
                : samples + " / 15 sesiones para activar personalización");
        status.getStyle()
                .set("margin", "0 0 10px 0").set("font-size", "0.9rem")
                .set("color", personalized ? "#14e0a1" : "#64748b");

        Div track = new Div();
        track.setWidthFull();
        track.setHeight("8px");
        track.getStyle()
                .set("background", "#1e293b").set("border-radius", "999px")
                .set("overflow", "hidden");

        Div fill = new Div();
        fill.setWidth(pct + "%");
        fill.setHeight("8px");
        fill.getStyle()
                .set("background", personalized
                        ? "linear-gradient(90deg,#14e0a1,#0ea5a4)"
                        : "linear-gradient(90deg,#60a5fa,#3b82f6)")
                .set("border-radius", "999px");
        track.add(fill);

        Paragraph hint = new Paragraph(personalized
                ? "El modelo ajusta predicciones con tu historial personal."
                : "Una vez activo, el modelo aprende de tus respuestas para personalizar alertas.");
        hint.getStyle()
                .set("margin", "8px 0 0 0").set("font-size", "0.8rem").set("color", "#475569");

        card.add(title, status, track, hint);
        return card;
    }

    private VerticalLayout buildWeekChart(List<RiskPredictionResponse> predictions) {
        VerticalLayout card = card();

        H3 title = new H3("↗ Evolución esta semana");
        title.getStyle()
                .set("margin", "0 0 12px 0").set("font-size", "1.1rem")
                .set("font-weight", "700").set("color", "white");

        Div chart = new Div();
        chart.setWidthFull();
        chart.getStyle()
                .set("display", "flex").set("align-items", "flex-end")
                .set("justify-content", "space-between").set("gap", "8px")
                .set("height", "150px");

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);

        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            int score = predictions.stream()
                    .filter(p -> p.getPredictionDate() != null
                            && p.getPredictionDate().toLocalDate().equals(day))
                    .max(Comparator.comparing(RiskPredictionResponse::getPredictionDate))
                    .map(p -> percent(p.getRiskScore()))
                    .orElse(0);

            boolean isToday = day.equals(LocalDate.now());
            String barColor = score > 0 ? riskColorByScore(score) : "rgba(255,255,255,0.06)";
            int barH = score > 0 ? Math.max(10, score) : 4;

            Div col = new Div();
            col.getStyle()
                    .set("flex", "1").set("display", "flex").set("flex-direction", "column")
                    .set("align-items", "center").set("justify-content", "flex-end")
                    .set("height", "100%");

            Div bar = new Div();
            bar.getStyle()
                    .set("width", "100%").set("height", barH + "px")
                    .set("background", barColor).set("border-radius", "6px 6px 0 0");

            Div dayLbl = new Div();
            dayLbl.setText(day.format(DateTimeFormatter.ofPattern("EEE", new Locale("es", "ES"))));
            dayLbl.getStyle()
                    .set("font-size", "0.75rem").set("margin-top", "4px")
                    .set("color", isToday ? "#14e0a1" : "#64748b")
                    .set("font-weight", isToday ? "700" : "400");

            col.add(bar, dayLbl);
            chart.add(col);
        }

        card.add(title, chart);
        return card;
    }

    private VerticalLayout buildHistoryList(List<RiskPredictionResponse> predictions) {
        VerticalLayout card = card();

        H3 title = new H3("Historial de predicciones");
        title.getStyle()
                .set("margin", "0 0 12px 0").set("font-size", "1.1rem")
                .set("font-weight", "700").set("color", "white");

        card.add(title);

        if (predictions.isEmpty()) {
            Paragraph empty = new Paragraph("Sin historial todavía");
            empty.getStyle().set("color", "#475569").set("margin", "0");
            card.add(empty);
            return card;
        }

        predictions.subList(0, Math.min(predictions.size(), 6)).forEach(p -> {
            int sc = percent(p.getRiskScore());
            String color = riskColorByScore(sc);

            Div item = new Div();
            item.getStyle()
                    .set("padding", "10px 14px").set("border-radius", "12px")
                    .set("background", "rgba(255,255,255,0.03)")
                    .set("border", "1px solid rgba(255,255,255,0.05)")
                    .set("margin-bottom", "6px")
                    .set("display", "flex").set("align-items", "center")
                    .set("justify-content", "space-between");

            Div left = new Div();
            left.getElement().setProperty("innerHTML",
                    "<div style='font-size:0.85rem;font-weight:600;color:white'>"
                            + formatDateTime(p.getPredictionDate()) + "</div>" +
                            "<div style='font-size:0.75rem;color:#64748b;margin-top:2px'>"
                            + riskLabel(p.getRiskLevel()) + " · "
                            + ("personalized".equals(p.getModelUsed()) ? "⚡ personalizado" : "global")
                            + "</div>"
            );

            Div right = new Div();
            right.getElement().setProperty("innerHTML",
                    "<div style='font-size:1.2rem;font-weight:900;color:" + color
                            + ";text-align:right'>" + sc + "%</div>"
            );

            item.add(left, right);
            card.add(item);
        });

        return card;
    }

    private void sendFeedback(RiskPredictionResponse prediction, int outcome) {
        try {
            riskPredictionService.sendFeedback(username, password, prediction.getId(), outcome);
        } catch (Exception e) {
            Notification.show("Error al enviar feedback: " + e.getMessage(),
                    4000, Notification.Position.MIDDLE);
        }
    }

    private VerticalLayout card() {
        VerticalLayout c = new VerticalLayout();
        c.setPadding(true);
        c.setSpacing(true);
        c.setWidthFull();
        c.getStyle()
                .set("background", "#0f172a").set("border-radius", "20px")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("box-sizing", "border-box");
        return c;
    }

    private String riskColor(String level) {
        return switch (safe(level)) {
            case "HIGH"   -> "#fb7185";
            case "MEDIUM" -> "#facc15";
            default       -> "#14e0a1";
        };
    }

    private String riskColorByScore(int score) {
        if (score >= 60) return "#fb7185";
        if (score >= 35) return "#facc15";
        return "#14e0a1";
    }

    private String riskBg(String level) {
        return switch (safe(level)) {
            case "HIGH"   -> "rgba(251,113,133,0.12)";
            case "MEDIUM" -> "rgba(250,204,21,0.12)";
            default       -> "rgba(20,224,161,0.12)";
        };
    }

    private String riskBorder(String level) {
        return switch (safe(level)) {
            case "HIGH"   -> "rgba(251,113,133,0.30)";
            case "MEDIUM" -> "rgba(250,204,21,0.30)";
            default       -> "rgba(20,224,161,0.30)";
        };
    }

    private String riskLabel(String level) {
        return switch (safe(level)) {
            case "HIGH"   -> "Riesgo alto";
            case "MEDIUM" -> "Riesgo moderado";
            default       -> "Riesgo bajo";
        };
    }

    private int percent(Double v) {
        if (v == null) return 0;
        return Math.max(0, Math.min(100, (int) Math.round(v * 100.0)));
    }

    private int safeInt(Integer v)  { return v != null ? v : 0; }
    private String safe(String v)   { return v != null ? v : ""; }

    private String formatDateTime(LocalDateTime dt) {
        if (dt == null) return "—";
        return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}