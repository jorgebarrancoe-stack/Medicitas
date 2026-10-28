package com.medicitas.service;

import com.medicitas.exception.ReglaNegocioException;
import com.medicitas.exception.RecursoNoEncontradoException;
import com.medicitas.model.Medico;
import com.medicitas.repository.MedicoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicoService.class);

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Transactional
    public Medico guardar(Medico medico) {
        medicoRepository.findByNumeroTarjetaProfesional(medico.getNumeroTarjetaProfesional())
                .ifPresent(existente -> {
                    throw new ReglaNegocioException("Ya existe un medico con la tarjeta profesional indicada");
                });

        Medico guardado = medicoRepository.save(medico);
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
        medicoRepository.delete(medico);
        LOGGER.info("Medico eliminado: id={}", id);
    }
}
