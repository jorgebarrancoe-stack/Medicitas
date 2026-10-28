-- Estructura de la base de datos Medicitas.
-- Ejecutar sobre la base de datos medicitas.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS pacientes (
    id BIGSERIAL PRIMARY KEY,
    documento VARCHAR(30) NOT NULL UNIQUE,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(120),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS medicos (
    id BIGSERIAL PRIMARY KEY,
    numero_tarjeta_profesional VARCHAR(40) NOT NULL UNIQUE,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    especialidad VARCHAR(80) NOT NULL,
    correo VARCHAR(120),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS citas_medicas (
    id BIGSERIAL PRIMARY KEY,
    id_transaccion UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    fecha_hora TIMESTAMP NOT NULL,
    motivo VARCHAR(250) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA',
    paciente_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT citas_estado_valido
        CHECK (estado IN ('PROGRAMADA', 'CONFIRMADA', 'ATENDIDA', 'CANCELADA')),
    CONSTRAINT citas_paciente_fk
        FOREIGN KEY (paciente_id) REFERENCES pacientes (id),
    CONSTRAINT citas_medico_fk
        FOREIGN KEY (medico_id) REFERENCES medicos (id),
    CONSTRAINT citas_medico_fecha_unica
        UNIQUE (medico_id, fecha_hora)
);

CREATE INDEX IF NOT EXISTS idx_citas_paciente
    ON citas_medicas (paciente_id);

CREATE INDEX IF NOT EXISTS idx_citas_medico
    ON citas_medicas (medico_id);

CREATE INDEX IF NOT EXISTS idx_citas_fecha_hora
    ON citas_medicas (fecha_hora);
