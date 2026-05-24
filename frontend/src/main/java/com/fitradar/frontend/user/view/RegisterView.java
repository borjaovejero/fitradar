package com.fitradar.frontend.user.view;

import com.fitradar.frontend.user.dto.RegisterRequest;
import com.fitradar.frontend.user.dto.UserResponse;
import com.fitradar.frontend.user.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("register")
public class RegisterView extends HorizontalLayout {

    private final AuthService authService;

    public RegisterView(AuthService authService) {
        this.authService = authService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "#020817");

        VerticalLayout leftPanel  = buildLeftPanel();
        VerticalLayout rightPanel = buildRightPanel();

        add(leftPanel, rightPanel);
        expand(leftPanel, rightPanel);
    }

    // ── Panel izquierdo ───────────────────────────────────────────────────────

    private VerticalLayout buildLeftPanel() {
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.setSizeFull();
        leftPanel.setPadding(false);
        leftPanel.setSpacing(false);
        leftPanel.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        leftPanel.setAlignItems(FlexComponent.Alignment.CENTER);
        leftPanel.getStyle()
                .set("background", "radial-gradient(circle at 30% 30%, #0f766e 0%, #1e293b 45%, #0f172a 100%)")
                .set("color", "white");

        VerticalLayout content = new VerticalLayout();
        content.setWidth("460px");
        content.setPadding(false);
        content.setSpacing(false);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.getStyle().set("gap", "20px");

        HorizontalLayout brandRow = new HorizontalLayout();
        brandRow.setSpacing(true);
        brandRow.setAlignItems(FlexComponent.Alignment.CENTER);

        Image logoImg = new Image("images/fitradar_logo.png", "FitRadar logo");
        logoImg.getStyle()
                .set("width", "72px").set("height", "72px")
                .set("object-fit", "contain").set("border-radius", "20px")
                .set("filter", "drop-shadow(0 0 12px rgba(16,185,129,0.45))");

        H1 brandTitle = new H1("FitRadar");
        brandTitle.getStyle()
                .set("margin", "0").set("font-size", "3rem")
                .set("font-weight", "800").set("color", "white");

        brandRow.add(logoImg, brandTitle);

        Paragraph tagline = new Paragraph("Predict. Prevent. Perform.");
        tagline.getStyle()
                .set("margin", "0").set("font-size", "1.3rem")
                .set("font-weight", "700").set("color", "#14e0a1")
                .set("letter-spacing", "0.04em").set("text-align", "center");

        Paragraph welcome = new Paragraph(
                "Crea tu cuenta y empieza a entrenar de forma mas inteligente. "
                        + "Registra tus sesiones, monitoriza tu estado físico diario "
                        + "y deja que el modelo ML trabaje por ti.");
        welcome.getStyle()
                .set("margin", "0").set("font-size", "1rem")
                .set("line-height", "1.7").set("color", "#94a3b8")
                .set("text-align", "center");

        Div separator = new Div();
        separator.getStyle()
                .set("width", "60px").set("height", "3px")
                .set("background", "linear-gradient(90deg,#14e0a1,#0ea5a4)")
                .set("border-radius", "999px");

        Paragraph step1 = buildStep("1", "Rellena tus datos básicos");
        Paragraph step2 = buildStep("2", "Añade tu perfil deportivo (opcional)");
        Paragraph step3 = buildStep("3", "Empieza a registrar entrenamientos");

        content.add(brandRow, tagline, separator, welcome, step1, step2, step3);
        leftPanel.add(content);
        return leftPanel;
    }

    private Paragraph buildStep(String num, String text) {
        Paragraph p = new Paragraph(num + ".  " + text);
        p.getStyle()
                .set("margin", "0").set("font-size", "0.95rem")
                .set("color", "#cbd5e1").set("text-align", "center");
        return p;
    }

    // ── Panel derecho (formulario) ────────────────────────────────────────────

    private VerticalLayout buildRightPanel() {
        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.setSizeFull();
        rightPanel.setPadding(false);
        rightPanel.setSpacing(false);
        rightPanel.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        rightPanel.setAlignItems(FlexComponent.Alignment.CENTER);
        rightPanel.getStyle()
                .set("background", "#020817").set("color", "white")
                .set("overflow-y", "auto")
                .set("padding-top", "40px")
                .set("padding-bottom", "40px");

        VerticalLayout formCard = new VerticalLayout();
        formCard.setWidth("560px");
        formCard.setPadding(false);
        formCard.setSpacing(true);
        formCard.setAlignItems(FlexComponent.Alignment.STRETCH);
        formCard.getStyle().set("padding", "40px 0").set("gap", "6px");

        H2 title = new H2("Crear cuenta");
        title.getStyle()
                .set("margin", "0").set("font-size", "2.5rem")
                .set("font-weight", "800").set("color", "white");

        Paragraph subtitle = new Paragraph("Completa tus datos para empezar");
        subtitle.getStyle()
                .set("margin", "0 0 18px 0").set("font-size", "1.1rem").set("color", "#94a3b8");

        // ── Datos obligatorios ────────────────────────────────────────────────

        TextField firstNameField    = styledText("Nombre",             "Tu nombre");
        TextField lastNameField     = styledText("Apellidos",           "Tus apellidos");
        EmailField emailField       = styledEmail("Correo electrónico", "tu@email.com");
        DatePicker birthDateField   = styledDate("Fecha de nacimiento");
        TextField usernameField     = styledText("Nombre de usuario",   "Tu usuario unico");
        PasswordField passwordField = styledPassword("Contraseña",      "Mínimo 6 caracteres");
        passwordField.setMinLength(6);
        passwordField.addValueChangeListener(e -> {
            if (!e.getValue().isEmpty() && e.getValue().length() < 6) {
                passwordField.setInvalid(true);
                passwordField.setErrorMessage("Mínimo 6 caracteres");
            } else {
                passwordField.setInvalid(false);
            }
        });

        PasswordField confirmPasswordField = styledPassword("Confirmar contraseña", "Repite tu contraseña");
        confirmPasswordField.addValueChangeListener(e -> {
            if (!e.getValue().isEmpty() && !e.getValue().equals(passwordField.getValue())) {
                confirmPasswordField.setInvalid(true);
                confirmPasswordField.setErrorMessage("Las contraseñas no coinciden");
            } else {
                confirmPasswordField.setInvalid(false);
            }
        });

        // ── Seccion opcional ──────────────────────────────────────────────────

        Paragraph optionalLabel = new Paragraph("Perfil deportivo (opcional)");
        optionalLabel.getStyle()
                .set("margin", "20px 0 4px 0").set("font-size", "0.95rem")
                .set("font-weight", "700").set("color", "#14e0a1")
                .set("letter-spacing", "0.05em");

        Hr divider = new Hr();
        divider.getStyle()
                .set("border", "none")
                .set("border-top", "1px solid rgba(255,255,255,0.10)")
                .set("margin", "0 0 12px 0");

        Select<String> sexField = styledSelect("Sexo");
        sexField.setItems("MALE", "FEMALE", "OTHER");
        sexField.setItemLabelGenerator(v -> switch (v) {
            case "MALE"   -> "Masculino";
            case "FEMALE" -> "Femenino";
            default       -> "Otro";
        });

        HorizontalLayout physicalRow = new HorizontalLayout();
        physicalRow.setWidthFull();
        physicalRow.setSpacing(true);

        NumberField heightField = styledNumber("Altura (cm)", 100, 250);
        heightField.setWidth("50%");
        NumberField weightField = styledNumber("Peso (kg)", 30, 300);
        weightField.setWidth("50%");
        physicalRow.add(heightField, weightField);

        Select<String> sportField = styledSelect("Deporte principal");
        sportField.setItems("RUNNING","FOOTBALL","GYM","CYCLING",
                "BASKETBALL","TENNIS","PADEL","SWIMMING","OTHER");
        sportField.setItemLabelGenerator(v -> switch (v) {
            case "RUNNING"    -> "Atletismo / Running";
            case "FOOTBALL"   -> "Futbol";
            case "GYM"        -> "Gimnasio";
            case "CYCLING"    -> "Ciclismo";
            case "BASKETBALL" -> "Baloncesto";
            case "TENNIS"     -> "Tenis";
            case "PADEL"      -> "Padel";
            case "SWIMMING"   -> "Natacion";
            default           -> "Otro";
        });

        Select<String> levelField = styledSelect("Nivel deportivo");
        levelField.setItems("BEGINNER", "INTERMEDIATE", "ADVANCED");
        levelField.setItemLabelGenerator(v -> switch (v) {
            case "BEGINNER"     -> "Principiante";
            case "INTERMEDIATE" -> "Intermedio";
            default             -> "Avanzado";
        });

        NumberField trainingDaysField = styledNumber("Días de entrenamiento por semana", 1, 7);

        TextArea observationsField = new TextArea("Observaciones");
        observationsField.setPlaceholder("Objetivos, notas adicionales...");
        observationsField.setWidthFull();
        observationsField.getStyle().set("margin-top", "10px");
        applyInputStyle(observationsField.getElement());

        // ── Boton ─────────────────────────────────────────────────────────────

        Button registerButton = new Button("Crear cuenta");
        registerButton.setWidthFull();
        registerButton.getStyle()
                .set("height", "60px").set("border-radius", "18px")
                .set("background", "#10b981").set("color", "white")
                .set("font-size", "1.2rem").set("font-weight", "700")
                .set("border", "none").set("cursor", "pointer")
                .set("margin-top", "14px");

        registerButton.addClickListener(event -> {
            try {
                if (firstNameField.getValue().isBlank()) {
                    Notification.show("El nombre es obligatorio", 3000, Notification.Position.MIDDLE); return;
                }
                if (lastNameField.getValue().isBlank()) {
                    Notification.show("Los apellidos son obligatorios", 3000, Notification.Position.MIDDLE); return;
                }
                if (emailField.getValue().isBlank()) {
                    Notification.show("El email es obligatorio", 3000, Notification.Position.MIDDLE); return;
                }
                if (birthDateField.getValue() == null) {
                    Notification.show("La fecha de nacimiento es obligatoria", 3000, Notification.Position.MIDDLE); return;
                }
                if (usernameField.getValue().isBlank()) {
                    Notification.show("El nombre de usuario es obligatorio", 3000, Notification.Position.MIDDLE); return;
                }
                if (passwordField.getValue().isBlank()) {
                    Notification.show("La contraseña es obligatoria", 3000, Notification.Position.MIDDLE); return;
                }
                if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                    Notification.show("Las contraseñas no coinciden", 3000, Notification.Position.MIDDLE); return;
                }
                if (passwordField.getValue().length() < 6) {
                    Notification.show("La contraseña debe tener al menos 6 caracteres", 3000, Notification.Position.MIDDLE);
                    return;
                }

                RegisterRequest request = new RegisterRequest();
                request.setFirstName(firstNameField.getValue());
                request.setLastName(lastNameField.getValue());
                request.setEmail(emailField.getValue());
                request.setBirthDate(birthDateField.getValue());
                request.setUsername(usernameField.getValue());
                request.setPassword(passwordField.getValue());

                if (sexField.getValue() != null)              request.setSex(sexField.getValue());
                if (heightField.getValue() != null)           request.setHeightCm(heightField.getValue());
                if (weightField.getValue() != null)           request.setWeightKg(weightField.getValue());
                if (sportField.getValue() != null)            request.setSportType(sportField.getValue());
                if (levelField.getValue() != null)            request.setAthleteLevel(levelField.getValue());
                if (trainingDaysField.getValue() != null)     request.setWeeklyTrainingDays(trainingDaysField.getValue().intValue());
                if (!observationsField.getValue().isBlank())  request.setObservations(observationsField.getValue());

                UserResponse response = authService.register(request);
                Notification.show("Cuenta creada. Bienvenido, " + response.getUsername(),
                        3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("");

            } catch (Exception e) {
                Notification.show("Error en el registro: " + e.getMessage(),
                        4000, Notification.Position.MIDDLE);
            }
        });

        HorizontalLayout loginRow = new HorizontalLayout();
        loginRow.setWidthFull();
        loginRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginRow.setAlignItems(FlexComponent.Alignment.CENTER);
        loginRow.getStyle().set("margin-top", "14px");

        Paragraph loginText = new Paragraph("¿Ya tienes cuenta?");
        loginText.getStyle().set("margin", "0").set("color", "#cbd5e1").set("font-size", "1rem");

        Button loginButton = new Button("Inicia sesión");
        loginButton.getStyle()
                .set("background", "transparent").set("color", "#10b981")
                .set("font-size", "1rem").set("font-weight", "700")
                .set("border", "none").set("cursor", "pointer");
        loginButton.addClickListener(e -> UI.getCurrent().navigate(""));

        loginRow.add(loginText, loginButton);

        formCard.add(
                title, subtitle,
                firstNameField, lastNameField, emailField,
                birthDateField, usernameField,
                passwordField, confirmPasswordField,
                optionalLabel, divider,
                sexField, physicalRow, sportField, levelField,
                trainingDaysField, observationsField,
                registerButton, loginRow
        );

        rightPanel.add(formCard);
        return rightPanel;
    }

    // ── Helpers de campos ─────────────────────────────────────────────────────

    private TextField styledText(String label, String placeholder) {
        TextField f = new TextField(label);
        f.setPlaceholder(placeholder);
        f.setWidthFull();
        f.getStyle().set("margin-top", "10px");
        applyInputStyle(f.getElement());
        return f;
    }

    private EmailField styledEmail(String label, String placeholder) {
        EmailField f = new EmailField(label);
        f.setPlaceholder(placeholder);
        f.setWidthFull();
        f.getStyle().set("margin-top", "10px");
        applyInputStyle(f.getElement());
        return f;
    }

    private PasswordField styledPassword(String label, String placeholder) {
        PasswordField f = new PasswordField(label);
        f.setPlaceholder(placeholder);
        f.setWidthFull();
        f.getStyle().set("margin-top", "10px");
        applyInputStyle(f.getElement());
        return f;
    }

    private DatePicker styledDate(String label) {
        DatePicker f = new DatePicker(label);
        f.setWidthFull();
        f.getStyle().set("margin-top", "10px");
        applyInputStyle(f.getElement());
        return f;
    }

    private Select<String> styledSelect(String label) {
        Select<String> f = new Select<>();
        f.setLabel(label);
        f.setWidthFull();
        f.getStyle().set("margin-top", "10px");
        applyInputStyle(f.getElement());
        return f;
    }

    private NumberField styledNumber(String label, double min, double max) {
        NumberField f = new NumberField(label);
        f.setMin(min);
        f.setMax(max);
        f.setWidthFull();
        f.getStyle().set("margin-top", "10px");
        applyInputStyle(f.getElement());
        return f;
    }

    private void applyInputStyle(com.vaadin.flow.dom.Element el) {
        el.getStyle()
                .set("--vaadin-input-field-background", "#1e293b")
                .set("--vaadin-input-field-border-radius", "18px")
                .set("--vaadin-input-field-border-color", "#334155")
                .set("--vaadin-input-field-value-color", "white")
                .set("--vaadin-input-field-label-color", "white")
                .set("--vaadin-input-field-placeholder-color", "#64748b")
                .set("font-size", "1.05rem")
                .set("margin-top", "2px");
    }
}