package com.fitradar.frontend.user.view;

import com.fitradar.frontend.shared.view.MainLayout;
import com.fitradar.frontend.user.dto.UpdateUserRequest;
import com.fitradar.frontend.user.dto.UserResponse;
import com.fitradar.frontend.user.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Editar perfil | FitRadar")
public class ProfileView extends VerticalLayout {

    private final AuthService authService;

    private final EmailField    emailField        = styledEmail("Correo electrónico");
    private final TextField     firstNameField    = styledText("Nombre");
    private final TextField     lastNameField     = styledText("Apellidos");
    private final DatePicker    birthDateField    = styledDate("Fecha de nacimiento");
    private final Select<String> sexField         = styledSelect("Sexo",
            "MALE", "FEMALE", "OTHER");
    private final NumberField   heightField       = styledNumber("Altura (cm)", 100, 250);
    private final NumberField   weightField       = styledNumber("Peso (kg)", 30, 300);
    private final Select<String> sportField       = styledSelect("Deporte principal",
            "RUNNING", "FOOTBALL", "GYM", "CYCLING",
            "BASKETBALL", "TENNIS", "PADEL", "SWIMMING", "OTHER");
    private final Select<String> levelField       = styledSelect("Nivel",
            "BEGINNER", "INTERMEDIATE", "ADVANCED");
    private final NumberField   trainingDaysField = styledNumber("Días de entrenamiento / semana", 1, 7);
    private final TextArea      observationsField = styledTextArea("Observaciones");

    public ProfileView(AuthService authService) {
        this.authService = authService;

        // Labels en español para los selects
        sexField.setItemLabelGenerator(v -> switch (v) {
            case "MALE"   -> "Masculino";
            case "FEMALE" -> "Femenino";
            default       -> "Otro";
        });

        sportField.setItemLabelGenerator(v -> switch (v) {
            case "RUNNING"    -> "Atletismo";
            case "FOOTBALL"   -> "Fútbol";
            case "GYM"        -> "Gimnasio";
            case "CYCLING"    -> "Ciclismo";
            case "BASKETBALL" -> "Baloncesto";
            case "TENNIS"     -> "Tenis";
            case "PADEL"      -> "Pádel";
            case "SWIMMING"   -> "Natación";
            default           -> "Otro";
        });

        levelField.setItemLabelGenerator(v -> switch (v) {
            case "BEGINNER"     -> "Principiante";
            case "INTERMEDIATE" -> "Intermedio";
            default             -> "Avanzado";
        });

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#020817").set("overflow-y", "auto");

        VerticalLayout container = new VerticalLayout();
        container.setWidth("720px");
        container.getStyle()
                .set("margin", "0 auto")
                .set("padding", "32px 0");
        container.setSpacing(false);

        container.add(
                buildSection("Datos personales",  buildPersonalSection()),
                buildSection("Perfil deportivo",  buildSportsSection()),
                buildActions()
        );

        add(container);
        setAlignItems(FlexComponent.Alignment.CENTER);

        loadProfile();
    }

    private void loadProfile() {
        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        String password = (String) UI.getCurrent().getSession().getAttribute("password");
        if (username == null || password == null) {
            UI.getCurrent().navigate("");
            return;
        }
        try {
            UserResponse user = authService.getUser(username, password);
            emailField.setValue(safe(user.getEmail()));
            firstNameField.setValue(safe(user.getFirstName()));
            lastNameField.setValue(safe(user.getLastName()));
            if (user.getBirthDate() != null)        birthDateField.setValue(user.getBirthDate());
            if (user.getSex() != null)              sexField.setValue(user.getSex());
            if (user.getHeightCm() != null)         heightField.setValue(user.getHeightCm());
            if (user.getWeightKg() != null)         weightField.setValue(user.getWeightKg());
            if (user.getSportType() != null)        sportField.setValue(user.getSportType());
            if (user.getAthleteLevel() != null)     levelField.setValue(user.getAthleteLevel());
            if (user.getWeeklyTrainingDays() != null)
                trainingDaysField.setValue(user.getWeeklyTrainingDays().doubleValue());
            if (user.getObservations() != null)     observationsField.setValue(user.getObservations());
        } catch (Exception e) {
            Notification.show("Error al cargar el perfil", 3000, Notification.Position.MIDDLE);
        }
    }

    private VerticalLayout buildPersonalSection() {
        HorizontalLayout nameRow = new HorizontalLayout();
        nameRow.setWidthFull();
        nameRow.setSpacing(true);
        firstNameField.setWidth("50%");
        lastNameField.setWidth("50%");
        nameRow.add(firstNameField, lastNameField);

        HorizontalLayout birthSexRow = new HorizontalLayout();
        birthSexRow.setWidthFull();
        birthSexRow.setSpacing(true);
        birthDateField.setWidth("60%");
        sexField.setWidth("40%");
        birthSexRow.add(birthDateField, sexField);

        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.add(emailField, nameRow, birthSexRow);
        return section;
    }

    private VerticalLayout buildSportsSection() {
        HorizontalLayout physicalRow = new HorizontalLayout();
        physicalRow.setWidthFull();
        physicalRow.setSpacing(true);
        heightField.setWidth("50%");
        weightField.setWidth("50%");
        physicalRow.add(heightField, weightField);

        HorizontalLayout sportLevelRow = new HorizontalLayout();
        sportLevelRow.setWidthFull();
        sportLevelRow.setSpacing(true);
        sportField.setWidth("60%");
        levelField.setWidth("40%");
        sportLevelRow.add(sportField, levelField);

        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.add(physicalRow, sportLevelRow, trainingDaysField, observationsField);
        return section;
    }

    private HorizontalLayout buildActions() {
        Button saveButton = new Button("Guardar cambios");
        saveButton.getStyle()
                .set("height", "52px").set("border-radius", "16px")
                .set("background", "#10b981").set("color", "white")
                .set("font-size", "1.1rem").set("font-weight", "700")
                .set("border", "none").set("cursor", "pointer")
                .set("padding", "0 32px");
        saveButton.addClickListener(e -> saveProfile());

        Button cancelButton = new Button("Cancelar");
        cancelButton.getStyle()
                .set("height", "52px").set("border-radius", "16px")
                .set("background", "transparent").set("color", "#94a3b8")
                .set("font-size", "1.1rem")
                .set("border", "1px solid #334155").set("cursor", "pointer")
                .set("padding", "0 32px");
        cancelButton.addClickListener(e -> UI.getCurrent().navigate("dashboard"));

        HorizontalLayout actions = new HorizontalLayout(cancelButton, saveButton);
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        actions.getStyle().set("margin-top", "24px");
        return actions;
    }

    private void saveProfile() {
        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        String password = (String) UI.getCurrent().getSession().getAttribute("password");
        if (username == null || password == null) {
            UI.getCurrent().navigate("");
            return;
        }
        if (emailField.getValue().isBlank()) {
            Notification.show("El email es obligatorio", 3000, Notification.Position.MIDDLE); return;
        }
        if (firstNameField.getValue().isBlank()) {
            Notification.show("El nombre es obligatorio", 3000, Notification.Position.MIDDLE); return;
        }
        if (lastNameField.getValue().isBlank()) {
            Notification.show("Los apellidos son obligatorios", 3000, Notification.Position.MIDDLE); return;
        }
        if (birthDateField.getValue() == null) {
            Notification.show("La fecha de nacimiento es obligatoria", 3000, Notification.Position.MIDDLE); return;
        }
        if (sexField.getValue() == null) {
            Notification.show("El sexo es obligatorio", 3000, Notification.Position.MIDDLE); return;
        }
        try {
            UpdateUserRequest request = new UpdateUserRequest();
            request.setEmail(emailField.getValue());
            request.setFirstName(firstNameField.getValue());
            request.setLastName(lastNameField.getValue());
            request.setBirthDate(birthDateField.getValue());
            request.setSex(sexField.getValue());
            if (heightField.getValue() != null)        request.setHeightCm(heightField.getValue());
            if (weightField.getValue() != null)        request.setWeightKg(weightField.getValue());
            if (sportField.getValue() != null)         request.setSportType(sportField.getValue());
            if (levelField.getValue() != null)         request.setAthleteLevel(levelField.getValue());
            if (trainingDaysField.getValue() != null)
                request.setWeeklyTrainingDays(trainingDaysField.getValue().intValue());
            if (!observationsField.getValue().isBlank())
                request.setObservations(observationsField.getValue());

            authService.updateUser(username, password, request);
            Notification.show("Perfil actualizado correctamente", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show("Error al guardar: " + e.getMessage(), 4000, Notification.Position.MIDDLE);
        }
    }

    private VerticalLayout buildSection(String title, VerticalLayout content) {
        VerticalLayout section = new VerticalLayout();
        section.setWidthFull();
        section.setPadding(true);
        section.setSpacing(false);
        section.getStyle()
                .set("background", "#0f172a").set("border-radius", "20px")
                .set("border", "1px solid rgba(255,255,255,0.08)")
                .set("margin-bottom", "20px");

        H2 sectionTitle = new H2(title);
        sectionTitle.getStyle()
                .set("margin", "0 0 4px 0").set("font-size", "1.3rem")
                .set("font-weight", "700").set("color", "white");

        Hr divider = new Hr();
        divider.getStyle()
                .set("border", "none")
                .set("border-top", "1px solid rgba(255,255,255,0.08)")
                .set("margin", "12px 0 16px 0");

        section.add(sectionTitle, divider, content);
        return section;
    }

    private static TextField styledText(String label) {
        TextField f = new TextField(label);
        f.setWidthFull();
        applyStyle(f.getElement());
        return f;
    }

    private static EmailField styledEmail(String label) {
        EmailField f = new EmailField(label);
        f.setWidthFull();
        applyStyle(f.getElement());
        return f;
    }

    private static NumberField styledNumber(String label, double min, double max) {
        NumberField f = new NumberField(label);
        f.setMin(min); f.setMax(max);
        f.setWidthFull();
        applyStyle(f.getElement());
        return f;
    }

    private static DatePicker styledDate(String label) {
        DatePicker f = new DatePicker(label);
        f.setWidthFull();
        applyStyle(f.getElement());
        return f;
    }

    private static Select<String> styledSelect(String label, String... items) {
        Select<String> f = new Select<>();
        f.setLabel(label);
        f.setItems(items);
        f.setWidthFull();
        applyStyle(f.getElement());
        return f;
    }

    private static TextArea styledTextArea(String label) {
        TextArea f = new TextArea(label);
        f.setPlaceholder("Añade aquí cualquier observación relevante...");
        f.setWidthFull();
        applyStyle(f.getElement());
        return f;
    }

    private static void applyStyle(com.vaadin.flow.dom.Element element) {
        element.getStyle()
                .set("--vaadin-input-field-background", "#1e293b")
                .set("--vaadin-input-field-border-radius", "14px")
                .set("--vaadin-input-field-border-color", "#334155")
                .set("--vaadin-input-field-value-color", "white")
                .set("--vaadin-input-field-label-color", "#94a3b8")
                .set("--vaadin-input-field-placeholder-color", "#64748b")
                .set("margin-top", "10px");
    }

    private String safe(String val) {
        return val == null ? "" : val;
    }
}