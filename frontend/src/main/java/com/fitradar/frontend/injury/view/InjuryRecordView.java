package com.fitradar.frontend.injury.view;

import com.fitradar.frontend.injury.dto.InjuryRecordRequest;
import com.fitradar.frontend.injury.dto.InjuryRecordResponse;
import com.fitradar.frontend.injury.service.InjuryRecordService;
import com.fitradar.frontend.shared.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Route(value = "injury", layout = MainLayout.class)
@PageTitle("Lesiones | FitRadar")
public class InjuryRecordView extends VerticalLayout {

    private final InjuryRecordService injuryRecordService;
    private final VerticalLayout   recordsList = new VerticalLayout();
    private final HorizontalLayout statsRow    = new HorizontalLayout();

    private final String username;
    private final String password;

    public InjuryRecordView(InjuryRecordService injuryRecordService) {
        this.injuryRecordService = injuryRecordService;
        this.username = (String) UI.getCurrent().getSession().getAttribute("username");
        this.password = (String) UI.getCurrent().getSession().getAttribute("password");

        if (username == null || password == null) {
            UI.getCurrent().navigate("");
            return;
        }

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.STRETCH);
        getStyle()
                .set("background", "#020817")
                .set("padding", "32px 40px")
                .set("box-sizing", "border-box")
                .set("overflow-y", "auto");

        add(buildHeader(), statsRow, buildListCard());
        refreshRecords();
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private HorizontalLayout buildHeader() {
        H2 title = new H2("Control de Lesiones");
        title.getStyle()
                .set("margin", "0").set("font-size", "2rem")
                .set("font-weight", "800").set("color", "white");

        Paragraph subtitle = new Paragraph("Seguimiento y recuperación de lesiones deportivas");
        subtitle.getStyle()
                .set("margin", "4px 0 0 0").set("font-size", "1rem")
                .set("color", "#94a3b8");

        VerticalLayout textBlock = new VerticalLayout(title, subtitle);
        textBlock.setPadding(false);
        textBlock.setSpacing(false);

        Button registerButton = new Button("+ Registrar lesión");
        registerButton.addClickListener(e -> openDialog(null));
        registerButton.getStyle()
                .set("height", "48px").set("padding", "0 24px")
                .set("border-radius", "14px").set("background", "#fb7185")
                .set("color", "white").set("font-size", "1rem")
                .set("font-weight", "700").set("border", "none")
                .set("box-shadow", "0 8px 24px rgba(251,113,133,0.30)")
                .set("cursor", "pointer");

        HorizontalLayout header = new HorizontalLayout(textBlock, registerButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.getStyle().set("margin-bottom", "24px");
        return header;
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    private void renderStats(List<InjuryRecordResponse> records) {
        statsRow.removeAll();
        statsRow.setWidthFull();
        statsRow.setSpacing(true);
        statsRow.getStyle().set("margin-bottom", "24px");

        long active    = records.stream().filter(r -> !r.isFullyRecovered()).count();
        long recovered = records.stream().filter(InjuryRecordResponse::isFullyRecovered).count();
        long reported  = records.stream().filter(InjuryRecordResponse::isReportedToModel).count();

        statsRow.add(
                buildStatCard(String.valueOf(active),    "Activas",            "#fb7185", "rgba(251,113,133,0.10)"),
                buildStatCard(String.valueOf(recovered), "Recuperadas",        "#14e0a1", "rgba(20,224,161,0.10)"),
                buildStatCard(String.valueOf(reported),  "Enviadas al modelo", "#60a5fa", "rgba(96,165,250,0.10)")
        );
    }

    private Div buildStatCard(String value, String label, String color, String bg) {
        H3 number = new H3(value);
        number.getStyle()
                .set("margin", "0").set("font-size", "2.2rem").set("font-weight", "900")
                .set("color", color).set("text-align", "center");

        Paragraph text = new Paragraph(label);
        text.getStyle()
                .set("margin", "4px 0 0 0").set("font-size", "0.9rem")
                .set("font-weight", "600").set("color", color)
                .set("text-align", "center");

        Div card = new Div(number, text);
        card.getStyle()
                .set("flex", "1").set("padding", "22px")
                .set("border-radius", "18px").set("background", bg)
                .set("border", "1px solid " + color + "44");
        return card;
    }

    // ── Lista ─────────────────────────────────────────────────────────────────

    private VerticalLayout buildListCard() {
        recordsList.setPadding(false);
        recordsList.setSpacing(false);
        recordsList.setWidthFull();

        VerticalLayout card = new VerticalLayout(recordsList);
        card.setPadding(false);
        card.setSpacing(false);
        card.setWidthFull();
        card.getStyle()
                .set("background", "#0f172a")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("border-radius", "18px").set("overflow", "hidden");
        return card;
    }

    private void refreshRecords() {
        recordsList.removeAll();
        statsRow.removeAll();

        try {
            List<InjuryRecordResponse> records = injuryRecordService
                    .getRecords(username, password)
                    .stream()
                    .sorted(Comparator.comparing(
                            InjuryRecordResponse::getStartDate,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            renderStats(records);

            if (records.isEmpty()) {
                Div empty = new Div();
                empty.setText("No tienes lesiones registradas. Sigue así!");
                empty.getStyle()
                        .set("padding", "40px").set("color", "#64748b")
                        .set("font-size", "1rem").set("text-align", "center");
                recordsList.add(empty);
                return;
            }

            records.forEach(r -> recordsList.add(buildRow(r)));

        } catch (Exception e) {
            Notification.show("No se pudieron cargar las lesiones", 4000, Notification.Position.MIDDLE);
        }
    }

    private HorizontalLayout buildRow(InjuryRecordResponse record) {
        Div zoneTag = new Div();
        zoneTag.setText(bodyZoneLabel(record.getBodyZone()));
        zoneTag.getStyle()
                .set("width", "50px").set("height", "50px").set("border-radius", "14px")
                .set("background", record.isFullyRecovered()
                        ? "rgba(20,224,161,0.10)" : "rgba(251,113,133,0.10)")
                .set("display", "flex").set("align-items", "center")
                .set("justify-content", "center").set("font-size", "0.6rem")
                .set("font-weight", "700").set("color", record.isFullyRecovered()
                        ? "#14e0a1" : "#fb7185")
                .set("flex-shrink", "0").set("text-align", "center").set("padding", "4px");

        H3 title = new H3(bodyZoneLabel(record.getBodyZone())
                + " - " + injuryTypeLabel(record.getInjuryType()));
        title.getStyle()
                .set("margin", "0").set("font-size", "1rem")
                .set("font-weight", "700").set("color", "white");

        String detailStr = "Desde " + formatDate(record.getStartDate())
                + " · " + severityLabel(record.getInjurySeverity());
        if (record.getDaysOff() != null && record.getDaysOff() > 0) {
            detailStr += " · " + record.getDaysOff() + " dias de baja";
        }
        if (record.isRecurrence()) {
            detailStr += " · Recaída";
        }

        Paragraph details = new Paragraph(detailStr);
        details.getStyle()
                .set("margin", "3px 0 0 0").set("font-size", "0.85rem").set("color", "#64748b");

        VerticalLayout info = new VerticalLayout(title, details);
        info.setPadding(false);
        info.setSpacing(false);

        Span badge = new Span(statusLabel(record));
        badge.getStyle()
                .set("padding", "5px 12px").set("border-radius", "10px")
                .set("font-size", "0.85rem").set("font-weight", "700")
                .set("background", statusBg(record))
                .set("color", statusColor(record))
                .set("border", "1px solid " + statusBorder(record))
                .set("white-space", "nowrap");

        if (record.isReportedToModel()) {
            Span mlBadge = new Span("ML");
            mlBadge.getStyle()
                    .set("padding", "5px 10px").set("border-radius", "10px")
                    .set("font-size", "0.8rem").set("font-weight", "700")
                    .set("background", "rgba(96,165,250,0.10)")
                    .set("color", "#60a5fa")
                    .set("border", "1px solid rgba(96,165,250,0.25)")
                    .set("white-space", "nowrap");
        }

        Button editBtn = new Button(VaadinIcon.EDIT.create());
        editBtn.addClickListener(e -> openDialog(record));
        editBtn.getStyle()
                .set("width", "36px").set("height", "36px").set("min-width", "36px")
                .set("padding", "0").set("border-radius", "10px")
                .set("background", "rgba(96,165,250,0.10)").set("color", "#60a5fa")
                .set("border", "1px solid rgba(96,165,250,0.25)").set("cursor", "pointer");

        Button deleteBtn = new Button(VaadinIcon.TRASH.create());
        deleteBtn.addClickListener(e -> confirmDelete(record));
        deleteBtn.getStyle()
                .set("width", "36px").set("height", "36px").set("min-width", "36px")
                .set("padding", "0").set("border-radius", "10px")
                .set("background", "rgba(251,113,133,0.10)").set("color", "#fb7185")
                .set("border", "1px solid rgba(251,113,133,0.25)").set("cursor", "pointer");

        HorizontalLayout actions = new HorizontalLayout(badge, editBtn, deleteBtn);
        actions.setAlignItems(Alignment.CENTER);
        actions.setSpacing(true);

        HorizontalLayout row = new HorizontalLayout(zoneTag, info, actions);
        row.setWidthFull();
        row.setPadding(true);
        row.setAlignItems(Alignment.CENTER);
        row.expand(info);
        row.getStyle()
                .set("border-top", "1px solid rgba(255,255,255,0.05)")
                .set("box-sizing", "border-box");
        return row;
    }

    // ── Dialogo crear/editar ──────────────────────────────────────────────────

    private void openDialog(InjuryRecordResponse existing) {
        boolean editing = existing != null;

        Dialog dialog = new Dialog();
        dialog.setWidth("660px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        H2 titleEl = new H2(editing ? "Editar lesión" : "Registrar lesión");
        titleEl.getStyle()
                .set("margin", "0 0 16px 0").set("font-size", "1.7rem")
                .set("font-weight", "800").set("color", "white");

        Select<String> bodyZoneField = select("Zona corporal", List.of(
                "NECK","SHOULDER","ARM","ELBOW","WRIST",
                "BACK","LUMBAR","HIP","GLUTE",
                "QUADRICEPS","HAMSTRING","KNEE","CALF","ANKLE","FOOT","OTHER"));
        bodyZoneField.setItemLabelGenerator(this::bodyZoneLabel);
        bodyZoneField.setValue(editing ? existing.getBodyZone() : "KNEE");

        Select<String> typeField = select("Tipo de lesión",
                List.of("MUSCULAR","TENDON","LIGAMENT","JOINT","BONE","OTHER"));
        typeField.setItemLabelGenerator(this::injuryTypeLabel);
        typeField.setValue(editing ? existing.getInjuryType() : "MUSCULAR");

        Select<String> severityField = select("Gravedad",
                List.of("LOW","MEDIUM","HIGH"));
        severityField.setItemLabelGenerator(this::severityLabel);
        severityField.setValue(editing ? existing.getInjurySeverity() : "LOW");

        DatePicker startField = datePicker("Fecha de inicio");
        startField.setValue(editing && existing.getStartDate() != null
                ? existing.getStartDate() : LocalDate.now());

        DatePicker endField = datePicker("Fecha de fin (si ya sabes)");
        if (editing && existing.getEndDate() != null) endField.setValue(existing.getEndDate());

        Checkbox recurrenceChk = new Checkbox("Recaída de lesión anterior");
        recurrenceChk.setValue(editing && existing.isRecurrence());
        recurrenceChk.getStyle().set("color", "#cbd5e1");

        Checkbox recoveredChk = new Checkbox("Ya totalmente recuperado");
        recoveredChk.setValue(editing && existing.isFullyRecovered());
        recoveredChk.getStyle().set("color", "#cbd5e1");

        TextArea descField = new TextArea("Descripcion");
        descField.setPlaceholder("Como ocurrio? Que sintomas tienes?");
        descField.setWidthFull();
        descField.setMinHeight("100px");
        styleInput(descField);
        if (editing) descField.setValue(safe(existing.getDescription()));

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());
        cancelBtn.getStyle()
                .set("background", "transparent").set("color", "#94a3b8")
                .set("border", "1px solid #334155").set("cursor", "pointer");

        Button saveBtn = new Button(editing ? "Guardar cambios" : "Registrar lesión", e -> {
            try {
                if (bodyZoneField.getValue() == null || typeField.getValue() == null
                        || severityField.getValue() == null || startField.getValue() == null) {
                    Notification.show("Completa zona, tipo, gravedad y fecha",
                            3000, Notification.Position.MIDDLE);
                    return;
                }

                InjuryRecordRequest req = new InjuryRecordRequest();
                req.setBodyZone(bodyZoneField.getValue());
                req.setInjuryType(typeField.getValue());
                req.setInjurySeverity(severityField.getValue());
                req.setStartDate(startField.getValue());
                req.setEndDate(endField.getValue());
                req.setRecurrence(recurrenceChk.getValue());
                req.setFullyRecovered(recoveredChk.getValue());
                req.setDescription(descField.getValue());

                if (editing) {
                    injuryRecordService.updateRecord(username, password, existing.getId(), req);
                    Notification.show("Lesión actualizada", 3000, Notification.Position.MIDDLE);
                } else {
                    injuryRecordService.createRecord(username, password, req);
                    Notification.show("Lesión registrada", 3000, Notification.Position.MIDDLE);
                }

                dialog.close();
                refreshRecords();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        saveBtn.getStyle()
                .set("background", "#fb7185").set("color", "white")
                .set("font-weight", "700").set("cursor", "pointer");

        HorizontalLayout btnRow = new HorizontalLayout(cancelBtn, saveBtn);
        btnRow.setWidthFull();
        btnRow.setJustifyContentMode(JustifyContentMode.END);
        btnRow.getStyle().set("margin-top", "8px");

        HorizontalLayout checkRow = new HorizontalLayout(recurrenceChk, recoveredChk);
        checkRow.setWidthFull();
        checkRow.setSpacing(true);

        VerticalLayout content = new VerticalLayout(
                titleEl,
                row2(bodyZoneField, typeField),
                row2(severityField, startField),
                endField,
                descField,
                checkRow,
                btnRow
        );
        content.setPadding(false);
        content.setSpacing(true);
        content.getStyle()
                .set("background", "#0f172a").set("color", "white")
                .set("padding", "28px").set("box-sizing", "border-box");

        dialog.add(content);
        dialog.open();
    }

    // ── Borrar ────────────────────────────────────────────────────────────────

    private void confirmDelete(InjuryRecordResponse record) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Borrar lesión");
        dialog.setText("Se eliminará la lesión de " + bodyZoneLabel(record.getBodyZone())
                + ". Esta acción no se puede deshacer.");
        dialog.setCancelable(true);
        dialog.setCancelText("Cancelar");
        dialog.setConfirmText("Borrar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            try {
                injuryRecordService.deleteRecord(username, password, record.getId());
                Notification.show("Lesión eliminada", 3000, Notification.Position.MIDDLE);
                refreshRecords();
            } catch (Exception ex) {
                Notification.show("Error al eliminar: " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });
        dialog.open();
    }

    // ── Helpers de formulario ─────────────────────────────────────────────────

    private Select<String> select(String label, List<String> items) {
        Select<String> f = new Select<>();
        f.setLabel(label);
        f.setItems(items);
        f.setWidthFull();
        styleInput(f);
        return f;
    }

    private DatePicker datePicker(String label) {
        DatePicker f = new DatePicker(label);
        f.setWidthFull();
        styleInput(f);
        return f;
    }

    private HorizontalLayout row2(Component a, Component b) {
        HorizontalLayout r = new HorizontalLayout(a, b);
        r.setWidthFull();
        r.setSpacing(true);
        return r;
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

    // ── Helpers de estado ─────────────────────────────────────────────────────

    private String statusLabel(InjuryRecordResponse r) {
        if (r.isFullyRecovered()) return "Recuperada";
        return r.getEndDate() != null ? "En recuperación" : "Activa";
    }

    private String statusColor(InjuryRecordResponse r) {
        if (r.isFullyRecovered()) return "#14e0a1";
        return r.getEndDate() != null ? "#facc15" : "#fb7185";
    }

    private String statusBg(InjuryRecordResponse r) {
        if (r.isFullyRecovered()) return "rgba(20,224,161,0.10)";
        return r.getEndDate() != null ? "rgba(250,204,21,0.10)" : "rgba(251,113,133,0.10)";
    }

    private String statusBorder(InjuryRecordResponse r) {
        if (r.isFullyRecovered()) return "rgba(20,224,161,0.30)";
        return r.getEndDate() != null ? "rgba(250,204,21,0.30)" : "rgba(251,113,133,0.30)";
    }

    // ── Labels ────────────────────────────────────────────────────────────────

    private String injuryTypeLabel(String v) {
        return switch (safe(v)) {
            case "MUSCULAR"  -> "Muscular";
            case "TENDON"    -> "Tendón";
            case "LIGAMENT"  -> "Ligamento";
            case "JOINT"     -> "Articular";
            case "BONE"      -> "Ósea";
            default          -> "Otra";
        };
    }

    private String bodyZoneLabel(String v) {
        return switch (safe(v)) {
            case "NECK"       -> "Cuello";
            case "SHOULDER"   -> "Hombro";
            case "ARM"        -> "Brazo";
            case "ELBOW"      -> "Codo";
            case "WRIST"      -> "Muñeca";
            case "BACK"       -> "Espalda";
            case "LUMBAR"     -> "Zona lumbar";
            case "HIP"        -> "Cadera";
            case "GLUTE"      -> "Glúteo";
            case "QUADRICEPS" -> "Cuádriceps";
            case "HAMSTRING"  -> "Isquiotibiales";
            case "KNEE"       -> "Rodilla";
            case "CALF"       -> "Gemelo";
            case "ANKLE"      -> "Tobillo";
            case "FOOT"       -> "Pie";
            default           -> "Otra zona";
        };
    }

    private String severityLabel(String v) {
        return switch (safe(v)) {
            case "LOW"    -> "Leve";
            case "MEDIUM" -> "Moderada";
            case "HIGH"   -> "Grave";
            default       -> "-";
        };
    }

    private String formatDate(LocalDate d) {
        if (d == null) return "-";
        return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "ES")));
    }

    private String safe(String v) { return v != null ? v : ""; }
}