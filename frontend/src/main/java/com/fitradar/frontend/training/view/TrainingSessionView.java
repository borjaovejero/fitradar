package com.fitradar.frontend.training.view;

import com.fitradar.frontend.shared.view.MainLayout;
import com.fitradar.frontend.training.dto.TrainingSessionRequest;
import com.fitradar.frontend.training.dto.TrainingSessionResponse;
import com.fitradar.frontend.training.service.TrainingSessionService;
import com.fitradar.frontend.wellness.dto.WellnessResponse;
import com.fitradar.frontend.wellness.service.WellnessService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@CssImport(value = "./styles/fitradar-dialog.css", themeFor = "vaadin-dialog-overlay")
@Route(value = "training", layout = MainLayout.class)
@PageTitle("Actividades | FitRadar")
public class TrainingSessionView extends VerticalLayout {

    private final TrainingSessionService trainingSessionService;
    private final WellnessService wellnessService;

    private final HorizontalLayout statsRow     = new HorizontalLayout();
    private final VerticalLayout   chartCard    = new VerticalLayout();
    private final VerticalLayout   sessionsList = new VerticalLayout();
    private final TextField        searchField  = new TextField();
    private final Select<String>   typeFilter   = new Select<>();

    private final String username;
    private final String password;

    private List<TrainingSessionResponse> allSessions = new ArrayList<>();

    private static final List<String> ENDURANCE = List.of("RUNNING", "CYCLING", "SWIMMING");
    private static final List<String> NO_HR     = List.of("GYM");

    public TrainingSessionView(TrainingSessionService trainingSessionService,
                               WellnessService wellnessService) {
        this.trainingSessionService = trainingSessionService;
        this.wellnessService        = wellnessService;

        this.username = (String) UI.getCurrent().getSession().getAttribute("username");
        this.password = (String) UI.getCurrent().getSession().getAttribute("password");

        if (username == null || password == null) {
            UI.getCurrent().navigate("");
            return;
        }

        setWidthFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.STRETCH);
        getStyle()
                .set("background", "#020817").set("padding", "32px 40px")
                .set("box-sizing", "border-box");

        add(buildHeader(), statsRow, chartCard, buildListCard());
        refreshSessions();
    }

    private HorizontalLayout buildHeader() {
        H2 title = new H2("Control de Actividades");
        title.getStyle().set("margin", "0").set("font-size", "2rem")
                .set("font-weight", "800").set("color", "white");

        Paragraph subtitle = new Paragraph("Registra y sigue el progreso de tus entrenamientos");
        subtitle.getStyle().set("margin", "4px 0 0 0").set("font-size", "1rem").set("color", "#94a3b8");

        VerticalLayout textBlock = new VerticalLayout(title, subtitle);
        textBlock.setPadding(false);
        textBlock.setSpacing(false);

        Button newButton = new Button("+ Nuevo entrenamiento");
        newButton.addClickListener(e -> openNewDialog());
        newButton.getStyle()
                .set("height", "48px").set("padding", "0 24px").set("border-radius", "14px")
                .set("background", "#10b981").set("color", "white").set("font-size", "1rem")
                .set("font-weight", "700").set("border", "none")
                .set("box-shadow", "0 8px 24px rgba(16,185,129,0.30)").set("cursor", "pointer");

        HorizontalLayout header = new HorizontalLayout(textBlock, newButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.getStyle().set("margin-bottom", "24px");
        return header;
    }

    private void renderStats() {
        statsRow.removeAll();
        statsRow.setWidthFull();
        statsRow.setSpacing(true);
        statsRow.getStyle().set("margin-bottom", "24px");

        int totalSessions = allSessions.size();
        int totalMinutes  = allSessions.stream().mapToInt(s -> safeInt(s.getDurationMinutes())).sum();
        int totalCalories = allSessions.stream().mapToInt(s -> safeInt(s.getCalories())).sum();

        statsRow.add(
                buildStatCard(String.valueOf(totalSessions), "Sesiones totales",  "#14e0a1", "rgba(20,224,161,0.10)"),
                buildStatCard(formatDuration(totalMinutes),  "Tiempo total",      "#60a5fa", "rgba(37,99,235,0.12)"),
                buildStatCard(String.valueOf(totalCalories), "Calorías quemadas", "#fb923c", "rgba(234,88,12,0.12)")
        );
    }

    private Div buildStatCard(String value, String label, String color, String bg) {
        H3 number = new H3(value);
        number.getStyle().set("margin", "0").set("font-size", "2.2rem").set("font-weight", "900")
                .set("color", color).set("text-align", "center");

        Paragraph text = new Paragraph(label);
        text.getStyle().set("margin", "4px 0 0 0").set("font-size", "0.9rem")
                .set("color", "#64748b").set("text-align", "center");

        Div card = new Div(number, text);
        card.getStyle()
                .set("flex", "1").set("padding", "24px").set("border-radius", "18px")
                .set("background", "#0f172a").set("border", "1px solid rgba(255,255,255,0.06)")
                .set("display", "flex").set("flex-direction", "column")
                .set("align-items", "center").set("justify-content", "center");
        return card;
    }

    private void renderChart() {
        chartCard.removeAll();
        chartCard.setWidthFull();
        chartCard.setPadding(true);
        chartCard.setSpacing(true);
        chartCard.getStyle()
                .set("background", "#0f172a").set("border", "1px solid rgba(255,255,255,0.06)")
                .set("border-radius", "18px").set("margin-bottom", "24px");

        H3 title = new H3("Minutos de entrenamiento esta semana");
        title.getStyle().set("margin", "0").set("font-size", "1.2rem")
                .set("font-weight", "700").set("color", "white");

        Div bars = new Div();
        bars.setWidthFull();
        bars.getStyle()
                .set("display", "flex").set("align-items", "flex-end")
                .set("justify-content", "space-between").set("gap", "8px").set("height", "160px")
                .set("overflow", "hidden");

        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        int maxMin = Math.max(60, allSessions.stream()
                .filter(s -> s.getSessionDate() != null
                        && !s.getSessionDate().isBefore(monday)
                        && !s.getSessionDate().isAfter(monday.plusDays(6)))
                .mapToInt(s -> safeInt(s.getDurationMinutes())).max().orElse(60));

        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            int mins = allSessions.stream()
                    .filter(s -> day.equals(s.getSessionDate()))
                    .mapToInt(s -> safeInt(s.getDurationMinutes())).sum();

            boolean isToday = day.equals(LocalDate.now());
            int height = mins == 0 ? 4 : Math.max(14, (int) ((mins / (double) maxMin) * 110));

            VerticalLayout col = new VerticalLayout();
            col.setPadding(false);
            col.setSpacing(false);
            col.setAlignItems(Alignment.CENTER);
            col.setJustifyContentMode(JustifyContentMode.END);
            col.setHeightFull();
            col.setWidth("100%");

            Div bar = new Div();
            bar.setWidth("100%");
            bar.setHeight(height + "px");
            bar.getStyle()
                    .set("background", mins > 0 ? "linear-gradient(180deg,#14e0a1,#0ea5a4)" : "rgba(255,255,255,0.06)")
                    .set("border-radius", "8px 8px 0 0");

            Paragraph dayLbl = new Paragraph(day.format(DateTimeFormatter.ofPattern("EEE", new Locale("es", "ES"))));
            dayLbl.getStyle().set("margin", "6px 0 0 0").set("font-size", "0.8rem")
                    .set("color", isToday ? "#14e0a1" : "#64748b")
                    .set("font-weight", isToday ? "700" : "400");

            col.add(bar, dayLbl);
            bars.add(col);
        }

        chartCard.add(title, bars);
    }

    private VerticalLayout buildListCard() {
        searchField.setPlaceholder("Buscar actividad...");
        searchField.setWidthFull();
        styleInput(searchField);
        searchField.addValueChangeListener(e -> renderSessionList());

        typeFilter.setItems("Todos los tipos", "RUNNING", "GYM", "FOOTBALL",
                "CYCLING", "SWIMMING", "TENNIS", "PADEL", "BASKETBALL", "OTHER");
        typeFilter.setItemLabelGenerator(this::typeLabel);
        typeFilter.setValue("Todos los tipos");
        typeFilter.setWidth("200px");
        styleInput(typeFilter);
        typeFilter.addValueChangeListener(e -> renderSessionList());

        HorizontalLayout filters = new HorizontalLayout(searchField, typeFilter);
        filters.setWidthFull();
        filters.setPadding(true);
        filters.setSpacing(true);
        filters.setAlignItems(Alignment.CENTER);
        filters.expand(searchField);

        sessionsList.setPadding(false);
        sessionsList.setSpacing(false);
        sessionsList.setWidthFull();

        VerticalLayout card = new VerticalLayout(filters, sessionsList);
        card.setPadding(false);
        card.setSpacing(false);
        card.setWidthFull();
        card.getStyle()
                .set("background", "#0f172a").set("border", "1px solid rgba(255,255,255,0.06)")
                .set("border-radius", "18px").set("overflow", "hidden");
        return card;
    }

    private void renderSessionList() {
        sessionsList.removeAll();
        String search = searchField.getValue() == null ? "" : searchField.getValue().trim().toLowerCase(Locale.ROOT);
        String type   = typeFilter.getValue();

        List<TrainingSessionResponse> filtered = allSessions.stream()
                .filter(s -> "Todos los tipos".equals(type) || type == null || type.equals(s.getTrainingType()))
                .filter(s -> search.isBlank()
                        || safe(s.getTitle()).toLowerCase(Locale.ROOT).contains(search)
                        || typeLabel(s.getTrainingType()).toLowerCase(Locale.ROOT).contains(search))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            Div empty = new Div();
            empty.setText("No hay entrenamientos que coincidan");
            empty.getStyle().set("padding", "36px").set("color", "#64748b")
                    .set("font-size", "1rem").set("text-align", "center");
            sessionsList.add(empty);
            return;
        }

        filtered.forEach(s -> sessionsList.add(buildSessionRow(s)));
    }

    private HorizontalLayout buildSessionRow(TrainingSessionResponse session) {
        Div typeTag = new Div();
        typeTag.setText(typeLabel(session.getTrainingType()));
        typeTag.getStyle()
                .set("width", "50px").set("height", "50px").set("border-radius", "14px")
                .set("background", "#1e293b").set("display", "flex").set("align-items", "center")
                .set("justify-content", "center").set("font-size", "0.7rem").set("font-weight", "700")
                .set("color", "#14e0a1").set("flex-shrink", "0").set("text-align", "center")
                .set("padding", "4px");

        String displayTitle = safe(session.getTitle()).isBlank()
                ? typeLabel(session.getTrainingType()) : session.getTitle();

        H3 title = new H3(displayTitle);
        title.getStyle().set("margin", "0").set("font-size", "1rem")
                .set("font-weight", "700").set("color", "white");

        String detailStr = formatDate(session.getSessionDate())
                + " · " + safeInt(session.getDurationMinutes()) + " min"
                + (session.getCalories() != null ? " · " + session.getCalories() + " kcal" : "");

        Paragraph details = new Paragraph(detailStr);
        details.getStyle().set("margin", "3px 0 0 0").set("font-size", "0.85rem").set("color", "#64748b");

        VerticalLayout info = new VerticalLayout(title, details);
        info.setPadding(false);
        info.setSpacing(false);

        Span badge = new Span(intensityLabel(session.getIntensityLevel()));
        badge.getStyle()
                .set("padding", "5px 12px").set("border-radius", "10px")
                .set("font-size", "0.85rem").set("font-weight", "700")
                .set("background", intensityBg(session.getIntensityLevel()))
                .set("color", intensityColor(session.getIntensityLevel()))
                .set("border", "1px solid " + intensityBorder(session.getIntensityLevel()))
                .set("white-space", "nowrap");

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addClickListener(e -> confirmDelete(session));
        deleteBtn.getStyle()
                .set("width", "36px").set("height", "36px").set("min-width", "36px").set("padding", "0")
                .set("border-radius", "10px").set("background", "rgba(251,113,133,0.10)")
                .set("color", "#fb7185").set("border", "1px solid rgba(251,113,133,0.25)").set("cursor", "pointer");

        Button detailBtn = new Button(VaadinIcon.CHEVRON_RIGHT.create());
        detailBtn.addClickListener(e -> openDetailDialog(session));
        detailBtn.getStyle()
                .set("width", "36px").set("height", "36px").set("min-width", "36px").set("padding", "0")
                .set("border-radius", "10px").set("background", "rgba(255,255,255,0.05)")
                .set("color", "#94a3b8").set("border", "1px solid rgba(255,255,255,0.08)").set("cursor", "pointer");

        HorizontalLayout actions = new HorizontalLayout(badge, deleteBtn, detailBtn);
        actions.setAlignItems(Alignment.CENTER);
        actions.setSpacing(true);

        HorizontalLayout row = new HorizontalLayout(typeTag, info, actions);
        row.setWidthFull();
        row.setPadding(true);
        row.setAlignItems(Alignment.CENTER);
        row.expand(info);
        row.getStyle().set("border-top", "1px solid rgba(255,255,255,0.05)").set("box-sizing", "border-box");
        return row;
    }

    private void openNewDialog() {
        try {
            boolean todayHasWellness = wellnessService
                    .getRecords(username, password)
                    .stream()
                    .anyMatch(r -> LocalDate.now().equals(r.getRecordDate()));
            if (!todayHasWellness) {
                Notification n = new Notification(
                        "No has registrado tu wellness hoy - el modelo ML sera menos preciso",
                        4000, Notification.Position.TOP_CENTER);
                n.getElement().getStyle()
                        .set("background", "#1e3a5f")
                        .set("color", "#60a5fa")
                        .set("font-weight", "600")
                        .set("border-radius", "12px")
                        .set("border", "1px solid rgba(96,165,250,0.30)");
                n.open();
            }
        } catch (Exception ignored) {}

        Dialog dialog = new Dialog();
        dialog.setWidth("680px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        H2 titleEl = new H2("Nuevo entrenamiento");
        titleEl.getStyle().set("margin", "0 0 16px 0").set("font-size", "1.8rem")
                .set("font-weight", "800").set("color", "white");

        TextField titleField   = field("Titulo");
        DatePicker dateField   = datePicker("Fecha");
        dateField.setValue(LocalDate.now());

        Select<String> typeField = select("Tipo de entrenamiento",
                List.of("RUNNING","GYM","FOOTBALL","CYCLING","SWIMMING","TENNIS","PADEL","BASKETBALL","OTHER"));
        typeField.setItemLabelGenerator(this::typeLabel);
        typeField.setValue("RUNNING");

        IntegerField durationField = intField("Duración (min)", 1, 600);
        durationField.setValue(45);

        IntegerField rpeField = intField("RPE (1-10)", 1, 10);
        rpeField.setValue(6);

        IntegerField caloriesField = intField("Calorías", 0, 9999);

        NumberField distanceField = numField("Distancia (km)");
        IntegerField hrField      = intField("FC media (bpm)", 40, 220);
        IntegerField maxHrField   = intField("FC maxima (bpm)", 40, 220);

        TextArea notesField = textArea("Notas", "Sensaciones, objetivo, molestias...");

        Paragraph tip = new Paragraph();
        tip.getStyle().set("margin", "0").set("font-size", "0.82rem").set("color", "#60a5fa");

        HorizontalLayout distanceRow = row2(distanceField, new Div());
        HorizontalLayout hrRow       = row2(hrField, maxHrField);

        updateFieldVisibility(typeField.getValue(), distanceField, distanceRow, hrRow, hrField, maxHrField, tip);
        typeField.addValueChangeListener(e ->
                updateFieldVisibility(e.getValue(), distanceField, distanceRow, hrRow, hrField, maxHrField, tip));

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());
        cancelBtn.getStyle()
                .set("background", "transparent").set("color", "#94a3b8")
                .set("border", "1px solid #334155").set("cursor", "pointer");

        Button saveBtn = new Button("Guardar entrenamiento", e -> {
            try {
                if (safe(titleField.getValue()).isBlank()) {
                    Notification.show("El titulo es obligatorio", 3000, Notification.Position.MIDDLE); return;
                }
                if (dateField.getValue() == null) {
                    Notification.show("La fecha es obligatoria", 3000, Notification.Position.MIDDLE); return;
                }
                if (durationField.getValue() == null || durationField.getValue() <= 0) {
                    Notification.show("La duración es obligatoria", 3000, Notification.Position.MIDDLE); return;
                }
                TrainingSessionRequest req = buildRequest(titleField, dateField, typeField,
                        durationField, rpeField, distanceField, caloriesField, hrField, maxHrField, notesField);
                trainingSessionService.createSession(username, password, req);
                Notification.show("Entrenamiento guardado", 3000, Notification.Position.MIDDLE);
                dialog.close();
                refreshSessions();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        saveBtn.getStyle()
                .set("background", "#10b981").set("color", "white")
                .set("font-weight", "700").set("cursor", "pointer");

        HorizontalLayout btnRow = new HorizontalLayout(cancelBtn, saveBtn);
        btnRow.setWidthFull();
        btnRow.setJustifyContentMode(JustifyContentMode.END);
        btnRow.getStyle().set("margin-top", "8px");

        VerticalLayout content = new VerticalLayout(
                titleEl,
                row2(titleField, dateField),
                row2(typeField, durationField),
                row2(rpeField, caloriesField),
                distanceRow, hrRow, tip,
                notesField, btnRow
        );
        content.setPadding(false);
        content.setSpacing(true);
        content.getStyle()
                .set("background", "#0f172a").set("color", "white")
                .set("padding", "28px").set("box-sizing", "border-box");

        dialog.add(content);
        dialog.open();
    }

    private void updateFieldVisibility(String type,
                                       NumberField distanceField,
                                       HorizontalLayout distanceRow,
                                       HorizontalLayout hrRow,
                                       IntegerField hrField,
                                       IntegerField maxHrField,
                                       Paragraph tip) {
        boolean showDistance = ENDURANCE.contains(type);
        boolean showHr       = !NO_HR.contains(type);

        distanceRow.setVisible(showDistance);
        hrRow.setVisible(showHr);

        if (!showDistance) distanceField.setValue(null);
        if (!showHr) { hrField.setValue(null); maxHrField.setValue(null); }

        String tipText = switch (type != null ? type : "") {
            case "GYM"        -> "Gimnasio: registra series, cargas y RPE en las notas.";
            case "RUNNING"    -> "Carrera: incluye la distancia recorrida y el ritmo medio.";
            case "CYCLING"    -> "Ciclismo: registra distancia, desnivel y vatios si los tienes.";
            case "SWIMMING"   -> "Natación: registra distancia y estilos en las notas.";
            case "FOOTBALL"   -> "Futbol: la FC media es el mejor indicador de carga.";
            case "BASKETBALL" -> "Baloncesto: valora el esfuerzo con el RPE.";
            case "TENNIS"     -> "Tenis: anota el resultado si quieres en las notas.";
            case "PADEL"      -> "Pádel: la duración del partido y RPE son suficientes.";
            default           -> "";
        };
        tip.setText(tipText);
        tip.setVisible(!tipText.isEmpty());
    }

    private void openDetailDialog(TrainingSessionResponse session) {
        Dialog dialog = new Dialog();
        dialog.setWidth("680px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        H2 titleEl = new H2(safe(session.getTitle()).isBlank()
                ? typeLabel(session.getTrainingType()) : session.getTitle());
        titleEl.getStyle().set("margin", "0 0 16px 0").set("font-size", "1.6rem")
                .set("font-weight", "800").set("color", "white");

        TextField titleField   = field("Titulo");
        titleField.setValue(safe(session.getTitle()));

        DatePicker dateField = datePicker("Fecha");
        if (session.getSessionDate() != null) dateField.setValue(session.getSessionDate());

        Select<String> typeField = select("Tipo",
                List.of("RUNNING","GYM","FOOTBALL","CYCLING","SWIMMING","TENNIS","PADEL","BASKETBALL","OTHER"));
        typeField.setItemLabelGenerator(this::typeLabel);
        typeField.setValue(safe(session.getTrainingType()).isBlank() ? "RUNNING" : session.getTrainingType());

        IntegerField durationField = intField("Duracion (min)", 1, 600);
        durationField.setValue(session.getDurationMinutes() != null ? session.getDurationMinutes() : 45);

        IntegerField rpeField = intField("RPE (1-10)", 1, 10);
        rpeField.setValue(session.getRpe() != null ? session.getRpe() : 6);

        IntegerField caloriesField = intField("Calorias", 0, 9999);
        if (session.getCalories() != null) caloriesField.setValue(session.getCalories());

        NumberField distanceField = numField("Distancia (km)");
        if (session.getDistanceKm() != null) distanceField.setValue(session.getDistanceKm());

        IntegerField hrField = intField("FC media (bpm)", 40, 220);
        if (session.getAverageHeartRate() != null) hrField.setValue(session.getAverageHeartRate());

        IntegerField maxHrField = intField("FC maxima (bpm)", 40, 220);
        if (session.getMaxHeartRate() != null) maxHrField.setValue(session.getMaxHeartRate());

        TextArea notesField = textArea("Notas", "Sensaciones, objetivo, molestias...");
        notesField.setValue(safe(session.getNotes()));

        Paragraph tip = new Paragraph();
        tip.getStyle().set("margin", "0").set("font-size", "0.82rem").set("color", "#60a5fa");

        HorizontalLayout distanceRow = row2(distanceField, new Div());
        HorizontalLayout hrRow       = row2(hrField, maxHrField);

        updateFieldVisibility(typeField.getValue(), distanceField, distanceRow, hrRow, hrField, maxHrField, tip);
        typeField.addValueChangeListener(e ->
                updateFieldVisibility(e.getValue(), distanceField, distanceRow, hrRow, hrField, maxHrField, tip));

        Div mlCard = buildMlCard(session);

        Button cancelBtn = new Button("Cerrar", e -> dialog.close());
        cancelBtn.getStyle()
                .set("background", "transparent").set("color", "#94a3b8")
                .set("border", "1px solid #334155").set("cursor", "pointer");

        Button saveBtn = new Button("Guardar cambios", e -> {
            try {
                if (durationField.getValue() == null || durationField.getValue() <= 0) {
                    Notification.show("La duración es obligatoria", 3000, Notification.Position.MIDDLE); return;
                }
                TrainingSessionRequest req = buildRequest(titleField, dateField, typeField,
                        durationField, rpeField, distanceField, caloriesField, hrField, maxHrField, notesField);
                trainingSessionService.updateSession(username, password, session.getId(), req);
                Notification.show("Entrenamiento actualizado", 3000, Notification.Position.MIDDLE);
                dialog.close();
                refreshSessions();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        saveBtn.getStyle()
                .set("background", "#10b981").set("color", "white")
                .set("font-weight", "700").set("cursor", "pointer");

        HorizontalLayout btnRow = new HorizontalLayout(cancelBtn, saveBtn);
        btnRow.setWidthFull();
        btnRow.setJustifyContentMode(JustifyContentMode.END);
        btnRow.getStyle().set("margin-top", "8px");

        VerticalLayout content = new VerticalLayout(
                titleEl,
                row2(titleField, dateField),
                row2(typeField, durationField),
                row2(rpeField, caloriesField),
                distanceRow, hrRow, tip,
                notesField, mlCard, btnRow
        );
        content.setPadding(false);
        content.setSpacing(true);
        content.getStyle()
                .set("background", "#0f172a").set("color", "white")
                .set("padding", "28px").set("box-sizing", "border-box");

        dialog.add(content);
        dialog.open();
    }

    private Div buildMlCard(TrainingSessionResponse s) {
        String acwrStr  = s.getAcwr()        != null ? String.format(Locale.US, "%.2f", s.getAcwr()) : "-";
        String loadStr  = s.getSessionLoad() != null ? s.getSessionLoad().toString() : "-";
        String acuteStr = s.getAcuteLoad7d() != null ? String.format(Locale.US,"%.0f",s.getAcuteLoad7d()) : "-";

        Div card = new Div();
        card.getStyle()
                .set("background", "rgba(20,224,161,0.06)").set("border", "1px solid rgba(20,224,161,0.15)")
                .set("border-radius", "14px").set("padding", "14px 16px");
        card.getElement().setProperty("innerHTML",
                "<div style='font-size:0.85rem;color:#14e0a1;font-weight:700;margin-bottom:10px'>Datos del modelo ML</div>" +
                        "<div style='display:flex;gap:28px;flex-wrap:wrap;font-size:0.9rem;color:#94a3b8'>" +
                        "<span>Carga sesión: <b style='color:white'>" + loadStr + "</b></span>" +
                        "<span>Carga aguda 7d: <b style='color:white'>" + acuteStr + "</b></span>" +
                        "<span>ACWR: <b style='color:" + acwrColor(s.getAcwr()) + "'>" + acwrStr + "</b></span>" +
                        "</div>"
        );
        return card;
    }

    private void confirmDelete(TrainingSessionResponse session) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Borrar entrenamiento");
        dialog.setText("Se eliminará \"" + safe(session.getTitle()) + "\". Esta acción no se puede deshacer.");
        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");
        dialog.setConfirmText("Borrar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            try {
                trainingSessionService.deleteSession(username, password, session.getId());
                Notification.show("Entrenamiento eliminado", 3000, Notification.Position.MIDDLE);
                refreshSessions();
            } catch (Exception ex) {
                Notification.show("Error al eliminar: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        dialog.open();
    }

    private void refreshSessions() {
        try {
            allSessions = trainingSessionService.getSessions(username, password)
                    .stream()
                    .sorted(Comparator.comparing(TrainingSessionResponse::getSessionDate,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
            renderStats();
            renderChart();
            renderSessionList();
        } catch (Exception e) {
            Notification.show("No se pudieron cargar las actividades", 4000, Notification.Position.MIDDLE);
        }
    }

    private TrainingSessionRequest buildRequest(
            TextField titleF, DatePicker dateF, Select<String> typeF,
            IntegerField durF, IntegerField rpeF, NumberField distF,
            IntegerField calF, IntegerField hrF, IntegerField maxHrF, TextArea notesF) {

        TrainingSessionRequest req = new TrainingSessionRequest();
        req.setTitle(titleF.getValue());
        req.setSessionDate(dateF.getValue());
        req.setTrainingType(typeF.getValue());
        req.setDurationMinutes(durF.getValue());
        req.setRpe(rpeF.getValue());
        if (distF.isVisible() && distF.getValue() != null)   req.setDistanceKm(distF.getValue());
        if (calF.getValue() != null)                         req.setCalories(calF.getValue());
        if (hrF.isVisible() && hrF.getValue() != null)       req.setAverageHeartRate(hrF.getValue());
        if (maxHrF.isVisible() && maxHrF.getValue() != null) req.setMaxHeartRate(maxHrF.getValue());
        req.setNotes(notesF.getValue());
        return req;
    }

    private TextField field(String label) {
        TextField f = new TextField(label); f.setWidthFull(); styleInput(f); return f;
    }

    private DatePicker datePicker(String label) {
        DatePicker f = new DatePicker(label); f.setWidthFull(); styleInput(f); return f;
    }

    private Select<String> select(String label, List<String> items) {
        Select<String> f = new Select<>(); f.setLabel(label); f.setItems(items);
        f.setWidthFull(); styleInput(f); return f;
    }

    private IntegerField intField(String label, int min, int max) {
        IntegerField f = new IntegerField(label); f.setMin(min); f.setMax(max);
        f.setWidthFull(); styleInput(f); return f;
    }

    private NumberField numField(String label) {
        NumberField f = new NumberField(label); f.setMin(0); f.setStep(0.1);
        f.setWidthFull(); styleInput(f); return f;
    }

    private TextArea textArea(String label, String placeholder) {
        TextArea f = new TextArea(label); f.setPlaceholder(placeholder);
        f.setWidthFull(); f.setMinHeight("80px"); styleInput(f); return f;
    }

    private HorizontalLayout row2(Component a, Component b) {
        HorizontalLayout r = new HorizontalLayout(a, b); r.setWidthFull(); r.setSpacing(true); return r;
    }

    private void styleInput(Component c) {
        c.getElement().getStyle()
                .set("--vaadin-input-field-background", "#1e293b")
                .set("--vaadin-input-field-border-radius", "12px")
                .set("--vaadin-input-field-border-color", "#334155")
                .set("--vaadin-input-field-value-color", "white")
                .set("--vaadin-input-field-label-color", "#94a3b8")
                .set("--vaadin-input-field-placeholder-color", "#475569")
                .set("color", "white");
    }

    private String acwrColor(Double acwr) {
        if (acwr == null) return "#94a3b8";
        if (acwr > 1.5)  return "#fb7185";
        if (acwr > 1.3)  return "#fb923c";
        if (acwr > 0.8)  return "#14e0a1";
        return "#facc15";
    }

    private String typeLabel(String v) {
        return switch (safe(v)) {
            case "RUNNING"    -> "Carrera";    case "GYM"        -> "Gimnasio";
            case "FOOTBALL"   -> "Futbol";     case "CYCLING"    -> "Ciclismo";
            case "SWIMMING"   -> "Natación";   case "TENNIS"     -> "Tenis";
            case "PADEL"      -> "Pádel";      case "BASKETBALL" -> "Baloncesto";
            case "Todos los tipos" -> "Todos los tipos"; default -> "Otro";
        };
    }

    private String intensityLabel(String v) {
        return switch (safe(v)) { case "LOW" -> "Baja"; case "HIGH" -> "Alta"; default -> "Media"; };
    }

    private String intensityColor(String v) {
        return switch (safe(v)) {
            case "LOW" -> "#14e0a1"; case "HIGH" -> "#fb7185"; default -> "#facc15";
        };
    }

    private String intensityBg(String v) {
        return switch (safe(v)) {
            case "LOW" -> "rgba(20,224,161,0.10)"; case "HIGH" -> "rgba(251,113,133,0.10)";
            default -> "rgba(250,204,21,0.10)";
        };
    }

    private String intensityBorder(String v) {
        return switch (safe(v)) {
            case "LOW" -> "rgba(20,224,161,0.30)"; case "HIGH" -> "rgba(251,113,133,0.30)";
            default -> "rgba(250,204,21,0.30)";
        };
    }

    private String formatDuration(int mins) {
        int h = mins / 60, m = mins % 60;
        if (h == 0) return m + "m"; if (m == 0) return h + "h"; return h + "h " + m + "m";
    }

    private String formatDate(LocalDate d) {
        if (d == null) return "-";
        return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private int safeInt(Integer v)  { return v != null ? v : 0; }
    private String safe(String v)   { return v != null ? v : ""; }
}