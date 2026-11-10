package com.medicitas.repository;

import com.medicitas.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    long countByActivoTrue();

    List<Medico> findByActivoTrue();

    Optional<Medico> findByNumeroTarjetaProfesional(String numeroTarjetaProfesional);
}
