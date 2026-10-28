package com.medicitas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "numero_tarjeta_profesional", nullable = false, unique = true, length = 40)
    private String numeroTarjetaProfesional;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String nombres;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String apellidos;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String especialidad;

    @Email
    @Column(length = 120)
    private String correo;

    @Column(nullable = false)
    private boolean activo = true;

    public Medico() {
    }

    public Medico(String numeroTarjetaProfesional, String nombres, String apellidos,
                  String especialidad, String correo) {
        this.numeroTarjetaProfesional = numeroTarjetaProfesional;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.correo = correo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroTarjetaProfesional() {
        return numeroTarjetaProfesional;
    }

    public void setNumeroTarjetaProfesional(String numeroTarjetaProfesional) {
        this.numeroTarjetaProfesional = numeroTarjetaProfesional;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Medico{" +
                "id=" + id +
                ", numeroTarjetaProfesional='" + numeroTarjetaProfesional + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", especialidad='" + especialidad + '\'' +
                '}';
    }
}
