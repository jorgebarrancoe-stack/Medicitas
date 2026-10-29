package com.medicitas.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitaMedicaTest {

    @Test
    void debeGenerarUnIdDeTransaccionUnico() {
        UUID primeraTransaccion = CitaMedica.generarIdTransaccion();
        UUID segundaTransaccion = CitaMedica.generarIdTransaccion();

        assertNotNull(primeraTransaccion);
        assertNotNull(segundaTransaccion);
        assertNotEquals(primeraTransaccion, segundaTransaccion);
    }

    @Test
    void debeCrearCitaConEstadoProgramado() {
        CitaMedica cita = new CitaMedica();

        assertTrue(cita.getEstado() == EstadoCita.PROGRAMADA);
    }
}
