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
import java.util.EnumSet;
import java.util.List;

@Service
public class CitaMedicaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CitaMedicaService.class);
    private static final int DURACION_MINUTOS = 30;
    private static final EnumSet<EstadoCita> ESTADOS_ACTIVOS =
            EnumSet.of(EstadoCita.PROGRAMADA, EstadoCita.CONFIRMADA, EstadoCita.ATENDIDA);

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
        if (cita.getFechaHora() == null) {
            throw new ReglaNegocioException("La cita debe tener fecha y hora");
        }
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("La cita debe ser en una fecha futura");
        }
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
    public long contar() {
        return citaMedicaRepository.count();
    }

    @Transactional(readOnly = true)
    public CitaMedica buscarPorId(Long id) {
        return citaMedicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada: " + id));
    }

    @Transactional
    public CitaMedica actualizar(Long id, CitaMedica datos) {
        CitaMedica cita = buscarPorId(id);
        validarCambioEstado(cita.getEstado(), datos.getEstado());
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
        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new ReglaNegocioException("Una cita atendida no puede eliminarse");
        }
        citaMedicaRepository.delete(cita);
        LOGGER.info("Cita eliminada: id={}", id);
    }

    private void validarCambioEstado(EstadoCita actual, EstadoCita nuevo) {
        if (nuevo == null) {
            throw new ReglaNegocioException("El estado es obligatorio");
        }
        if ((actual == EstadoCita.CANCELADA || actual == EstadoCita.ATENDIDA)
                && nuevo == EstadoCita.PROGRAMADA) {
            throw new ReglaNegocioException("Una cita cancelada o atendida no puede volver a programada");
        }
    }

    private void validarDisponibilidad(CitaMedica cita) {
        if (cita.getMedico() == null || cita.getMedico().getId() == null) {
            throw new ReglaNegocioException("La cita debe tener un medico valido");
        }
        if (cita.getPaciente() == null || cita.getPaciente().getId() == null) {
            throw new ReglaNegocioException("La cita debe tener un paciente valido");
        }
        if (cita.getFechaHora() == null) {
            throw new ReglaNegocioException("La cita debe tener fecha y hora");
        }

        LocalDateTime inicio = cita.getFechaHora();
        LocalDateTime fin = inicio.plusMinutes(DURACION_MINUTOS);

        boolean medicoOcupado = citaMedicaRepository
                .findByMedicoIdAndFechaHoraBetweenAndEstadoIn(
                        cita.getMedico().getId(), inicio.minusMinutes(DURACION_MINUTOS - 1), fin.minusMinutes(1), ESTADOS_ACTIVOS)
                .stream().anyMatch(existente -> esCruce(existente, cita, fin));
        if (medicoOcupado) {
            throw new ReglaNegocioException("El medico ya tiene una cita en el horario indicado");
        }

        boolean pacienteOcupado = citaMedicaRepository
                .findByPacienteIdAndFechaHoraBetweenAndEstadoIn(
                        cita.getPaciente().getId(), inicio.minusMinutes(DURACION_MINUTOS - 1), fin.minusMinutes(1), ESTADOS_ACTIVOS)
                .stream().anyMatch(existente -> esCruce(existente, cita, fin));
        if (pacienteOcupado) {
            throw new ReglaNegocioException("El paciente ya tiene una cita en el horario indicado");
        }
    }

    private boolean esCruce(CitaMedica existente, CitaMedica cita, LocalDateTime finNueva) {
        if (cita.getId() != null && cita.getId().equals(existente.getId())) {
            return false;
        }
        LocalDateTime inicioExistente = existente.getFechaHora();
        LocalDateTime finExistente = inicioExistente.plusMinutes(DURACION_MINUTOS);
        return cita.getFechaHora().isBefore(finExistente) && inicioExistente.isBefore(finNueva);
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
