package com.medicitas.view;

import com.medicitas.controller.PacienteController;
import com.medicitas.model.Paciente;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.data.binder.Binder;

@Route("pacientes")
@PageTitle("Pacientes | Medicitas")
public class PacienteView extends VerticalLayout {

    private final PacienteController pacienteController;
    private final Grid<Paciente> grid = new Grid<>(Paciente.class, false);
    private final Binder<Paciente> binder = new Binder<>(Paciente.class);
    private final TextField documento = new TextField("Documento");
    private final TextField nombres = new TextField("Nombres");
    private final TextField apellidos = new TextField("Apellidos");
    private final DatePicker fechaNacimiento = new DatePicker("Fecha de nacimiento");
    private final TextField telefono = new TextField("Telefono");
    private final EmailField correo = new EmailField("Correo");
    private Paciente pacienteSeleccionado;

    public PacienteView(PacienteController pacienteController) {
        this.pacienteController = pacienteController;
        setSizeFull();
        configurarGrid();
        configurarFormulario();
        cargarPacientes();
    }

    private void configurarGrid() {
        grid.addColumn(Paciente::getDocumento).setHeader("Documento").setAutoWidth(true);
        grid.addColumn(Paciente::getNombres).setHeader("Nombres").setAutoWidth(true);
        grid.addColumn(Paciente::getApellidos).setHeader("Apellidos").setAutoWidth(true);
        grid.addColumn(Paciente::getTelefono).setHeader("Telefono").setAutoWidth(true);
        grid.addColumn(Paciente::getCorreo).setHeader("Correo").setAutoWidth(true);
        grid.addColumn(paciente -> paciente.isActivo() ? "Activo" : "Inactivo")
                .setHeader("Estado").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> {
            pacienteSeleccionado = event.getValue();
            if (pacienteSeleccionado == null) {
                binder.setBean(new Paciente());
            } else {
                binder.setBean(pacienteSeleccionado);
            }
        });
    }

    private void configurarFormulario() {
        binder.forField(documento).asRequired("El documento es obligatorio")
                .bind(Paciente::getDocumento, Paciente::setDocumento);
        binder.forField(nombres).asRequired("Los nombres son obligatorios")
                .bind(Paciente::getNombres, Paciente::setNombres);
        binder.forField(apellidos).asRequired("Los apellidos son obligatorios")
                .bind(Paciente::getApellidos, Paciente::setApellidos);
        binder.forField(fechaNacimiento).asRequired("La fecha de nacimiento es obligatoria")
                .bind(Paciente::getFechaNacimiento, Paciente::setFechaNacimiento);
        binder.forField(telefono).asRequired("El telefono es obligatorio")
                .bind(Paciente::getTelefono, Paciente::setTelefono);
        binder.bind(correo, Paciente::getCorreo, Paciente::setCorreo);

        Button guardar = new Button("Guardar", event -> guardar());
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button limpiar = new Button("Limpiar", event -> limpiarFormulario());
        Button eliminar = new Button("Eliminar", event -> eliminar());
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        FormLayout formulario = new FormLayout(documento, nombres, apellidos,
                fechaNacimiento, telefono, correo);
        HorizontalLayout acciones = new HorizontalLayout(guardar, limpiar, eliminar);
        VerticalLayout panel = new VerticalLayout(new H2("Datos del paciente"), formulario, acciones);
        panel.setWidth("360px");

        HorizontalLayout contenido = new HorizontalLayout(panel, grid);
        contenido.setSizeFull();
        contenido.setFlexGrow(1, grid);

        add(new RouterLink("Volver al inicio", MainView.class),
                new H2("Pacientes"), contenido);
    }

    private void guardar() {
        if (binder.validate().hasErrors()) {
            notificar("Revisa los datos ingresados", true);
            return;
        }

        try {
            Paciente formulario = binder.getBean();
            if (pacienteSeleccionado == null) {
                pacienteController.guardar(formulario);
            } else {
                pacienteController.actualizar(pacienteSeleccionado.getId(), formulario);
            }
            notificar("Paciente guardado correctamente", false);
            limpiarFormulario();
            cargarPacientes();
        } catch (RuntimeException exception) {
            notificar(exception.getMessage(), true);
        }
    }

    private void eliminar() {
        if (pacienteSeleccionado == null) {
            notificar("Selecciona un paciente para eliminar", true);
            return;
        }

        try {
            pacienteController.eliminar(pacienteSeleccionado.getId());
            notificar("Paciente eliminado correctamente", false);
            limpiarFormulario();
            cargarPacientes();
        } catch (RuntimeException exception) {
            notificar(exception.getMessage(), true);
        }
    }

    private void limpiarFormulario() {
        pacienteSeleccionado = null;
        grid.asSingleSelect().clear();
        binder.setBean(new Paciente());
    }

    private void cargarPacientes() {
        grid.setItems(pacienteController.listar());
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
