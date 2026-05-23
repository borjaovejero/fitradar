package com.fitradar.frontend.shared.view;

import com.fitradar.frontend.user.dto.UserResponse;
import com.fitradar.frontend.user.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainLayout extends AppLayout {

    private final AuthService authService;

    public MainLayout(AuthService authService) {
        this.authService = authService;
        setPrimarySection(Section.DRAWER);
        createDrawer();
        createHeader();
        getStyle().set("background", "#020817");
    }

    private void createDrawer() {
        VerticalLayout drawer = new VerticalLayout();
        drawer.setSizeFull();
        drawer.setPadding(false);
        drawer.setSpacing(false);
        drawer.getStyle().set("background", "#0f172a").set("color", "white");

        HorizontalLayout brandRow = new HorizontalLayout();
        brandRow.setWidthFull();
        brandRow.setAlignItems(FlexComponent.Alignment.CENTER);
        brandRow.getStyle()
                .set("padding", "20px 18px")
                .set("border-bottom", "1px solid rgba(255,255,255,0.08)");

        Div logoBox = new Div();
        logoBox.setText("⚡");
        logoBox.getStyle()
                .set("width", "44px").set("height", "44px").set("border-radius", "14px")
                .set("background", "#10b981").set("display", "flex")
                .set("align-items", "center").set("justify-content", "center")
                .set("box-shadow", "0 0 18px rgba(16,185,129,0.35)");

        H2 brandTitle = new H2("FitRadar");
        brandTitle.getStyle().set("margin", "0").set("font-size", "2rem")
                .set("font-weight", "800").set("color", "white");
        brandRow.add(logoBox, brandTitle);

        Paragraph sectionTitle = new Paragraph("MENU PRINCIPAL");
        sectionTitle.getStyle()
                .set("margin", "20px 18px 8px 18px").set("font-size", "0.9rem")
                .set("letter-spacing", "0.08em").set("color", "#64748b");

        VerticalLayout menu = new VerticalLayout();
        menu.setPadding(false);
        menu.setSpacing(false);
        menu.getStyle().set("padding", "0 14px");
        menu.add(
                navBtn("Dashboard",        VaadinIcon.DASHBOARD,   "dashboard", isActive("dashboard")),
                navBtn("Wellness",         VaadinIcon.HEART_O,     "wellness",  isActive("wellness")),
                navBtn("Actividades",      VaadinIcon.WRENCH,      "training",  isActive("training")),
                navBtn("Riesgo de lesión", VaadinIcon.SHIELD,      "risk",      isActive("risk")),
                navBtn("Lesiones",         VaadinIcon.STETHOSCOPE, "injury",    isActive("injury"))
        );

        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        VerticalLayout bottomMenu = new VerticalLayout();
        bottomMenu.setPadding(false);
        bottomMenu.setSpacing(false);
        bottomMenu.getStyle().set("padding", "14px")
                .set("border-top", "1px solid rgba(255,255,255,0.08)");
        bottomMenu.add(
                navBtn("Editar perfil", VaadinIcon.USER, "profile", isActive("profile")),
                logoutBtn()
        );

        drawer.add(brandRow, sectionTitle, menu, spacer, bottomMenu);
        drawer.expand(spacer);
        addToDrawer(drawer);
        setDrawerOpened(true);
    }

    private void createHeader() {
        String username = (String) UI.getCurrent().getSession().getAttribute("username");
        String password = (String) UI.getCurrent().getSession().getAttribute("password");

        String firstName  = username != null ? username : "Usuario";
        String sportLevel = "";

        if (username != null && password != null) {
            try {
                UserResponse user = authService.getUser(username, password);
                if (user.getFirstName() != null && !user.getFirstName().isBlank())
                    firstName = user.getFirstName();
                String sport = sportLabel(user.getSportType());
                String level = levelLabel(user.getAthleteLevel());
                if (!sport.isBlank() && !level.isBlank())      sportLevel = sport + " - " + level;
                else if (!sport.isBlank())                     sportLevel = sport;
                else if (!level.isBlank())                     sportLevel = level;
            } catch (Exception ignored) {}
        }

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle()
                .set("padding", "14px 28px").set("background", "#0b1224")
                .set("border-bottom", "1px solid rgba(255,255,255,0.08)");

        // Izquierda: título + fecha
        VerticalLayout titleBlock = new VerticalLayout();
        titleBlock.setPadding(false);
        titleBlock.setSpacing(false);
        H2 title = new H2(headerTitle());
        title.getStyle().set("margin", "0").set("font-size", "2rem")
                .set("font-weight", "800").set("color", "white");
        Paragraph dateText = new Paragraph(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", new Locale("es", "ES"))));
        dateText.getStyle().set("margin", "0").set("font-size", "0.95rem").set("color", "#64748b");
        titleBlock.add(title, dateText);

        // Derecha: nombre + deporte/nivel a la izquierda del avatar
        // Estructura: [ texto (nombre\ndeporte) ] [ avatar ]
        Paragraph nameP = new Paragraph(firstName);
        nameP.getStyle()
                .set("margin", "0").set("font-size", "0.95rem")
                .set("font-weight", "700").set("color", "white");

        VerticalLayout userText = new VerticalLayout();
        userText.setPadding(false);
        userText.setSpacing(false);
        userText.setAlignItems(FlexComponent.Alignment.END);
        userText.add(nameP);

        if (!sportLevel.isBlank()) {
            Paragraph sportP = new Paragraph(sportLevel);
            sportP.getStyle()
                    .set("margin", "0").set("font-size", "0.8rem").set("color", "#64748b").set("white-space", "nowrap");
            userText.add(sportP);
        }

        Avatar avatar = new Avatar(buildInitials(firstName));
        avatar.getStyle()
                .set("--vaadin-avatar-size", "44px")
                .set("background", "#14b8a6").set("color", "white")
                .set("flex-shrink", "0");

        Div avatarWrapper = new Div(avatar);
        avatarWrapper.getStyle().set("cursor", "pointer");
        avatarWrapper.addClickListener(e -> UI.getCurrent().navigate("profile"));

        HorizontalLayout rightBlock = new HorizontalLayout();
        rightBlock.setAlignItems(FlexComponent.Alignment.CENTER);
        rightBlock.setPadding(false);
        rightBlock.setSpacing(true);
        rightBlock.add(userText, avatarWrapper);

        header.add(titleBlock, rightBlock);
        addToNavbar(header);
    }

    private Button navBtn(String text, VaadinIcon icon, String route, boolean active) {
        Button btn = new Button(text, icon.create(), e -> UI.getCurrent().navigate(route));
        btn.setWidthFull();
        btn.getStyle()
                .set("height", "48px").set("justify-content", "flex-start")
                .set("border-radius", "16px").set("margin-bottom", "10px")
                .set("font-size", "1.05rem")
                .set("font-weight", active ? "700" : "500")
                .set("border", active ? "1px solid rgba(20,224,161,0.25)" : "1px solid transparent")
                .set("background", active ? "rgba(16,185,129,0.18)" : "transparent")
                .set("color", active ? "#14e0a1" : "#cbd5e1")
                .set("cursor", "pointer");
        return btn;
    }

    private Button logoutBtn() {
        Button btn = new Button("Cerrar sesión", VaadinIcon.SIGN_OUT.create(), e -> {
            UI.getCurrent().getSession().setAttribute("username", null);
            UI.getCurrent().getSession().setAttribute("password", null);
            UI.getCurrent().navigate("");
        });
        btn.setWidthFull();
        btn.getStyle()
                .set("height", "48px").set("justify-content", "flex-start")
                .set("border-radius", "16px").set("font-size", "1.05rem")
                .set("font-weight", "500").set("border", "1px solid transparent")
                .set("background", "transparent").set("color", "#cbd5e1").set("cursor", "pointer");
        return btn;
    }

    private boolean isActive(String route) {
        return route.equals(UI.getCurrent().getInternals().getActiveViewLocation().getPath());
    }

    private String headerTitle() {
        return switch (UI.getCurrent().getInternals().getActiveViewLocation().getPath()) {
            case "wellness" -> "Wellness";
            case "risk"     -> "Medidor de Riesgo";
            case "training" -> "Control de Actividades";
            case "injury"   -> "Control de Lesiones";
            case "profile"  -> "Editar perfil";
            default         -> "Resumen general";
        };
    }

    private String sportLabel(String v) {
        if (v == null) return "";
        return switch (v) {
            case "RUNNING" -> "Atletismo"; case "FOOTBALL" -> "Fútbol";
            case "CYCLING" -> "Ciclismo";  case "SWIMMING" -> "Natación";
            case "TENNIS"  -> "Tenis";     case "PADEL"    -> "Pádel";
            case "BASKETBALL" -> "Baloncesto"; case "GYM"  -> "Gimnasio";
            default -> "";
        };
    }

    private String levelLabel(String v) {
        if (v == null) return "";
        return switch (v) {
            case "BEGINNER" -> "Principiante"; case "INTERMEDIATE" -> "Intermedio";
            case "ADVANCED" -> "Avanzado"; default -> "";
        };
    }

    private String buildInitials(String text) {
        if (text == null || text.isBlank()) return "FR";
        String[] parts = text.trim().split("\\s+");
        if (parts.length >= 2) return (parts[0].substring(0,1) + parts[1].substring(0,1)).toUpperCase();
        String t = text.trim();
        return t.length() >= 2 ? t.substring(0, 2).toUpperCase() : t.toUpperCase();
    }
}