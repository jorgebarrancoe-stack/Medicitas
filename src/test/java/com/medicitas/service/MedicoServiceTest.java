package com.medicitas.service;

import com.medicitas.exception.ReglaNegocioException;
import com.medicitas.model.Medico;
import com.medicitas.repository.CitaMedicaRepository;
import com.medicitas.repository.MedicoRepository;
import com.medicitas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CitaMedicaRepository citaMedicaRepository;

    @InjectMocks
    private MedicoService medicoService;

    @Test
    void noDebePermitirMasDeCincoMedicosActivosAlGuardar() {
        Medico medico = medico(null, "TP-100", true);
        when(medicoRepository.countByActivoTrue()).thenReturn(5L);

        assertThrows(ReglaNegocioException.class,
                () -> medicoService.guardar(medico, "nuevo", "clave123"));
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    void noDebePermitirActivarMedicoSiYaHayCincoActivos() {
        Medico existente = medico(1L, "TP-100", false);
        Medico datos = medico(null, "TP-100", true);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(medicoRepository.countByActivoTrue()).thenReturn(5L);

        assertThrows(ReglaNegocioException.class,
                () -> medicoService.actualizar(1L, datos));
    }

    private Medico medico(Long id, String tarjeta, boolean activo) {
        Medico medico = new Medico(tarjeta, "Nombre", "Apellido", "Odontologia general", "correo@example.com");
        medico.setId(id);
        medico.setActivo(activo);
        return medico;
    }
}
