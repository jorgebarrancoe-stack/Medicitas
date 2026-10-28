-- Datos iniciales para pruebas de Medicitas.

INSERT INTO pacientes (documento, nombres, apellidos, fecha_nacimiento, telefono, correo)
VALUES
    ('1001001001', 'Ana', 'Martinez', DATE '1995-04-12', '3001001001', 'ana.martinez@example.com'),
    ('1002002002', 'Carlos', 'Rodriguez', DATE '1988-09-25', '3002002002', 'carlos.rodriguez@example.com')
ON CONFLICT (documento) DO NOTHING;

INSERT INTO medicos (numero_tarjeta_profesional, nombres, apellidos, especialidad, correo)
VALUES
    ('TP-001001', 'Laura', 'Gomez', 'Medicina general', 'laura.gomez@example.com'),
    ('TP-002002', 'Andres', 'Perez', 'Pediatria', 'andres.perez@example.com')
ON CONFLICT (numero_tarjeta_profesional) DO NOTHING;

INSERT INTO citas_medicas (
    id_transaccion, fecha_hora, motivo, estado, paciente_id, medico_id
)
SELECT
    '11111111-1111-1111-1111-111111111111',
    CURRENT_TIMESTAMP + INTERVAL '1 day',
    'Consulta de control general',
    'PROGRAMADA',
    paciente.id,
    medico.id
FROM pacientes paciente
JOIN medicos medico ON medico.numero_tarjeta_profesional = 'TP-001001'
WHERE paciente.documento = '1001001001'
  AND NOT EXISTS (
      SELECT 1
      FROM citas_medicas cita
      WHERE cita.id_transaccion = '11111111-1111-1111-1111-111111111111'
  );
