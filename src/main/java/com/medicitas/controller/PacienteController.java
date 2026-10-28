package com.medicitas.controller;

import com.medicitas.model.Paciente;
import com.medicitas.service.PacienteService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    public Paciente guardar(Paciente paciente) {
        return pacienteService.guardar(paciente);
    }

    public List<Paciente> listar() {
        return pacienteService.listar();
    }

    public Paciente actualizar(Long id, Paciente paciente) {
        return pacienteService.actualizar(id, paciente);
    }

    public void eliminar(Long id) {
        pacienteService.eliminar(id);
    }
}
