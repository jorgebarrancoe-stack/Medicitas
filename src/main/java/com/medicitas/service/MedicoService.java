package com.medicitas.service;

import com.medicitas.exception.ReglaNegocioException;
import com.medicitas.exception.RecursoNoEncontradoException;
import com.medicitas.model.Medico;
import com.medicitas.model.RolUsuario;
import com.medicitas.model.Usuario;
import com.medicitas.repository.MedicoRepository;
import com.medicitas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicoService.class);

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public MedicoService(MedicoRepository medicoRepository,
                         UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Medico guardar(Medico medico) {
        return guardar(medico, null, null);
    }

    @Transactional
    public Medico guardar(Medico medico, String username, String password) {
        if (medicoRepository.countByActivoTrue() >= 5) {
            throw new ReglaNegocioException("GoodDentis permite maximo 5 medicos activos");
        }

        medicoRepository.findByNumeroTarjetaProfesional(medico.getNumeroTarjetaProfesional())
                .ifPresent(existente -> {
                    throw new ReglaNegocioException("Ya existe un medico con la tarjeta profesional indicada");
                });

        Medico guardado = medicoRepository.save(medico);
        if (username != null && !username.isBlank() && password != null && !password.isBlank()) {
            if (usuarioRepository.existsByUsername(username)) {
                throw new ReglaNegocioException("El usuario del medico ya existe");
            }
            Usuario usuario = new Usuario(username, passwordEncoder.encode(password),
                    "Dr. " + medico.getNombres() + " " + medico.getApellidos(), RolUsuario.DOCTOR);
            usuario.setMedico(guardado);
            usuarioRepository.save(usuario);
        }
        LOGGER.info("Medico registrado: id={}, tarjeta={}",
                guardado.getId(), guardado.getNumeroTarjetaProfesional());
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<Medico> listar() {
        return medicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Medico buscarPorId(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico no encontrado: " + id));
    }

    @Transactional
    public Medico actualizar(Long id, Medico datos) {
        Medico medico = buscarPorId(id);
        medicoRepository.findByNumeroTarjetaProfesional(datos.getNumeroTarjetaProfesional())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe otro medico con la tarjeta profesional indicada");
                });

        medico.setNumeroTarjetaProfesional(datos.getNumeroTarjetaProfesional());
        medico.setNombres(datos.getNombres());
        medico.setApellidos(datos.getApellidos());
        medico.setEspecialidad(datos.getEspecialidad());
        medico.setCorreo(datos.getCorreo());
        medico.setActivo(datos.isActivo());

        Medico actualizado = medicoRepository.save(medico);
        LOGGER.info("Medico actualizado: id={}", id);
        return actualizado;
    }

    @Transactional
    public void eliminar(Long id) {
        Medico medico = buscarPorId(id);
        usuarioRepository.findByMedicoId(id).ifPresent(usuario -> {
            usuario.setActivo(false);
            usuario.setMedico(null);
            usuarioRepository.save(usuario);
        });
        medicoRepository.delete(medico);
        LOGGER.info("Medico eliminado: id={}", id);
    }
}
