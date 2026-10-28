package com.medicitas.service;

import com.medicitas.exception.ReglaNegocioException;
import com.medicitas.exception.RecursoNoEncontradoException;
import com.medicitas.model.CitaMedica;
import com.medicitas.model.EstadoCita;
import com.medicitas.model.Medico;
import com.medicitas.model.Paciente;
import com.medicitas.repository.CitaMedicaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaMedicaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CitaMedicaService.class);

    private final CitaMedicaRepository citaMedicaRepository;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;

    public CitaMedicaService(CitaMedicaRepository citaMedicaRepository,
                             PacienteService pacienteService,
                             MedicoService medicoService) {
        this.citaMedicaRepository = citaMedicaRepository;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
    }

    @Transactional
    public CitaMedica guardar(CitaMedica cita) {
        validarDisponibilidad(cita);
        validarParticipantes(cita);

        CitaMedica guardada = citaMedicaRepository.save(cita);
        LOGGER.info("Cita registrada: id={}, transaccion={}",
                guardada.getId(), guardada.getIdTransaccion());
        return guardada;
    }

    @Transactional(readOnly = true)
    public List<CitaMedica> listar() {
        return citaMedicaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CitaMedica buscarPorId(Long id) {
        return citaMedicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada: " + id));
    }

    @Transactional
    public CitaMedica actualizar(Long id, CitaMedica datos) {
        CitaMedica cita = buscarPorId(id);
        datos.setId(id);
        validarDisponibilidad(datos);
        validarParticipantes(datos);

        cita.setFechaHora(datos.getFechaHora());
        cita.setMotivo(datos.getMotivo());
        cita.setEstado(datos.getEstado());
        cita.setPaciente(datos.getPaciente());
        cita.setMedico(datos.getMedico());

        CitaMedica actualizada = citaMedicaRepository.save(cita);
        LOGGER.info("Cita actualizada: id={}", id);
        return actualizada;
    }

    @Transactional
    public void cancelar(Long id) {
        CitaMedica cita = buscarPorId(id);
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new ReglaNegocioException("Una cita atendida no puede cancelarse");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        citaMedicaRepository.save(cita);
        LOGGER.info("Cita cancelada: id={}", id);
    }

    @Transactional(readOnly = true)
    public List<CitaMedica> listarPorPaciente(Long pacienteId) {
        return citaMedicaRepository.findByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    @Transactional(readOnly = true)
    public List<CitaMedica> listarPorMedico(Long medicoId) {
        return citaMedicaRepository.findByMedicoIdOrderByFechaHoraDesc(medicoId);
    }

    @Transactional
    public void eliminar(Long id) {
        CitaMedica cita = buscarPorId(id);
        citaMedicaRepository.delete(cita);
        LOGGER.info("Cita eliminada: id={}", id);
    }

    private void validarDisponibilidad(CitaMedica cita) {
        if (cita.getMedico() == null || cita.getMedico().getId() == null) {
            throw new ReglaNegocioException("La cita debe tener un medico valido");
        }
        if (cita.getFechaHora() == null) {
            throw new ReglaNegocioException("La cita debe tener fecha y hora");
        }

        boolean ocupada = citaMedicaRepository.existsByMedicoIdAndFechaHora(
                cita.getMedico().getId(), cita.getFechaHora());
        if (ocupada && (cita.getId() == null || !mismaCita(cita))) {
            throw new ReglaNegocioException("El medico ya tiene una cita en la fecha y hora indicadas");
        }
    }

    private boolean mismaCita(CitaMedica cita) {
        return citaMedicaRepository.findById(cita.getId())
                .map(actual -> actual.getMedico().getId().equals(cita.getMedico().getId())
                        && actual.getFechaHora().equals(cita.getFechaHora()))
                .orElse(false);
    }

    private void validarParticipantes(CitaMedica cita) {
        if (cita.getPaciente() == null || cita.getPaciente().getId() == null) {
            throw new ReglaNegocioException("La cita debe tener un paciente valido");
        }

        Paciente paciente = pacienteService.buscarPorId(cita.getPaciente().getId());
        Medico medico = medicoService.buscarPorId(cita.getMedico().getId());
        if (!paciente.isActivo()) {
            throw new ReglaNegocioException("El paciente seleccionado esta inactivo");
        }
        if (!medico.isActivo()) {
            throw new ReglaNegocioException("El medico seleccionado esta inactivo");
        }

        cita.setPaciente(paciente);
        cita.setMedico(medico);
    }
}
