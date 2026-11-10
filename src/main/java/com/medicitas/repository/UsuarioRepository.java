package com.medicitas.repository;

import com.medicitas.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "medico")
    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
