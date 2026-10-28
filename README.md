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

## Ejecutar

```bash
mvn spring-boot:run
```
