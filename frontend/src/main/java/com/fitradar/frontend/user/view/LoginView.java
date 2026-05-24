package com.fitradar.frontend.user.view;

import com.fitradar.frontend.user.dto.LoginRequest;
import com.fitradar.frontend.user.dto.LoginResponse;
import com.fitradar.frontend.user.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class LoginView extends HorizontalLayout {

    private final AuthService authService;

    public LoginView(AuthService authService) {
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
        content.setWidth("520px");
        content.setPadding(false);
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.START);

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
                .set("margin", "24px 0 0 0").set("font-size", "1.3rem")
                .set("font-weight", "700").set("color", "#14e0a1")
                .set("letter-spacing", "0.04em");

        Div separator = new Div();
        separator.getStyle()
                .set("width", "60px").set("height", "3px")
                .set("background", "linear-gradient(90deg,#14e0a1,#0ea5a4)")
                .set("border-radius", "999px")
                .set("margin", "10px 0 12px 0");

        HorizontalLayout row1 = new HorizontalLayout();
        row1.setWidthFull();
        row1.setSpacing(true);
        row1.add(
                buildFeatureCard("Wellness diario"),
                buildFeatureCard("Actividades")
        );

        HorizontalLayout row2 = new HorizontalLayout();
        row2.setWidthFull();
        row2.setSpacing(true);
        row2.add(
                buildFeatureCard("Predicción ML"),
                buildFeatureCard("Lesiones")
        );

        content.add(brandRow, tagline, separator, row1, row2);
        leftPanel.add(content);
        return leftPanel;
    }

    private VerticalLayout buildFeatureCard(String title) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("250px");
        card.setPadding(true);
        card.setSpacing(false);
        card.setAlignItems(FlexComponent.Alignment.START);
        card.getStyle()
                .set("background", "rgba(255,255,255,0.08)")
                .set("border-radius", "20px")
                .set("border", "1px solid rgba(255,255,255,0.08)");

        H2 titleText = new H2(title);
        titleText.getStyle()
                .set("margin", "0").set("font-size", "1.4rem")
                .set("font-weight", "800").set("color", "#14e0a1");

        card.add(titleText);
        return card;
    }

    private VerticalLayout buildRightPanel() {
        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.setSizeFull();
        rightPanel.setPadding(false);
        rightPanel.setSpacing(false);
        rightPanel.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        rightPanel.setAlignItems(FlexComponent.Alignment.CENTER);
        rightPanel.getStyle()
                .set("background", "#020817").set("color", "white");

        VerticalLayout formCard = new VerticalLayout();
        formCard.setWidth("560px");
        formCard.setPadding(false);
        formCard.setSpacing(true);
        formCard.setAlignItems(FlexComponent.Alignment.STRETCH);
        formCard.getStyle().set("gap", "6px");

        H2 title = new H2("Bienvenido de vuelta");
        title.getStyle()
                .set("margin", "0").set("font-size", "2.5rem")
                .set("font-weight", "800").set("color", "white");

        Paragraph subtitle = new Paragraph("Inicia sesión");
        subtitle.getStyle()
                .set("margin", "0 0 8px 0").set("font-size", "1.1rem").set("color", "#94a3b8");

        TextField usernameField = new TextField("Nombre de usuario");
        styleField(usernameField);

        PasswordField passwordField = new PasswordField("Contraseña");
        styleField(passwordField);

        Button loginButton = new Button("Iniciar sesión");
        loginButton.setWidthFull();
        loginButton.getStyle()
                .set("height", "60px").set("border-radius", "18px")
                .set("background", "#10b981").set("color", "white")
                .set("font-size", "1.2rem").set("font-weight", "700")
                .set("border", "none").set("cursor", "pointer")
                .set("margin-top", "14px");

        loginButton.addClickListener(event -> {
            try {
                if (usernameField.getValue() == null || usernameField.getValue().isBlank()) {
                    Notification.show("El nombre de usuario es obligatorio", 3000, Notification.Position.MIDDLE);
                    return;
                }
                if (passwordField.getValue() == null || passwordField.getValue().isBlank()) {
                    Notification.show("La contrasena es obligatoria", 3000, Notification.Position.MIDDLE);
                    return;
                }

                LoginRequest request = new LoginRequest(
                        usernameField.getValue(),
                        passwordField.getValue()
                );

                LoginResponse response = authService.login(request);

                UI.getCurrent().getSession().setAttribute("username", response.getUsername());
                UI.getCurrent().getSession().setAttribute("password", passwordField.getValue());

                Notification.show(response.getMessage(), 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("dashboard");

            } catch (Exception e) {
                Notification.show("Credenciales incorrectas o error de conexion", 4000, Notification.Position.MIDDLE);
            }
        });

        HorizontalLayout registerRow = new HorizontalLayout();
        registerRow.setWidthFull();
        registerRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        registerRow.setAlignItems(FlexComponent.Alignment.CENTER);
        registerRow.getStyle().set("margin-top", "14px");

        Paragraph registerText = new Paragraph("¿No tienes cuenta?");
        registerText.getStyle()
                .set("margin", "0").set("color", "#cbd5e1").set("font-size", "1rem");

        Button registerButton = new Button("Regístrate aquí");
        registerButton.getStyle()
                .set("background", "transparent").set("color", "#10b981")
                .set("font-size", "1rem").set("font-weight", "700")
                .set("border", "none").set("cursor", "pointer");
        registerButton.addClickListener(event -> UI.getCurrent().navigate("register"));

        registerRow.add(registerText, registerButton);

        formCard.add(title, subtitle, usernameField, passwordField, loginButton, registerRow);
        rightPanel.add(formCard);
        return rightPanel;
    }

    private void styleField(com.vaadin.flow.component.textfield.TextFieldBase<?, ?> field) {
        field.setWidthFull();
        field.getStyle().set("margin-top", "2px");
        field.getElement().getStyle()
                .set("--vaadin-input-field-background", "#1e293b")
                .set("--vaadin-input-field-border-radius", "18px")
                .set("--vaadin-input-field-border-width", "1px")
                .set("--vaadin-input-field-border-color", "#334155")
                .set("--vaadin-input-field-value-color", "white")
                .set("--vaadin-input-field-label-color", "white")
                .set("--vaadin-input-field-placeholder-color", "#64748b")
                .set("font-size", "1.05rem");
    }
}