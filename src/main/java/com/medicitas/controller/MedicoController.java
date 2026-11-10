package com.medicitas.controller;

import com.medicitas.model.Medico;
import com.medicitas.service.MedicoService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    public Medico guardar(Medico medico) {
        return medicoService.guardar(medico);
    }

    public Medico guardar(Medico medico, String username, String password) {
        return medicoService.guardar(medico, username, password);
    }

    public List<Medico> listar() {
        return medicoService.listar();
    }

    public List<Medico> listarActivos() {
        return medicoService.listarActivos();
    }

    public long contar() {
        return medicoService.contar();
    }

    public Medico actualizar(Long id, Medico medico) {
        return medicoService.actualizar(id, medico);
    }

    public void eliminar(Long id) {
        medicoService.eliminar(id);
    }
}
