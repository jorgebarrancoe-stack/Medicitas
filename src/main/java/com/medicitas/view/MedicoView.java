package com.medicitas.view;

import com.medicitas.controller.MedicoController;
import com.medicitas.model.Medico;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

@Route("medicos")
@PageTitle("Medicos | Medicitas")
public class MedicoView extends VerticalLayout {

    private final MedicoController medicoController;
    private final Grid<Medico> grid = new Grid<>(Medico.class, false);
    private final Binder<Medico> binder = new Binder<>(Medico.class);
    private final TextField numeroTarjetaProfesional = new TextField("Tarjeta profesional");
    private final TextField nombres = new TextField("Nombres");
    private final TextField apellidos = new TextField("Apellidos");
    private final TextField especialidad = new TextField("Especialidad");
    private final EmailField correo = new EmailField("Correo");
    private Medico medicoSeleccionado;

    public MedicoView(MedicoController medicoController) {
        this.medicoController = medicoController;
        setSizeFull();
        configurarGrid();
        configurarFormulario();
        cargarMedicos();
    }

    private void configurarGrid() {
        grid.addColumn(Medico::getNumeroTarjetaProfesional).setHeader("Tarjeta profesional").setAutoWidth(true);
        grid.addColumn(Medico::getNombres).setHeader("Nombres").setAutoWidth(true);
        grid.addColumn(Medico::getApellidos).setHeader("Apellidos").setAutoWidth(true);
        grid.addColumn(Medico::getEspecialidad).setHeader("Especialidad").setAutoWidth(true);
        grid.addColumn(Medico::getCorreo).setHeader("Correo").setAutoWidth(true);
        grid.addColumn(medico -> medico.isActivo() ? "Activo" : "Inactivo")
                .setHeader("Estado").setAutoWidth(true);
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> {
            medicoSeleccionado = event.getValue();
            binder.setBean(medicoSeleccionado == null ? new Medico() : medicoSeleccionado);
        });
    }

    private void configurarFormulario() {
        binder.forField(numeroTarjetaProfesional).asRequired("La tarjeta profesional es obligatoria")
                .bind(Medico::getNumeroTarjetaProfesional, Medico::setNumeroTarjetaProfesional);
        binder.forField(nombres).asRequired("Los nombres son obligatorios")
                .bind(Medico::getNombres, Medico::setNombres);
        binder.forField(apellidos).asRequired("Los apellidos son obligatorios")
                .bind(Medico::getApellidos, Medico::setApellidos);
        binder.forField(especialidad).asRequired("La especialidad es obligatoria")
                .bind(Medico::getEspecialidad, Medico::setEspecialidad);
        binder.bind(correo, Medico::getCorreo, Medico::setCorreo);

        Button guardar = new Button("Guardar", event -> guardar());
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button limpiar = new Button("Limpiar", event -> limpiarFormulario());
        Button eliminar = new Button("Eliminar", event -> eliminar());
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        FormLayout formulario = new FormLayout(numeroTarjetaProfesional, nombres, apellidos,
                especialidad, correo);
        HorizontalLayout acciones = new HorizontalLayout(guardar, limpiar, eliminar);
        VerticalLayout panel = new VerticalLayout(new H2("Datos del medico"), formulario, acciones);
        panel.setWidth("360px");

        HorizontalLayout contenido = new HorizontalLayout(panel, grid);
        contenido.setSizeFull();
        contenido.setFlexGrow(1, grid);

        add(new RouterLink("Volver al inicio", MainView.class),
                new H2("Medicos"), contenido);
    }

    private void guardar() {
        if (binder.validate().hasErrors()) {
            notificar("Revisa los datos ingresados", true);
            return;
        }

        try {
            Medico formulario = binder.getBean();
            if (medicoSeleccionado == null) {
                medicoController.guardar(formulario);
            } else {
                medicoController.actualizar(medicoSeleccionado.getId(), formulario);
            }
            notificar("Medico guardado correctamente", false);
            limpiarFormulario();
            cargarMedicos();
        } catch (RuntimeException exception) {
            notificar(exception.getMessage(), true);
        }
    }

    private void eliminar() {
        if (medicoSeleccionado == null) {
            notificar("Selecciona un medico para eliminar", true);
            return;
        }

        try {
            medicoController.eliminar(medicoSeleccionado.getId());
            notificar("Medico eliminado correctamente", false);
            limpiarFormulario();
            cargarMedicos();
        } catch (RuntimeException exception) {
            notificar(exception.getMessage(), true);
        }
    }

    private void limpiarFormulario() {
        medicoSeleccionado = null;
        grid.asSingleSelect().clear();
        binder.setBean(new Medico());
    }

    private void cargarMedicos() {
        grid.setItems(medicoController.listar());
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
