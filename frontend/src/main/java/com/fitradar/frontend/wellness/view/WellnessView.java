package com.fitradar.frontend.wellness.view;

import com.fitradar.frontend.shared.view.MainLayout;
import com.fitradar.frontend.wellness.dto.WellnessRequest;
import com.fitradar.frontend.wellness.dto.WellnessResponse;
import com.fitradar.frontend.wellness.service.WellnessService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "wellness", layout = MainLayout.class)
@PageTitle("Wellness | FitRadar")
public class WellnessView extends VerticalLayout {

    private final WellnessService wellnessService;
    private final String username;
    private final String password;

    // Valores seleccionados
    private final int[]    mood           = {3};
    private final int[]    energy         = {3};
    private final int[]    sleep          = {4};
    private final int[]    stress         = {2};
    private final int[]    muscleSoreness = {3};
    private final double[] sleepHoursVal  = {8.0};
    // Opcionales — wearable
    private final double[] hrvVal         = {0};  // 0 = no introducido
    private final int[]    restingHrVal   = {0};  // 0 = no introducido

    private final VerticalLayout leftCol  = new VerticalLayout();
    private final VerticalLayout rightCol = new VerticalLayout();

    public WellnessView(WellnessService wellnessService) {
        this.wellnessService = wellnessService;
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

        HorizontalLayout cols = new HorizontalLayout();
        cols.setWidth("100%");
        cols.setPadding(false);
        cols.setSpacing(false);
        cols.setAlignItems(FlexComponent.Alignment.START);
        cols.getStyle()
                .set("padding", "20px 32px")
                .set("gap", "16px")
                .set("box-sizing", "border-box");

        leftCol.setWidth("50%");
        leftCol.setPadding(false);
        leftCol.setSpacing(false);
        leftCol.getStyle().set("gap", "10px");

        rightCol.setWidth("50%");
        rightCol.setPadding(false);
        rightCol.setSpacing(false);
        rightCol.getStyle().set("gap", "10px");

        cols.add(leftCol, rightCol);
        add(cols);
        refresh();
    }

    // ── Refresco ──────────────────────────────────────────────────────────────

    private void refresh() {
        leftCol.removeAll();
        rightCol.removeAll();
        try {
            List<WellnessResponse> records = wellnessService.getRecords(username, password)
                    .stream()
                    .sorted(Comparator.comparing(WellnessResponse::getRecordDate,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            Optional<WellnessResponse> today = records.stream()
                    .filter(r -> LocalDate.now().equals(r.getRecordDate()))
                    .findFirst();

            buildLeft(today);
            buildRight(today.orElseGet(this::emptyRecord), records);

        } catch (Exception e) {
            Notification.show("Error al cargar: " + e.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    // ── Columna izquierda ─────────────────────────────────────────────────────

    private void buildLeft(Optional<WellnessResponse> today) {
        H2 title = new H2("Medidor de Wellness");
        title.getStyle().set("margin", "0 0 2px 0").set("font-size", "1.6rem")
                .set("font-weight", "800").set("color", "white");

        Paragraph sub = new Paragraph(today.isPresent()
                ? "Ya registraste tu estado de hoy"
                : "¿Cómo te sientes hoy?");
        sub.getStyle().set("margin", "0 0 10px 0").set("font-size", "0.9rem").set("color", "#64748b");

        leftCol.add(title, sub);

        if (today.isPresent()) {
            leftCol.add(buildLockedSummary(today.get()));
        } else {
            leftCol.add(
                    buildMetricCard("Estado de ánimo",    "¿Cómo te sientes emocionalmente?", mood,           true),
                    buildMetricCard("Nivel de energía",   "¿Cuánta energía tienes?",           energy,         true),
                    buildMetricCard("Calidad del sueño",  "¿Cómo dormiste anoche?",             sleep,          true),
                    buildMetricCard("Nivel de estrés",    "¿Qué tan estresado estás?",          stress,         false),
                    buildMetricCard("Agujetas musculares","¿Cómo tienes los músculos?",         muscleSoreness, false),
                    buildSleepHoursCard(),
                    buildWearableCard(),
                    buildSaveButton()
            );
        }
    }

    // ── Tarjeta de métrica 1-5 ────────────────────────────────────────────────

    private VerticalLayout buildMetricCard(String titleText,
                                           String subtitleText, int[] val, boolean positive) {
        VerticalLayout card = card();
        card.getStyle().set("padding", "12px 16px");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidthFull();
        top.setAlignItems(FlexComponent.Alignment.CENTER);
        top.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout texts = new VerticalLayout();
        texts.setPadding(false);
        texts.setSpacing(false);
        Paragraph t = new Paragraph(titleText);
        t.getStyle().set("margin", "0").set("font-size", "0.95rem").set("font-weight", "700").set("color", "white");
        Paragraph s = new Paragraph(subtitleText);
        s.getStyle().set("margin", "0").set("font-size", "0.78rem").set("color", "#64748b");
        texts.add(t, s);

        Div badge = new Div();
        badge.getStyle().set("text-align", "right").set("min-width", "80px");
        updateBadge(badge, val[0], positive);

        top.add(texts, badge);

        // Selector 1-5
        Div selector = new Div();
        selector.getStyle()
                .set("display", "flex").set("gap", "6px")
                .set("width", "100%").set("margin-top", "8px");

        Div[] btns = new Div[5];
        for (int i = 1; i <= 5; i++) {
            int num = i;
            Div btn = new Div();
            btn.setText(String.valueOf(i));
            styleSelectorBtn(btn, num == val[0]);
            btn.addClickListener(e -> {
                val[0] = num;
                updateBadge(badge, num, positive);
                for (int j = 0; j < btns.length; j++)
                    styleSelectorBtn(btns[j], j + 1 == num);
            });
            btns[i - 1] = btn;
            selector.add(btn);
        }

        HorizontalLayout scaleRow = new HorizontalLayout();
        scaleRow.setWidthFull();
        scaleRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        scaleRow.add(
                scaleLabel(positive ? "Muy mal" : "Muy bajo"),
                scaleLabel(positive ? "Excelente" : "Muy alto")
        );

        card.add(top, selector, scaleRow);
        return card;
    }

    private void styleSelectorBtn(Div btn, boolean selected) {
        btn.getStyle()
                .set("flex", "1").set("height", "36px").set("border-radius", "10px")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center")
                .set("font-size", "0.95rem").set("font-weight", selected ? "800" : "500")
                .set("border", selected ? "none" : "1px solid rgba(255,255,255,0.12)")
                .set("background", selected ? "#10b981" : "rgba(255,255,255,0.04)")
                .set("color", selected ? "white" : "#64748b").set("cursor", "pointer")
                .set("box-shadow", selected ? "0 2px 10px rgba(16,185,129,0.35)" : "none");
    }

    private void updateBadge(Div badge, int value, boolean positive) {
        String text  = positive
                ? switch (value) { case 1 -> "Muy mal"; case 2 -> "Mal"; case 3 -> "Regular"; case 4 -> "Bien"; default -> "Excelente"; }
                : switch (value) { case 1 -> "Muy bajo"; case 2 -> "Bajo"; case 3 -> "Regular"; case 4 -> "Alto"; default -> "Muy alto"; };
        String color = positive
                ? (value >= 4 ? "#14e0a1" : value == 3 ? "#facc15" : "#fb7185")
                : (value <= 2 ? "#14e0a1" : value == 3 ? "#facc15" : "#fb7185");
        badge.getElement().setProperty("innerHTML",
                "<div style='font-size:0.85rem;font-weight:700;color:" + color + "'>" + text + "</div>");
    }

    // ── Tarjeta de horas de sueño ─────────────────────────────────────────────

    private VerticalLayout buildSleepHoursCard() {
        VerticalLayout card = card();
        card.getStyle().set("padding", "12px 16px");

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout texts = new VerticalLayout();
        texts.setPadding(false);
        texts.setSpacing(false);
        Paragraph t = new Paragraph("Horas de sueño");
        t.getStyle().set("margin", "0").set("font-size", "0.95rem").set("font-weight", "700").set("color", "white");
        Paragraph s = new Paragraph("¿Cuántas horas dormiste? (optimo: 8h)");
        s.getStyle().set("margin", "0").set("font-size", "0.78rem").set("color", "#64748b");
        texts.add(t, s);

        NumberField field = new NumberField();
        field.setMin(0); field.setMax(12); field.setStep(0.5);
        field.setValue(sleepHoursVal[0]);
        field.setSuffixComponent(new Paragraph("h"));
        field.setWidth("110px");
        applyNumberStyle(field);
        field.addValueChangeListener(e -> { if (e.getValue() != null) sleepHoursVal[0] = e.getValue(); });

        row.add(texts, field);
        card.add(row);
        return card;
    }

    // ── Tarjeta de wearable (opcional) ────────────────────────────────────────

    private VerticalLayout buildWearableCard() {
        VerticalLayout card = card();
        card.getStyle()
                .set("padding", "12px 16px")
                .set("border", "1px solid rgba(96,165,250,0.15)");

        Paragraph title = new Paragraph("Datos del wearable (opcional)");
        title.getStyle().set("margin", "0 0 4px 0").set("font-size", "0.85rem")
                .set("font-weight", "700").set("color", "#60a5fa");

        Paragraph hint = new Paragraph(
                "HRV y FC en reposo mejoran la precision del modelo ML. " +
                        "Déjalos en blanco si no tienes dispositivo.");
        hint.getStyle().set("margin", "0 0 10px 0").set("font-size", "0.75rem").set("color", "#475569");

        HorizontalLayout fields = new HorizontalLayout();
        fields.setWidthFull();
        fields.setSpacing(true);

        // HRV RMSSD
        NumberField hrv = new NumberField("HRV RMSSD (ms)");
        hrv.setPlaceholder("ej. 45");
        hrv.setMin(0); hrv.setMax(300);
        hrv.setWidth("50%");
        applyNumberStyle(hrv);
        hrv.addValueChangeListener(e -> hrvVal[0] = e.getValue() != null ? e.getValue() : 0);

        // FC en reposo
        NumberField restHr = new NumberField("FC en reposo (bpm)");
        restHr.setPlaceholder("ej. 55");
        restHr.setMin(30); restHr.setMax(120);
        restHr.setWidth("50%");
        applyNumberStyle(restHr);
        restHr.addValueChangeListener(e -> restingHrVal[0] = e.getValue() != null ? e.getValue().intValue() : 0);

        fields.add(hrv, restHr);
        card.add(title, hint, fields);
        return card;
    }

    // ── Botón guardar ─────────────────────────────────────────────────────────

    private Button buildSaveButton() {
        Button btn = new Button("Registrar wellness de hoy");
        btn.setWidthFull();
        btn.getStyle()
                .set("height", "48px").set("border-radius", "14px")
                .set("background", "#10b981").set("color", "white")
                .set("font-size", "1rem").set("font-weight", "700")
                .set("border", "none").set("cursor", "pointer")
                .set("box-shadow", "0 6px 20px rgba(16,185,129,0.30)")
                .set("margin-top", "4px");

        btn.addClickListener(e -> {
            try {
                WellnessRequest req = new WellnessRequest();
                req.setGeneralFeeling(mood[0]);
                req.setRecoveryFeeling(energy[0]);
                req.setSleepQuality(sleep[0]);
                req.setStress(stress[0]);
                req.setMuscleSoreness(muscleSoreness[0]);
                req.setFatigue(6 - energy[0]);
                req.setSleepHours(sleepHoursVal[0]);
                if (hrvVal[0] > 0)       req.setHrvRmssd(hrvVal[0]);
                if (restingHrVal[0] > 0) req.setRestingHr(restingHrVal[0]);

                wellnessService.createRecord(username, password, req);
                Notification.show("Wellness guardado", 2500, Notification.Position.MIDDLE);
                refresh();
            } catch (Exception ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.contains("hoy")) {
                    Notification.show("Ya registraste el wellness de hoy", 3000, Notification.Position.MIDDLE);
                } else {
                    Notification.show("Error al guardar: " + msg, 4000, Notification.Position.MIDDLE);
                }
            }
        });
        return btn;
    }

    // ── Estado bloqueado ──────────────────────────────────────────────────────

    private VerticalLayout buildLockedSummary(WellnessResponse r) {
        VerticalLayout card = card();
        card.getStyle()
                .set("border", "1px solid rgba(20,224,161,0.20)")
                .set("padding", "16px");

        Paragraph tag = new Paragraph("Wellness registrado hoy");
        tag.getStyle().set("margin", "0 0 12px 0").set("font-size", "0.9rem")
                .set("color", "#14e0a1").set("font-weight", "600");

        HorizontalLayout row1 = summaryRow("Ánimo",    r.getGeneralFeeling(),  true,
                "Energía", r.getRecoveryFeeling(), true);
        HorizontalLayout row2 = summaryRow("Sueño",    r.getSleepQuality(),    true,
                "Estrés",  r.getStress(),          false);
        HorizontalLayout row3 = new HorizontalLayout();
        row3.setWidthFull();
        row3.setSpacing(true);

        Div hoursBox = new Div();
        hoursBox.getStyle()
                .set("flex", "1").set("padding", "10px 12px").set("border-radius", "12px")
                .set("background", "rgba(255,255,255,0.04)")
                .set("border", "1px solid rgba(255,255,255,0.06)");
        if (r.getSleepHours() != null) {
            hoursBox.getElement().setProperty("innerHTML",
                    "<div style='font-size:0.75rem;color:#64748b;margin-bottom:3px'>Horas sueno</div>" +
                            "<div style='font-size:1.3rem;font-weight:900;color:#14e0a1;line-height:1'>" +
                            String.format(Locale.US, "%.1f", r.getSleepHours()) + "h</div>"
            );
        } else {
            hoursBox.getElement().setProperty("innerHTML",
                    "<div style='font-size:0.75rem;color:#64748b;margin-bottom:3px'>Horas sueno</div>" +
                            "<div style='font-size:1.3rem;font-weight:900;color:#64748b;line-height:1'>-</div>"
            );
        }
        row3.add(summaryMetric("Agujetas", r.getMuscleSoreness(), false), hoursBox);

        if (r.getHrvRmssd() != null || r.getRestingHr() != null) {
            Paragraph wearable = new Paragraph(
                    "Wearable: " +
                            (r.getHrvRmssd()  != null ? "HRV " + r.getHrvRmssd() + " ms  " : "") +
                            (r.getRestingHr() != null ? "FC reposo " + r.getRestingHr() + " bpm" : "")
            );
            wearable.getStyle().set("margin", "10px 0 0 0").set("font-size", "0.85rem").set("color", "#60a5fa");
            card.add(tag, row1, row2, row3, wearable);
        } else {
            card.add(tag, row1, row2, row3);
        }
        return card;
    }

    private HorizontalLayout summaryRow(String t1, Integer v1, boolean p1,
                                        String t2, Integer v2, boolean p2) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setSpacing(true);
        row.add(summaryMetric(t1, v1, p1), summaryMetric(t2, v2, p2));
        return row;
    }

    private Div summaryMetric(String title, Integer val, boolean positive) {
        int v = val != null ? val : 3;
        String color = positive
                ? (v >= 4 ? "#14e0a1" : v == 3 ? "#facc15" : "#fb7185")
                : (v <= 2 ? "#14e0a1" : v == 3 ? "#facc15" : "#fb7185");
        String lbl = positive
                ? switch (v) { case 1 -> "Muy mal"; case 2 -> "Mal"; case 3 -> "Regular"; case 4 -> "Bien"; default -> "Excelente"; }
                : switch (v) { case 1 -> "Muy bajo"; case 2 -> "Bajo"; case 3 -> "Regular"; case 4 -> "Alto"; default -> "Muy alto"; };
        Div box = new Div();
        box.getStyle()
                .set("flex", "1").set("padding", "10px 12px").set("border-radius", "12px")
                .set("background", "rgba(255,255,255,0.04)")
                .set("border", "1px solid rgba(255,255,255,0.06)");
        box.getElement().setProperty("innerHTML",
                "<div style='font-size:0.75rem;color:#64748b;margin-bottom:3px'>" + title + "</div>" +
                        "<div style='font-size:1.3rem;font-weight:900;color:" + color + ";line-height:1'>" + v + "/5</div>" +
                        "<div style='font-size:0.78rem;font-weight:600;color:" + color + ";margin-top:2px'>" + lbl + "</div>"
        );
        return box;
    }

    // ── Columna derecha ───────────────────────────────────────────────────────

    private void buildRight(WellnessResponse today, List<WellnessResponse> all) {
        rightCol.add(buildScoreCard(today), buildRadarCard(today), buildWeekChart(all), buildHistoryCard(all));
    }

    private VerticalLayout buildScoreCard(WellnessResponse r) {
        double score = calcScore(r);
        VerticalLayout card = card();
        card.getStyle()
                .set("background", "linear-gradient(135deg,#0f2a1a 0%,#0f1b38 100%)")
                .set("border", "1px solid rgba(20,224,161,0.15)").set("padding", "16px");

        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Paragraph lbl = new Paragraph("Puntuación hoy");
        lbl.getStyle().set("margin", "0").set("font-size", "0.9rem").set("color", "#94a3b8");

        VerticalLayout scoreBlock = new VerticalLayout();
        scoreBlock.setPadding(false);
        scoreBlock.setSpacing(false);
        scoreBlock.setAlignItems(FlexComponent.Alignment.END);

        H2 scoreText = new H2(String.format(Locale.US, "%.0f%%", score));
        scoreText.getStyle()
                .set("margin", "0").set("font-size", "2.8rem").set("font-weight", "900")
                .set("line-height", "1").set("color", scoreColor(score));

        Paragraph desc = new Paragraph(describeScore(score));
        desc.getStyle().set("margin", "2px 0 0 0").set("font-size", "0.9rem")
                .set("font-weight", "700").set("color", scoreColor(score));

        scoreBlock.add(scoreText, desc);
        row.add(lbl, scoreBlock);
        card.add(row);
        return card;
    }

    private VerticalLayout buildRadarCard(WellnessResponse r) {
        VerticalLayout card = card();
        card.getStyle().set("padding", "12px 16px");

        H3 title = new H3("Vision general");
        title.getStyle().set("margin", "0 0 6px 0").set("font-size", "1rem")
                .set("font-weight", "700").set("color", "white");

        Div radarContainer = new Div();
        radarContainer.setWidthFull();
        radarContainer.getStyle()
                .set("position", "relative").set("height", "220px")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center");

        radarContainer.add(buildDiamondGrid(), buildDiamondShape(r));
        radarContainer.add(axisLabel("Ánimo",      "50%",               "4px",                "translateX(-50%)"));
        radarContainer.add(axisLabel("Energía",    "calc(50% + 90px)",  "50%",                "translateY(-50%)"));
        radarContainer.add(axisLabel("Sueño",      "50%",               "calc(100% - 16px)",  "translateX(-50%)"));
        radarContainer.add(axisLabel("Bajo estrés","calc(50% - 90px)",  "50%",                "translateY(-50%) translateX(-100%)"));

        card.add(title, radarContainer);
        return card;
    }

    private VerticalLayout buildWeekChart(List<WellnessResponse> records) {
        VerticalLayout card = card();
        card.getStyle().set("padding", "12px 16px");

        H3 title = new H3("Esta semana");
        title.getStyle().set("margin", "0 0 8px 0").set("font-size", "1rem")
                .set("font-weight", "700").set("color", "white");

        Div chart = new Div();
        chart.setWidthFull();
        chart.getStyle()
                .set("display", "flex").set("align-items", "flex-end")
                .set("justify-content", "space-between").set("gap", "6px").set("height", "100px");

        LocalDate start = LocalDate.now().minusDays(6);
        for (int i = 0; i < 7; i++) {
            LocalDate day = start.plusDays(i);
            double pct = records.stream()
                    .filter(r -> day.equals(r.getRecordDate()))
                    .findFirst().map(this::calcScore).orElse(0.0);

            boolean isToday = day.equals(LocalDate.now());
            int barH = pct > 0 ? Math.max(8, (int)(pct * 0.65)) : 3;

            Div col = new Div();
            col.getStyle()
                    .set("flex", "1").set("display", "flex").set("flex-direction", "column")
                    .set("align-items", "center").set("justify-content", "flex-end").set("height", "100%");

            Div bar = new Div();
            bar.getStyle()
                    .set("width", "100%").set("height", barH + "px")
                    .set("background", pct > 0 ? "linear-gradient(180deg,#14e0a1,#0ea5a4)" : "rgba(255,255,255,0.06)")
                    .set("border-radius", "6px 6px 0 0");

            Div dayLbl = new Div();
            dayLbl.setText(day.format(DateTimeFormatter.ofPattern("EEE", new Locale("es", "ES"))));
            dayLbl.getStyle()
                    .set("font-size", "0.72rem").set("margin-top", "4px")
                    .set("color", isToday ? "#14e0a1" : "#64748b")
                    .set("font-weight", isToday ? "700" : "400");

            col.add(bar, dayLbl);
            chart.add(col);
        }

        card.add(title, chart);
        return card;
    }

    private VerticalLayout buildHistoryCard(List<WellnessResponse> records) {
        VerticalLayout card = card();
        card.getStyle().set("padding", "12px 16px");

        H3 title = new H3("Histórico");
        title.getStyle().set("margin", "0 0 8px 0").set("font-size", "1rem")
                .set("font-weight", "700").set("color", "white");

        card.add(title);

        if (records.isEmpty()) {
            Paragraph empty = new Paragraph("Sin registros todavía");
            empty.getStyle().set("color", "#64748b").set("margin", "0").set("font-size", "0.85rem");
            card.add(empty);
            return card;
        }

        records.subList(0, Math.min(records.size(), 5)).forEach(r -> {
            double score = calcScore(r);
            Div item = new Div();
            item.getStyle()
                    .set("display", "flex").set("align-items", "center")
                    .set("justify-content", "space-between")
                    .set("padding", "8px 12px").set("border-radius", "10px")
                    .set("background", "rgba(255,255,255,0.03)")
                    .set("border", "1px solid rgba(255,255,255,0.05)")
                    .set("margin-bottom", "6px");
            item.getElement().setProperty("innerHTML",
                    "<div>" +
                            "<div style='font-size:0.85rem;font-weight:600;color:white'>" + formatDate(r.getRecordDate()) + "</div>" +
                            "<div style='font-size:0.75rem;color:#64748b;margin-top:2px'>" +
                            "Animo " + safe(r.getGeneralFeeling()) + " · Energia " + safe(r.getRecoveryFeeling()) +
                            " · Sueno " + safe(r.getSleepQuality()) + " · Estres " + safe(r.getStress()) +
                            (r.getSleepHours() != null ? " · " + String.format(Locale.US,"%.1f",r.getSleepHours()) + "h" : "") +
                            "</div></div>" +
                            "<div style='font-size:1.1rem;font-weight:900;color:" + scoreColor(score) + "'>" +
                            String.format(Locale.US, "%.0f%%", score) + "</div>"
            );
            card.add(item);
        });

        return card;
    }

    // ── Radar ─────────────────────────────────────────────────────────────────

    private Div buildDiamondGrid() {
        Div grid = new Div();
        grid.getStyle().set("position", "absolute").set("width", "160px").set("height", "160px")
                .set("transform", "rotate(45deg)").set("border", "1px solid rgba(148,163,184,0.20)");
        for (int s : new int[]{120, 80, 40}) {
            Div inner = new Div();
            inner.getStyle()
                    .set("position", "absolute").set("left", "50%").set("top", "50%")
                    .set("width", s + "px").set("height", s + "px")
                    .set("transform", "translate(-50%,-50%)")
                    .set("border", "1px solid rgba(148,163,184,0.12)");
            grid.add(inner);
        }
        Div v = new Div();
        v.getStyle().set("position","absolute").set("left","50%").set("top","0")
                .set("width","1px").set("height","100%").set("background","rgba(148,163,184,0.15)");
        Div h = new Div();
        h.getStyle().set("position","absolute").set("top","50%").set("left","0")
                .set("width","100%").set("height","1px").set("background","rgba(148,163,184,0.15)");
        grid.add(v, h);
        return grid;
    }

    private Div buildDiamondShape(WellnessResponse r) {
        double m  = scaleD(safe(r.getGeneralFeeling()));
        double e  = scaleD(safe(r.getRecoveryFeeling()));
        double sl = scaleD(safe(r.getSleepQuality()));
        double st = scaleD(6 - safe(r.getStress()));
        String pts = "50% " + (50-m) + "%, " + (50+e) + "% 50%, 50% " + (50+sl) + "%, " + (50-st) + "% 50%";
        Div shape = new Div();
        shape.getStyle().set("position", "absolute").set("width", "160px").set("height", "160px")
                .set("background", "rgba(20,224,161,0.18)").set("border", "2px solid #14e0a1")
                .set("clip-path", "polygon(" + pts + ")");
        return shape;
    }

    private Div axisLabel(String text, String left, String top, String transform) {
        Div d = new Div();
        d.setText(text);
        d.getStyle().set("position", "absolute").set("left", left).set("top", top)
                .set("transform", transform).set("font-size", "0.75rem")
                .set("color", "#94a3b8").set("white-space", "nowrap").set("font-weight", "500");
        return d;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private VerticalLayout card() {
        VerticalLayout c = new VerticalLayout();
        c.setPadding(true);
        c.setSpacing(false);
        c.setWidthFull();
        c.getStyle().set("background", "#0f1b38").set("border-radius", "16px")
                .set("border", "1px solid rgba(255,255,255,0.06)").set("box-sizing", "border-box");
        return c;
    }

    private Paragraph scaleLabel(String text) {
        Paragraph p = new Paragraph(text);
        p.getStyle().set("margin", "0").set("font-size", "0.72rem").set("color", "#475569");
        return p;
    }

    private void applyNumberStyle(NumberField f) {
        f.getElement().getStyle()
                .set("--vaadin-input-field-background", "#1e293b")
                .set("--vaadin-input-field-border-radius", "10px")
                .set("--vaadin-input-field-border-color", "#334155")
                .set("--vaadin-input-field-value-color", "white")
                .set("--vaadin-input-field-label-color", "#94a3b8")
                .set("--vaadin-input-field-placeholder-color", "#475569");
    }

    private double calcScore(WellnessResponse r) {
        double m  = safe(r.getGeneralFeeling());
        double e  = safe(r.getRecoveryFeeling());
        double sl = safe(r.getSleepQuality());
        double st = 6 - safe(r.getStress());
        return ((m + e + sl + st) / 4.0 / 5.0) * 100.0;
    }

    private double scaleD(int v) {
        return switch (v) { case 1 -> 8; case 2 -> 18; case 3 -> 28; case 4 -> 38; default -> 48; };
    }

    private String scoreColor(double s) {
        if (s >= 85) return "#14e0a1"; if (s >= 70) return "#22c55e";
        if (s >= 55) return "#facc15"; if (s >= 40) return "#fb923c";
        return "#fb7185";
    }

    private String describeScore(double s) {
        if (s >= 85) return "Excelente estado"; if (s >= 70) return "Buen estado";
        if (s >= 55) return "Estado intermedio";  if (s >= 40) return "Conviene recuperar";
        return "Día delicado";
    }

    private String formatDate(LocalDate d) {
        if (d == null) return "-";
        return d.format(DateTimeFormatter.ofPattern("EEE d MMM", new Locale("es", "ES")));
    }

    private int safe(Integer v) { return v != null ? v : 3; }

    private WellnessResponse emptyRecord() {
        WellnessResponse r = new WellnessResponse();
        r.setGeneralFeeling(3); r.setRecoveryFeeling(3); r.setSleepQuality(3); r.setStress(3);
        return r;
    }
}