package com.medicitas.controller;

import com.medicitas.model.CitaMedica;
import com.medicitas.service.CitaMedicaService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CitaMedicaController {

    private final CitaMedicaService citaMedicaService;

    public CitaMedicaController(CitaMedicaService citaMedicaService) {
        this.citaMedicaService = citaMedicaService;
    }

    public CitaMedica guardar(CitaMedica cita) {
        return citaMedicaService.guardar(cita);
    }

    public List<CitaMedica> listar() {
        return citaMedicaService.listar();
    }

    public CitaMedica actualizar(Long id, CitaMedica cita) {
        return citaMedicaService.actualizar(id, cita);
    }

    public void cancelar(Long id) {
        citaMedicaService.cancelar(id);
    }

    public void eliminar(Long id) {
        citaMedicaService.eliminar(id);
    }
}
