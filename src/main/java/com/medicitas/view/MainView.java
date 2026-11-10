package com.medicitas.view;

import com.medicitas.controller.CitaMedicaController;
import com.medicitas.controller.MedicoController;
import com.medicitas.controller.PacienteController;
import com.medicitas.model.Usuario;
import com.medicitas.repository.UsuarioRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("")
@PermitAll
public class MainView extends VerticalLayout {

    private final PacienteController pacienteController;
    private final MedicoController medicoController;
    private final CitaMedicaController citaController;
    private final UsuarioRepository usuarioRepository;

    public MainView(PacienteController pacienteController,
                    MedicoController medicoController,
                    CitaMedicaController citaController,
                    UsuarioRepository usuarioRepository) {
        this.pacienteController = pacienteController;
        this.medicoController = medicoController;
        this.citaController = citaController;
        this.usuarioRepository = usuarioRepository;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "#f4f7fb")
                .set("color", "#172033")
                .set("overflow", "auto");

        add(crearBarraSuperior(), crearContenido());
    }

    private Div crearBarraSuperior() {
        Div barra = new Div();
        barra.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between")
                .set("flex-wrap", "wrap")
                .set("gap", "16px")
                .set("padding", "18px 6vw")
                .set("background", "#ffffff")
                .set("border-bottom", "1px solid #e7ebf2");

        Div marca = new Div();
        marca.getStyle().set("display", "flex").set("align-items", "center").set("gap", "10px");
        Span simbolo = new Span("+");
        simbolo.getStyle()
                .set("display", "grid")
                .set("place-items", "center")
                .set("width", "34px")
                .set("height", "34px")
                .set("border-radius", "10px")
                .set("background", "#0e8f88")
                .set("color", "white")
                .set("font-size", "24px")
                .set("font-weight", "700");
        Div identidad = new Div();
        identidad.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "2px");
        Span nombre = new Span("GoodDentis");
        nombre.getStyle().set("font-size", "20px").set("font-weight", "800").set("line-height", "1");
        Span subtitulo = new Span("Cuidado dental organizado");
        subtitulo.getStyle().set("font-size", "11px").set("color", "#768397");
        identidad.add(nombre, subtitulo);
        marca.add(simbolo, identidad);

        boolean doctor = esDoctor();
        Button citas = new Button(doctor ? "Mi agenda" : "Agendar cita", VaadinIcon.CALENDAR.create(),
                event -> UI.getCurrent().navigate(doctor ? "mi-agenda" : "citas"));
        citas.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        citas.getStyle().set("white-space", "nowrap").set("font-weight", "700");
        barra.add(marca, citas);
        return barra;
    }

    private VerticalLayout crearContenido() {
        VerticalLayout contenido = new VerticalLayout();
        contenido.setWidth("84vw");
        contenido.setMaxWidth("1220px");
        contenido.getStyle().set("margin", "0 auto").set("padding", "42px 0 64px");

        Div hero = new Div();
        hero.getStyle()
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "end")
                .set("flex-wrap", "wrap")
                .set("gap", "30px")
                .set("padding", "38px 42px")
                .set("border-radius", "24px")
                .set("background", "linear-gradient(120deg, #e8f7f4 0%, #e1f1f7 58%, #fff5e6 100%)")
                .set("color", "#173b4d")
                .set("box-shadow", "0 18px 42px rgba(54, 112, 126, .12)");

        Div heroTexto = new Div();
        Span etiqueta = new Span("CENTRO DE CONTROL");
        etiqueta.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "800")
                .set("letter-spacing", "2px")
                .set("color", "#0e8f88");
        boolean doctor = esDoctor();
        H1 titulo = new H1("Hola, " + nombreUsuario());
        titulo.getStyle().set("max-width", "560px").set("margin", "12px 0 8px").set("font-size", "clamp(28px, 4vw, 48px)");
        Paragraph descripcion = new Paragraph(doctor
                ? "Esta es tu agenda de atencion. Consulta tus citas y horarios del dia."
                : "Organiza pacientes, profesionales y citas desde un solo lugar.");
        descripcion.getStyle().set("margin", "0").set("color", "#496576").set("font-size", "16px");
        heroTexto.add(etiqueta, titulo, descripcion);

        Button registrar = new Button("Registrar paciente", VaadinIcon.USER.create(),
                event -> UI.getCurrent().navigate(PacienteView.class));
        registrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registrar.getStyle().set("font-weight", "700");
        hero.add(heroTexto, registrar);

        H2 resumenTitulo = new H2("Resumen del sistema");
        resumenTitulo.getStyle().set("margin", "34px 0 4px").set("font-size", "22px");
        Paragraph resumenTexto = new Paragraph("Informacion actual de tu agenda medica");
        resumenTexto.getStyle().set("margin", "0 0 14px").set("color", "#6b7689");

        Div agendaDestacada = crearAgendaDestacada(doctor);

        HorizontalLayout metricas = new HorizontalLayout(
                crearMetrica("Pacientes", pacienteController.listar().size(), "Registrados", "#e7f6f3", "#0e8f88"),
                crearMetrica("Medicos", medicoController.listar().size(), "Profesionales", "#fff2df", "#c67819"),
                crearMetrica("Citas", citaController.listar().size(), "En la agenda", "#ece9ff", "#6554c0"));
        metricas.setWidthFull();
        metricas.setSpacing(true);
        metricas.getStyle().set("flex-wrap", "wrap");

        H2 accionesTitulo = new H2("Acciones rapidas");
        accionesTitulo.getStyle().set("margin", "34px 0 14px").set("font-size", "22px");
        HorizontalLayout acciones = esDoctor()
                ? new HorizontalLayout(crearAccion("Mi agenda", "Consulta tus citas asignadas",
                VaadinIcon.CALENDAR, DoctorAgendaView.class))
                : new HorizontalLayout(
                crearAccion("Gestionar pacientes", "Registra y actualiza informacion", VaadinIcon.USER,
                        PacienteView.class),
                crearAccion("Gestionar medicos", "Administra especialidades", VaadinIcon.STETHOSCOPE,
                        MedicoView.class),
                crearAccion("Abrir agenda", "Programa una nueva cita", VaadinIcon.CALENDAR,
                        CitaMedicaView.class));
        acciones.setWidthFull();
        acciones.setSpacing(true);
        acciones.getStyle().set("flex-wrap", "wrap");

        Div estado = new Div();
        estado.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("margin-top", "34px")
                .set("padding", "16px 20px")
                .set("border", "1px solid #dcefe8")
                .set("border-radius", "14px")
                .set("background", "#f4fcf8");
        Span punto = new Span();
        punto.getStyle().set("width", "10px").set("height", "10px").set("border-radius", "50%").set("background", "#2fbd79");
        Span estadoTexto = new Span("Sistema operativo y listo para gestionar citas");
        estadoTexto.getStyle().set("color", "#287152").set("font-weight", "600");
        estado.add(punto, estadoTexto);

        contenido.add(hero, agendaDestacada, resumenTitulo, resumenTexto, metricas,
                accionesTitulo, acciones, estado);
        return contenido;
    }

    private Div crearAgendaDestacada(boolean doctor) {
        Div tarjeta = new Div();
        tarjeta.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between")
                .set("flex-wrap", "wrap")
                .set("gap", "18px")
                .set("margin-top", "28px")
                .set("padding", "24px 28px")
                .set("border-radius", "20px")
                .set("background", doctor ? "#fffaf0" : "#f0fbfa")
                .set("border", doctor ? "1px solid #f3dfb8" : "1px solid #c9ebe6");

        Div texto = new Div();
        Span etiqueta = new Span(doctor ? "PRIORIDAD DEL DIA" : "AGENDA CLINICA");
        etiqueta.getStyle()
                .set("display", "block")
                .set("font-size", "11px")
                .set("font-weight", "800")
                .set("letter-spacing", "1.6px")
                .set("color", doctor ? "#b57418" : "#0e8f88");
        H2 titulo = new H2(doctor ? "Revisa tu agenda antes de atender" : "Agenda medica centralizada");
        titulo.getStyle().set("margin", "7px 0 4px").set("font-size", "22px");
        Paragraph detalle = new Paragraph(doctor
                ? "Consulta tus pacientes, horarios y estados de atencion en un solo lugar."
                : "Organiza las citas y los horarios de todos los profesionales.");
        detalle.getStyle().set("margin", "0").set("color", "#657386");
        texto.add(etiqueta, titulo, detalle);

        Button abrir = new Button(doctor ? "Abrir mi agenda" : "Abrir agenda",
                VaadinIcon.CALENDAR.create(),
                event -> UI.getCurrent().navigate(doctor ? "mi-agenda" : "citas"));
        abrir.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        abrir.getStyle().set("white-space", "nowrap").set("font-weight", "700");
        tarjeta.add(texto, abrir);
        return tarjeta;
    }

    private Div crearMetrica(String titulo, int valor, String detalle, String fondo, String color) {
        Div tarjeta = new Div();
        tarjeta.getStyle()
                .set("flex", "1")
                .set("min-width", "180px")
                .set("padding", "22px")
                .set("border-radius", "18px")
                .set("background", fondo)
                .set("border", "1px solid rgba(0,0,0,.04)");
        Span nombre = new Span(titulo);
        nombre.getStyle().set("color", "#586477").set("font-weight", "700");
        Span numero = new Span(String.valueOf(valor));
        numero.getStyle().set("display", "block").set("margin-top", "9px").set("font-size", "36px").set("font-weight", "800").set("color", color);
        Span texto = new Span(detalle);
        texto.getStyle().set("color", "#738095").set("font-size", "13px");
        tarjeta.add(nombre, numero, texto);
        return tarjeta;
    }

    private Button crearAccion(String titulo, String detalle, VaadinIcon icono,
                               Class<? extends com.vaadin.flow.component.Component> vista) {
        Button accion = new Button(titulo + "\n" + detalle, icono.create(),
                event -> UI.getCurrent().navigate(vista));
        accion.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        accion.getStyle()
                .set("flex", "1")
                .set("height", "82px")
                .set("justify-content", "flex-start")
                .set("text-align", "left")
                .set("white-space", "pre-line")
                .set("padding", "18px")
                .set("border", "1px solid #e2e7ef")
                .set("border-radius", "16px")
                .set("background", "white");
        return accion;
    }

    private boolean esDoctor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_DOCTOR"));
    }

    private String nombreUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return "equipo GoodDentis";
        }

        return usuarioRepository.findByUsername(authentication.getName())
                .map(Usuario::getNombre)
                .orElse(authentication.getName());
    }

}
