package com.medicitas.repository;

import com.medicitas.model.CitaMedica;
import com.medicitas.model.EstadoCita;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    @Override
    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<CitaMedica> findAll();

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<CitaMedica> findByMedicoIdAndFechaHoraBetweenAndEstadoIn(Long medicoId,
                                                                  LocalDateTime desde,
                                                                  LocalDateTime hasta,
                                                                  Collection<EstadoCita> estados);

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<CitaMedica> findByPacienteIdAndFechaHoraBetweenAndEstadoIn(Long pacienteId,
                                                                    LocalDateTime desde,
                                                                    LocalDateTime hasta,
                                                                    Collection<EstadoCita> estados);

    boolean existsByIdTransaccion(UUID idTransaccion);

    boolean existsByPacienteId(Long pacienteId);

    boolean existsByMedicoId(Long medicoId);

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<CitaMedica> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<CitaMedica> findByMedicoIdOrderByFechaHoraDesc(Long medicoId);
}
