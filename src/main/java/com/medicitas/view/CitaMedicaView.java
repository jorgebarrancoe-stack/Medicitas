package com.medicitas.view;

import com.medicitas.controller.CitaMedicaController;
import com.medicitas.controller.MedicoController;
import com.medicitas.controller.PacienteController;
import com.medicitas.model.CitaMedica;
import com.medicitas.model.EstadoCita;
import com.medicitas.model.Medico;
import com.medicitas.model.Paciente;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("citas")
@PageTitle("Citas medicas | Medicitas")
public class CitaMedicaView extends VerticalLayout {

    private final CitaMedicaController citaController;
    private final PacienteController pacienteController;
    private final MedicoController medicoController;
    private final Grid<CitaMedica> grid = new Grid<>(CitaMedica.class, false);
    private final Binder<CitaMedica> binder = new Binder<>(CitaMedica.class);
    private final DateTimePicker fechaHora = new DateTimePicker("Fecha y hora");
    private final TextArea motivo = new TextArea("Motivo");
    private final ComboBox<EstadoCita> estado = new ComboBox<>("Estado");
    private final ComboBox<Paciente> paciente = new ComboBox<>("Paciente");
    private final ComboBox<Medico> medico = new ComboBox<>("Medico");
    private CitaMedica citaSeleccionada;

    public CitaMedicaView(CitaMedicaController citaController,
                          PacienteController pacienteController,
                          MedicoController medicoController) {
        this.citaController = citaController;
        this.pacienteController = pacienteController;
        this.medicoController = medicoController;
        setSizeFull();
        configurarGrid();
        configurarFormulario();
        cargarDatos();
    }

    private void configurarGrid() {
        grid.addColumn(cita -> cita.getIdTransaccion() == null
                        ? ""
                        : cita.getIdTransaccion().toString())
                .setHeader("Transaccion").setAutoWidth(true);
        grid.addColumn(CitaMedica::getFechaHora).setHeader("Fecha y hora").setAutoWidth(true);
        grid.addColumn(cita -> nombrePaciente(cita.getPaciente()))
                .setHeader("Paciente").setAutoWidth(true);
        grid.addColumn(cita -> nombreMedico(cita.getMedico()))
                .setHeader("Medico").setAutoWidth(true);
        grid.addColumn(CitaMedica::getEstado).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(CitaMedica::getMotivo).setHeader("Motivo").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> {
            citaSeleccionada = event.getValue();
            binder.setBean(citaSeleccionada == null ? nuevaCita() : citaSeleccionada);
        });
    }

    private void configurarFormulario() {
        paciente.setItemLabelGenerator(this::nombrePaciente);
        medico.setItemLabelGenerator(this::nombreMedico);
        estado.setItems(EstadoCita.values());

        binder.forField(fechaHora).asRequired("La fecha y hora son obligatorias")
                .bind(CitaMedica::getFechaHora, CitaMedica::setFechaHora);
        binder.forField(motivo).asRequired("El motivo es obligatorio")
                .bind(CitaMedica::getMotivo, CitaMedica::setMotivo);
        binder.forField(estado).asRequired("El estado es obligatorio")
                .bind(CitaMedica::getEstado, CitaMedica::setEstado);
        binder.forField(paciente).asRequired("El paciente es obligatorio")
                .bind(CitaMedica::getPaciente, CitaMedica::setPaciente);
        binder.forField(medico).asRequired("El medico es obligatorio")
                .bind(CitaMedica::getMedico, CitaMedica::setMedico);

        Button guardar = new Button("Guardar", event -> guardar());
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button limpiar = new Button("Limpiar", event -> limpiarFormulario());
        Button cancelar = new Button("Cancelar cita", event -> cancelar());
        cancelar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button eliminar = new Button("Eliminar", event -> eliminar());
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        FormLayout formulario = new FormLayout(fechaHora, paciente, medico, estado, motivo);
        formulario.setColspan(motivo, 2);
        HorizontalLayout acciones = new HorizontalLayout(guardar, limpiar, cancelar, eliminar);
        VerticalLayout panel = new VerticalLayout(new H2("Datos de la cita"), formulario, acciones);
        panel.setWidth("390px");

        HorizontalLayout contenido = new HorizontalLayout(panel, grid);
        contenido.setSizeFull();
        contenido.setFlexGrow(1, grid);

        add(new RouterLink("Volver al inicio", MainView.class),
                new H2("Citas medicas"), contenido);
    }

    private void guardar() {
        if (binder.validate().hasErrors()) {
            notificar("Revisa los datos ingresados", true);
            return;
        }

        try {
            CitaMedica formulario = binder.getBean();
            if (citaSeleccionada == null) {
                citaController.guardar(formulario);
            } else {
                citaController.actualizar(citaSeleccionada.getId(), formulario);
            }
            notificar("Cita guardada correctamente", false);
            limpiarFormulario();
            cargarCitas();
        } catch (RuntimeException exception) {
            notificar(exception.getMessage(), true);
        }
    }

    private void cancelar() {
        if (citaSeleccionada == null) {
            notificar("Selecciona una cita para cancelar", true);
            return;
        }

        try {
            citaController.cancelar(citaSeleccionada.getId());
            notificar("Cita cancelada correctamente", false);
            limpiarFormulario();
            cargarCitas();
        } catch (RuntimeException exception) {
            notificar(exception.getMessage(), true);
        }
    }

    private void eliminar() {
        if (citaSeleccionada == null) {
            notificar("Selecciona una cita para eliminar", true);
            return;
        }

        try {
            citaController.eliminar(citaSeleccionada.getId());
            notificar("Cita eliminada correctamente", false);
            limpiarFormulario();
            cargarCitas();
        } catch (RuntimeException exception) {
            notificar(exception.getMessage(), true);
        }
    }

    private void cargarDatos() {
        paciente.setItems(pacienteController.listar());
        medico.setItems(medicoController.listar());
        cargarCitas();
        binder.setBean(nuevaCita());
    }

    private void cargarCitas() {
        grid.setItems(citaController.listar());
    }

    private CitaMedica nuevaCita() {
        CitaMedica cita = new CitaMedica();
        cita.setEstado(EstadoCita.PROGRAMADA);
        return cita;
    }

    private void limpiarFormulario() {
        citaSeleccionada = null;
        grid.asSingleSelect().clear();
        binder.setBean(nuevaCita());
    }

    private String nombrePaciente(Paciente paciente) {
        if (paciente == null) {
            return "";
        }
        return paciente.getDocumento() + " - " + paciente.getNombres() + " " + paciente.getApellidos();
    }

    private String nombreMedico(Medico medico) {
        if (medico == null) {
            return "";
        }
        return medico.getNombres() + " " + medico.getApellidos() + " - " + medico.getEspecialidad();
    }

    private void notificar(String mensaje, boolean error) {
        Notification notification = Notification.show(mensaje, 3500, Position.BOTTOM_END);
        if (error) {
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
    }
}
