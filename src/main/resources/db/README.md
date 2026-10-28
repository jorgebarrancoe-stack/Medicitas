# Scripts De PostgreSQL

Los scripts deben ejecutarse en la base de datos `medicitas` en este orden:

1. `01_schema.sql`: crea la extension, tablas, relaciones, restricciones e indices.
2. `02_data.sql`: inserta pacientes, medicos y una cita de prueba.

Ejemplo desde `psql`:

```bash
psql -U postgres -d medicitas -f src/main/resources/db/01_schema.sql
psql -U postgres -d medicitas -f src/main/resources/db/02_data.sql
```

La aplicacion usa Hibernate con `ddl-auto=update` para el desarrollo. Los
scripts se conservan como respaldo reproducible de la estructura y los datos.
