package com.medicitas.service;

import com.medicitas.exception.ReglaNegocioException;
import com.medicitas.exception.RecursoNoEncontradoException;
import com.medicitas.model.Paciente;
import com.medicitas.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacienteService.class);

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public Paciente guardar(Paciente paciente) {
        pacienteRepository.findByDocumento(paciente.getDocumento())
                .ifPresent(existente -> {
                    throw new ReglaNegocioException("Ya existe un paciente con el documento indicado");
                });

        Paciente guardado = pacienteRepository.save(paciente);
        LOGGER.info("Paciente registrado: id={}, documento={}", guardado.getId(), guardado.getDocumento());
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Paciente> listar() {
        return pacienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado: " + id));
    }

    @Transactional
    public Paciente actualizar(Long id, Paciente datos) {
        Paciente paciente = buscarPorId(id);
        pacienteRepository.findByDocumento(datos.getDocumento())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe otro paciente con el documento indicado");
                });

        paciente.setDocumento(datos.getDocumento());
        paciente.setNombres(datos.getNombres());
        paciente.setApellidos(datos.getApellidos());
        paciente.setFechaNacimiento(datos.getFechaNacimiento());
        paciente.setTelefono(datos.getTelefono());
        paciente.setCorreo(datos.getCorreo());
        paciente.setActivo(datos.isActivo());

        Paciente actualizado = pacienteRepository.save(paciente);
        LOGGER.info("Paciente actualizado: id={}", id);
        return actualizado;
    }

    @Transactional
    public void eliminar(Long id) {
        Paciente paciente = buscarPorId(id);
        pacienteRepository.delete(paciente);
        LOGGER.info("Paciente eliminado: id={}", id);
    }
}
