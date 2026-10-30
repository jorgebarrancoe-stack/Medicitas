package com.medicitas.service;

import com.medicitas.exception.ReglaNegocioException;
import com.medicitas.model.CitaMedica;
import com.medicitas.model.Medico;
import com.medicitas.model.Paciente;
import com.medicitas.repository.CitaMedicaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
        CitaMedica cita = cita(1L, medico, paciente(1L, true));
        when(citaMedicaRepository.existsByMedicoIdAndFechaHora(2L, cita.getFechaHora()))
                .thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> citaMedicaService.guardar(cita));
    }

    @Test
    void noDebePermitirCitaParaPacienteInactivo() {
        Medico medico = medico(2L, true);
        Paciente paciente = paciente(1L, false);
        CitaMedica cita = cita(null, medico, paciente);
        when(citaMedicaRepository.existsByMedicoIdAndFechaHora(2L, cita.getFechaHora()))
                .thenReturn(false);
        when(pacienteService.buscarPorId(1L)).thenReturn(paciente);
        when(medicoService.buscarPorId(2L)).thenReturn(medico);

        assertThrows(ReglaNegocioException.class, () -> citaMedicaService.guardar(cita));
    }

    private CitaMedica cita(Long id, Medico medico, Paciente paciente) {
        CitaMedica cita = new CitaMedica(LocalDateTime.now().plusDays(1),
                "Consulta general", paciente, medico);
        cita.setId(id);
        return cita;
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
