package com.medicitas.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 titulo = new H1("Medicitas");
        Paragraph descripcion = new Paragraph("Gestion de pacientes, medicos y citas medicas");

        RouterLink pacientes = new RouterLink("Pacientes", PacienteView.class);
        RouterLink medicos = new RouterLink("Medicos", MedicoView.class);
        RouterLink citas = new RouterLink("Citas medicas", CitaMedicaView.class);

        HorizontalLayout menu = new HorizontalLayout(pacientes, medicos, citas);
        menu.setSpacing(true);

        add(titulo, descripcion, menu);
    }
}
