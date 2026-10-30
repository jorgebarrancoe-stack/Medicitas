-- Datos demostrativos del perfil dev. Solo se cargan en H2.

INSERT INTO pacientes (
    documento, nombres, apellidos, fecha_nacimiento, telefono, correo, activo
)
VALUES
    ('1001001001', 'Ana', 'Martinez', DATE '1995-04-12',
     '3001001001', 'ana.martinez@example.com', TRUE),
    ('1002002002', 'Carlos', 'Rodriguez', DATE '1988-09-25',
     '3002002002', 'carlos.rodriguez@example.com', TRUE);

INSERT INTO medicos (
    numero_tarjeta_profesional, nombres, apellidos, especialidad, correo, activo
)
VALUES
    ('TP-001001', 'Laura', 'Gomez', 'Medicina general',
     'laura.gomez@example.com', TRUE),
    ('TP-002002', 'Andres', 'Perez', 'Pediatria',
     'andres.perez@example.com', TRUE);

INSERT INTO citas_medicas (
    id_transaccion, fecha_hora, motivo, estado, paciente_id, medico_id, fecha_creacion
)
SELECT
    '11111111-1111-1111-1111-111111111111',
    TIMESTAMPADD(DAY, 1, CURRENT_TIMESTAMP),
    'Consulta de control general',
    'PROGRAMADA',
    paciente.id,
    medico.id,
    CURRENT_TIMESTAMP
FROM pacientes paciente
JOIN medicos medico ON medico.numero_tarjeta_profesional = 'TP-001001'
WHERE paciente.documento = '1001001001';
