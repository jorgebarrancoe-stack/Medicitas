package com.medicitas.repository;

import com.medicitas.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDocumento(String documento);
}
