package com.medicitas.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Iniciar sesion | GoodDentis")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #e8f7f4, #fff5e6)");

        H1 titulo = new H1("GoodDentis");
        Paragraph descripcion = new Paragraph("Acceso para recepcion y profesionales");
        descripcion.getStyle().set("color", "#627184");

        configurarIdioma();
        add(titulo, descripcion, loginForm);
    }

    private void configurarIdioma() {
        LoginI18n idioma = LoginI18n.createDefault();
        idioma.getForm().setTitle("Iniciar sesion");
        idioma.getForm().setUsername("Usuario");
        idioma.getForm().setPassword("Contrasena");
        idioma.getForm().setSubmit("Ingresar");
        idioma.getForm().setForgotPassword("Olvide mi contrasena");
        idioma.getErrorMessage().setTitle("Datos incorrectos");
        idioma.getErrorMessage().setMessage("Verifica tu usuario y contrasena");
        loginForm.setI18n(idioma);
        loginForm.setAction("login");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
