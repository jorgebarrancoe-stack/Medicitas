package com.medicitas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "citas_medicas")
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "id_transaccion", nullable = false, unique = true, updatable = false)
    private UUID idTransaccion;

    @NotNull
    @FutureOrPresent
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @NotBlank
    @Column(nullable = false, length = 250)
    private String motivo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public CitaMedica() {
    }

    public CitaMedica(LocalDateTime fechaHora, String motivo,
                      Paciente paciente, Medico medico) {
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.paciente = paciente;
        this.medico = medico;
        this.estado = EstadoCita.PROGRAMADA;
    }

    @PrePersist
    protected void prepararRegistro() {
        if (idTransaccion == null) {
            idTransaccion = generarIdTransaccion();
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public static UUID generarIdTransaccion() {
        return UUID.randomUUID();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(UUID idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    @Override
    public String toString() {
        return "CitaMedica{" +
                "id=" + id +
                ", idTransaccion=" + idTransaccion +
                ", fechaHora=" + fechaHora +
                ", motivo='" + motivo + '\'' +
                ", estado=" + estado +
                ", pacienteId=" + (paciente != null ? paciente.getId() : null) +
                ", medicoId=" + (medico != null ? medico.getId() : null) +
                '}';
    }
}
