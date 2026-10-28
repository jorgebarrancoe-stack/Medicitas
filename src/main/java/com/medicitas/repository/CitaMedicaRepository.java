package com.medicitas.repository;

import com.medicitas.model.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    boolean existsByMedicoIdAndFechaHora(Long medicoId, LocalDateTime fechaHora);

    boolean existsByIdTransaccion(UUID idTransaccion);

    List<CitaMedica> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    List<CitaMedica> findByMedicoIdOrderByFechaHoraDesc(Long medicoId);
}
