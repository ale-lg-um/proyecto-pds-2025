# Créditos del proyecto

Este documento describe la contribución de cada miembro del equipo al desarrollo del sistema, incluyendo evidencias en el repositorio (commits, PRs, etc.).

## Miembros del equipo

- Daniel Marín Sánchez -> [@skyscrabble](https://www.github.com/skyscrabble)
- Alejandro López Galián -> [@ale-lg-um](https://www.github.com/ale-lg-um)
- David Sergio Anishchenko Halkina -> [@davidanish](https://www.github.com/davidanish)

---

## Daniel Marín Sánchez – Backend y modelo de dominio (DDD)

### Responsabilidades principales
- Diseño e implementación del modelo de dominio siguiendo DDD:
  - Entidades, Value Objects, Agregados
  - Servicios de dominio
  - Implementación en backend de todas las funcionalidades extra
- Diseño e implementación de la arquitectura hexagonal:
  - Puertos (interfaces)
  - Adaptadores (persistencia, eventos)
- Servicios de aplicación (casos de uso)
- Persistencia con JPA
- Sistema de eventos de dominio
- Tests del dominio y tests de integración de los servicios
- Documentación de la aplicación

### Evidencias

- Commits relevantes:
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/448c3b893d79ed946912851f82accbe911b63d99>` - Estructura de paquetes y clases de puertos de entrada y salida. Paquete application.common para paginación en el historial y estructura de la plantilla del tablero
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/68358642b39b145c5c0e372c6598f68814a32dec>` - Paquete eventos y todos los records de los eventos creados en cada agregado
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/d1afc1d87fb69e91d455945b40c7daa74e9ec544>` - Métodos factoría de EntryHistorial completados para rellenar eventos de dominio, pequeña actualización en el DDD relativa a eventos de dominio
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/b2f55d8fa79bea1fff7d8218051bfc95cc38c1a8>` - Entities JPA modificadas, repositorios JPA comentados e implementación de los adapters JPA de los repositorios
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/19667ef14facd552d7176cd4fe5b8b4082aed93e>` - ProyectoTarjetasApplicationTests mockeado también con el correo y añadidas propiedades del test
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/cc9bfbde04c32446c332e544cc5b2a315f31f412>` - Incluido README y retoques del documento DDD, actualizado el pom.xml del frontend

---

## Alejandro López Galián – Frontend (JavaFX)

### Responsabilidades principales
- Diseño e implementación de la interfaz gráfica con JavaFX
- Pantallas principales de la aplicación
- Navegación y experiencia de usuario
- Integración con backend (llamadas a servicios)
- Implementación parcial de repositorios en memoria para pruebas
- Tests del dominio y tests de integración de los servicios

### Evidencias

- Commits relevantes (hechos con dos cuentas por trabajar en dos ordenadores diferentes. Cuentas: ale-lg-um y SpiderYoda11):
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/0c4684db31b7b5112764bf662f09b4fdf82e896a>` – Limite y prerrequisitos de las listas hechos en interfaz
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/a3de4810a9e2f7d7c44e6f072609df22435f98b7>` – Bloqueo de tableros completado en la interfaz
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/016f59c7153a4822aca31738cc491c7eaa26bd14>` – Corregir fallos checklist, modificar tarea, errores pequeños
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/3856d24212472fdfd08a0f48e1bb1278916b67ef>` – Implementación de varios tests


### Comentarios
xxxxxxxxxxxxxxxxxxxxxxx

---

## David Sergio Anishchenko Halkina – API REST e integración

### Responsabilidades principales
- Diseño, implementación y documentación de la API REST (Spring Boot)
- Controladores REST
- Serialización/deserialización (DTOs)
- Integración entre frontend y backend de la API
- Gestión de endpoints y comunicación HTTP
- Implementación del servicio de envío de correos electrónicos.

### Evidencias

- Commits relevantes:
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/051cc3be4cc8aa87ad5fdbfebcdcb670a860ced2>` - Creadas e implementadas las clases de la API REST.
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/1794b13f7eff56a0fe04489cb7d59d501ced3ea0>` - Integrada la API REST en el frontend de la aplicación e implementada la clase PlantillaEndpoint, así como el servicio de envío de correos electrónicos.
  - `<https://github.com/ale-lg-um/proyecto-pds-2025/commit/bfc540ab73e7dfb4cfe90fd09ea34cbe044dd3fc>` – Imprimir código de autenticación por consola e implementación parcial ListaEndpointTest.
