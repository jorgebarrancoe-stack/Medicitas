# Medicitas

Sistema de gestión de citas médicas desarrollado con Spring Boot, Vaadin,
Spring Data JPA y PostgreSQL.

## Requisitos

- Java 21
- Maven 3.9 o superior
- PostgreSQL

## Configuración local

Crear una base de datos llamada `medicitas` y configurar estas variables de
entorno:

```text
DB_URL=jdbc:postgresql://localhost:5432/medicitas
DB_USERNAME=postgres
DB_PASSWORD=tu_contraseña
```

Como alternativa en Windows, copia `.env.example` como `.env` y completa
`DB_PASSWORD`. El archivo `.env` esta excluido de Git y no debe publicarse.

## Ejecutar

```bash
mvn spring-boot:run
```

Para revisar la interfaz sin configurar PostgreSQL todavía, usar el perfil de
desarrollo con H2:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Después abrir `http://localhost:8080`. Este perfil usa una base de datos en
memoria y pierde los datos al detener la aplicación; PostgreSQL sigue siendo
la base de datos oficial del proyecto.

## Ejecutar pruebas

Se recomienda usar Java 21 o una version compatible con la version de Vaadin
del proyecto. Las pruebas unitarias se pueden ejecutar con:

```bash
mvn test
```

Las pruebas cubren la generacion de UUID, el registro de pacientes y las
reglas de disponibilidad e inactividad en las citas.

## Vistas disponibles

- `/`: pagina principal.
- `/pacientes`: CRUD de pacientes.
- `/medicos`: CRUD de medicos.
- `/citas`: CRUD y cancelacion de citas.
