package com.medicitas.repository;

import com.medicitas.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByNumeroTarjetaProfesional(String numeroTarjetaProfesional);
}
