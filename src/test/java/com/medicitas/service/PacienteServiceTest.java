package com.medicitas.service;

import com.medicitas.exception.ReglaNegocioException;
import com.medicitas.exception.RecursoNoEncontradoException;
import com.medicitas.model.Paciente;
import com.medicitas.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    @Test
    void debeGuardarPacienteConDocumentoNuevo() {
        Paciente paciente = crearPaciente("1001001001");
        when(pacienteRepository.findByDocumento(paciente.getDocumento()))
                .thenReturn(Optional.empty());
        when(pacienteRepository.save(paciente)).thenReturn(paciente);

        Paciente resultado = pacienteService.guardar(paciente);

        assertEquals(paciente, resultado);
        verify(pacienteRepository).save(paciente);
    }

    @Test
    void noDebeGuardarPacienteConDocumentoDuplicado() {
        Paciente paciente = crearPaciente("1001001001");
        when(pacienteRepository.findByDocumento(paciente.getDocumento()))
                .thenReturn(Optional.of(paciente));

        assertThrows(ReglaNegocioException.class, () -> pacienteService.guardar(paciente));
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void debeLanzarExcepcionCuandoPacienteNoExiste() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> pacienteService.buscarPorId(99L));
    }

    private Paciente crearPaciente(String documento) {
        return new Paciente(documento, "Ana", "Martinez",
                LocalDate.of(1995, 4, 12), "3001001001", "ana@example.com");
    }
}
