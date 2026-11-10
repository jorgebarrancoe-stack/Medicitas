package com.medicitas.view;

import com.medicitas.controller.CitaMedicaController;
import com.medicitas.model.CitaMedica;
import com.medicitas.model.Usuario;
import com.medicitas.repository.UsuarioRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;

@Route("mi-agenda")
@PageTitle("Mi agenda | GoodDentis")
@RolesAllowed("DOCTOR")
public class DoctorAgendaView extends VerticalLayout {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DoctorAgendaView(UsuarioRepository usuarioRepository,
                            CitaMedicaController citaController) {
        setSizeFull();
        setPadding(true);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();

        H2 titulo = new H2("Mi agenda");
        Paragraph descripcion = new Paragraph("Citas asignadas a " + usuario.getNombre());
        Grid<CitaMedica> grid = crearGrid();
        grid.setItems(citaController.listarPorMedico(usuario.getMedico().getId()));

        add(new RouterLink("Volver al inicio", MainView.class), titulo, descripcion, grid);
        expand(grid);
    }

    private Grid<CitaMedica> crearGrid() {
        Grid<CitaMedica> grid = new Grid<>(CitaMedica.class, false);
        grid.addColumn(cita -> cita.getFechaHora() == null
                        ? ""
                        : cita.getFechaHora().format(FORMATO_FECHA_HORA))
                .setHeader("Fecha y hora").setAutoWidth(true);
        grid.addColumn(cita -> cita.getPaciente().getNombres() + " "
                        + cita.getPaciente().getApellidos())
                .setHeader("Paciente").setAutoWidth(true);
        grid.addColumn(CitaMedica::getMotivo).setHeader("Motivo").setAutoWidth(true);
        grid.addColumn(CitaMedica::getEstado).setHeader("Estado").setAutoWidth(true);
        grid.setSizeFull();
        return grid;
    }
}
