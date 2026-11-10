package com.medicitas.service;

import com.medicitas.exception.ReglaNegocioException;
import com.medicitas.model.CitaMedica;
import com.medicitas.model.EstadoCita;
import com.medicitas.model.Medico;
import com.medicitas.model.Paciente;
import com.medicitas.repository.CitaMedicaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository citaMedicaRepository;

    @Mock
    private PacienteService pacienteService;

    @Mock
    private MedicoService medicoService;

    @InjectMocks
    private CitaMedicaService citaMedicaService;

    @Test
    void noDebePermitirDosCitasParaElMismoMedicoYHorario() {
        Medico medico = medico(2L, true);
        Paciente paciente = paciente(1L, true);
        CitaMedica cita = cita(null, medico, paciente, LocalDateTime.now().plusDays(1), EstadoCita.PROGRAMADA);
        when(citaMedicaRepository.findByMedicoIdAndFechaHoraBetweenAndEstadoIn(eq(2L), any(), any(), any()))
                .thenReturn(List.of(citaExistente(10L, medico, paciente(9L, true), cita.getFechaHora())));
        when(citaMedicaRepository.findByPacienteIdAndFechaHoraBetweenAndEstadoIn(eq(1L), any(), any(), any()))
                .thenReturn(List.of());

        assertThrows(ReglaNegocioException.class, () -> citaMedicaService.guardar(cita));
    }

    @Test
    void noDebePermitirCitaParaPacienteInactivo() {
        Medico medico = medico(2L, true);
        Paciente paciente = paciente(1L, false);
        CitaMedica cita = cita(null, medico, paciente, LocalDateTime.now().plusDays(1), EstadoCita.PROGRAMADA);
        when(citaMedicaRepository.findByMedicoIdAndFechaHoraBetweenAndEstadoIn(eq(2L), any(), any(), any()))
                .thenReturn(List.of());
        when(citaMedicaRepository.findByPacienteIdAndFechaHoraBetweenAndEstadoIn(eq(1L), any(), any(), any()))
                .thenReturn(List.of());
        when(pacienteService.buscarPorId(1L)).thenReturn(paciente);
        when(medicoService.buscarPorId(2L)).thenReturn(medico);

        assertThrows(ReglaNegocioException.class, () -> citaMedicaService.guardar(cita));
    }

    @Test
    void noDebePermitirCancelarCitaAtendida() {
        CitaMedica cita = cita(5L, medico(2L, true), paciente(1L, true), LocalDateTime.now().plusDays(1), EstadoCita.ATENDIDA);
        when(citaMedicaRepository.findById(5L)).thenReturn(Optional.of(cita));

        assertThrows(ReglaNegocioException.class, () -> citaMedicaService.cancelar(5L));
    }

    @Test
    void noDebePermitirVolverAProgramadaUnaCitaCancelada() {
        CitaMedica actual = cita(5L, medico(2L, true), paciente(1L, true), LocalDateTime.now().plusDays(1), EstadoCita.CANCELADA);
        CitaMedica datos = cita(null, medico(2L, true), paciente(1L, true), LocalDateTime.now().plusDays(2), EstadoCita.PROGRAMADA);
        when(citaMedicaRepository.findById(5L)).thenReturn(Optional.of(actual));

        assertThrows(ReglaNegocioException.class, () -> citaMedicaService.actualizar(5L, datos));
    }

    @Test
    void debeCancelarCitaProgramada() {
        CitaMedica cita = cita(5L, medico(2L, true), paciente(1L, true), LocalDateTime.now().plusDays(1), EstadoCita.PROGRAMADA);
        when(citaMedicaRepository.findById(5L)).thenReturn(Optional.of(cita));
        when(citaMedicaRepository.save(cita)).thenReturn(cita);

        citaMedicaService.cancelar(5L);

        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
        verify(citaMedicaRepository).save(cita);
    }

    private CitaMedica cita(Long id, Medico medico, Paciente paciente, LocalDateTime fechaHora, EstadoCita estado) {
        CitaMedica cita = new CitaMedica(fechaHora, "Consulta general", paciente, medico);
        cita.setId(id);
        cita.setEstado(estado);
        return cita;
    }

    private CitaMedica citaExistente(Long id, Medico medico, Paciente paciente, LocalDateTime fechaHora) {
        return cita(id, medico, paciente, fechaHora, EstadoCita.PROGRAMADA);
    }

    private Medico medico(Long id, boolean activo) {
        Medico medico = new Medico();
        medico.setId(id);
        medico.setActivo(activo);
        return medico;
    }

    private Paciente paciente(Long id, boolean activo) {
        Paciente paciente = new Paciente();
        paciente.setId(id);
        paciente.setActivo(activo);
        return paciente;
    }
}
