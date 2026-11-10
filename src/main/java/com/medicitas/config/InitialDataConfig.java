package com.medicitas.config;

import com.medicitas.model.Medico;
import com.medicitas.model.RolUsuario;
import com.medicitas.model.Usuario;
import com.medicitas.repository.MedicoRepository;
import com.medicitas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class InitialDataConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataConfig.class);

    @Bean
    CommandLineRunner cargarUsuariosIniciales(
            UsuarioRepository usuarioRepository,
            MedicoRepository medicoRepository,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_INITIAL_PASSWORD:recepcion123}") String passwordAdmin,
            @Value("${DOCTOR_INITIAL_PASSWORD:doctor123}") String passwordDoctor) {
        return args -> {
            crearAdministrador(usuarioRepository, passwordEncoder, passwordAdmin);
            crearDoctores(usuarioRepository, medicoRepository, passwordEncoder, passwordDoctor);
        };
    }

    private void crearAdministrador(UsuarioRepository usuarioRepository,
                                    PasswordEncoder passwordEncoder,
                                    String password) {
        if (usuarioRepository.existsByUsername("recepcion")) {
            return;
        }

        Usuario administrador = new Usuario(
                "recepcion",
                passwordEncoder.encode(password),
                "Recepcion GoodDentis",
                RolUsuario.ADMIN);
        usuarioRepository.save(administrador);
        LOGGER.info("Usuario administrador inicial creado: recepcion");
    }

    private void crearDoctores(UsuarioRepository usuarioRepository,
                               MedicoRepository medicoRepository,
                               PasswordEncoder passwordEncoder,
                               String password) {
        List<DoctorInicial> doctores = List.of(
                new DoctorInicial("laura.gomez", "doctor1", "Laura", "Gomez", "Odontologia general", "TP-000001"),
                new DoctorInicial("andres.perez", "doctor2", "Andres", "Perez", "Ortodoncia", "TP-000002"),
                new DoctorInicial("valentina.rojas", "doctor3", "Valentina", "Rojas", "Periodoncia", "TP-000003"),
                new DoctorInicial("santiago.torres", "doctor4", "Santiago", "Torres", "Endodoncia", "TP-000004"),
                new DoctorInicial("camila.moreno", "doctor5", "Camila", "Moreno", "Odontopediatria", "TP-000005"));

        for (DoctorInicial inicial : doctores) {
            Usuario usuarioExistente = usuarioRepository.findByUsername(inicial.username())
                    .orElseGet(() -> usuarioRepository.findByUsername(inicial.usuarioAnterior()).orElse(null));
            if (usuarioExistente != null) {
                if (usuarioExistente.getUsername().equals(inicial.usuarioAnterior())) {
                    usuarioExistente.setUsername(inicial.username());
                    usuarioExistente.setNombre("Dr. " + inicial.nombres() + " " + inicial.apellidos());
                    usuarioRepository.save(usuarioExistente);
                    LOGGER.info("Usuario de doctor actualizado: {}", inicial.username());
                }
                continue;
            }

            Medico medico = medicoRepository.findByNumeroTarjetaProfesional(inicial.tarjeta())
                    .orElseGet(() -> medicoRepository.save(new Medico(
                            inicial.tarjeta(), inicial.nombres(), inicial.apellidos(),
                            inicial.especialidad(), inicial.username() + "@gooddentis.local")));

            Usuario usuario = new Usuario(
                    inicial.username(),
                    passwordEncoder.encode(password),
                    "Dr. " + inicial.nombres() + " " + inicial.apellidos(),
                    RolUsuario.DOCTOR);
            usuario.setMedico(medico);
            usuarioRepository.save(usuario);
        }
        LOGGER.info("Usuarios iniciales de doctores verificados");
    }

    private record DoctorInicial(String username, String usuarioAnterior,
                                 String nombres, String apellidos,
                                 String especialidad, String tarjeta) {
    }
}
