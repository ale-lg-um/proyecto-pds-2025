# Tareax
Proyecto final para la asignatura de Programación de Desarrollo de Software en la Universidad de Murcia para el curso 2025/26. Este proyecto presenta un sistema de gestión de trabajo colaborativo a través de tableros de tareas, usando como referencia modelos de aplicación como Trello (https://www.trello.com/).

## Autores

- Alejandro López Galián -> [@ale-lg-um](https://www.github.com/ale-lg-um)
- Daniel Marín Sánchez -> [@skyscrabble](https://www.github.com/skyscrabble)
- David Sergio Anishchenko Halkina -> [@davidanish](https://www.github.com/davidanish)

## Características de Tareax
La versión actual de la aplicación contiene las siguientes funcionalidades:
- Crear y modificar tableros donde hay listas de tareas. Una lista de tareas tiene tarjetas de manera que las tarjetas sirven para asignar tareas o para anotar información relevante.
- Mover las tarjetas entre listas y marcar las tarjetas como completadas, en cuyo caso pueden pasar a una lista especial del tablero de tarjetas completadas.
- Etiquetar las tarjetas para clasificarlas y personalizarlas al gusto, permitiendo añadir una etiqueta con nombre y color.
- Establecer dos tipos de tarjetas: tarjetas con tareas (simplemente con una descripción textual) y tarjetas con checklists (compuestas por diferentes ítems que se pueden marcar y desmarcar).
- Guardar un historial con todas las acciones de los usuarios. Por ejemplo, si se mueve una tarjeta entre listas de tareas se registra una traza de que este movimiento se ha realizado.
- Bloquear temporalmente un tablero para que no se puedan añadir nuevas tarjetas, solo mover tarjetas entre sus listas (por ejemplo, un tablero con TODO, DOING, DONE en el que durante una semana no se pueden añadir nuevas tarjetas).
- Implementar reglas a nivel de tablero o de lista de tareas:
  - Una lista no puede tener más de N ítems (configurable).
  - Una lista define que una tarjeta tiene que haber pasado por otras listas antes de llegar a ella.
- Filtrar tarjetas del tablero por el nombre de la etiqueta.
- Creación de plantillas para crear tableros con listas y tarjetas predeterminadas. Las plantillas se definen como un fichero YAML. _(no implementado aún en frontend)_.
- El acceso compartido simultáneo de un tablero por varios usuarios se realiza mediante URL que se genera al crear el tablero. Todos los usuarios con acceso a esa URL pueden trabajar sobre el tablero.
- La autenticación a la aplicación se hace mediante el correo electrónico del usuario, el cual recibe un código en su bandeja y debe validarlo.

## Construcción y ejecución

### Requisitos previos

- Java 21
- Maven 3.9.x o superior
- Git

### Descargar el proyecto

```bash
git clone <https://github.com/ale-lg-um/proyecto-pds-2025>
cd proyecto-pds-2025/workspace-proyecto
```

El frontend depende del módulo backend proyecto-tarjetas, por lo que primero hay que instalarlo en el repositorio local de Maven:

```bash
cd proyecto-tarjetas
mvn clean install
```

- En Linux/macOS también se puede usar el wrapper:

```bash
./mvnw clean install
```

- En Windows:

```bash
mvnw.cmd clean install
```

Ahora, para ejecutar la interfaz gráfica:

```bash
cd ../proyecto-tarjetas-ui
mvn javafx:run
```

En caso de no funcionar, importar como Maven ambos proyectos y ejecutar como Java Application la clase "App" de proyecto-tarjetas-ui

La aplicación usa una base de datos H2 en fichero. Se creará automáticamente en el directorio desde el que se ejecute la aplicación, con nombre "__datos_tarjetas.mv.db__"

## Aspectos a tener en cuenta
- Para mover una tarjeta de una lista a otra se hará arrastrando la tarjeta con el cursor desde una lista hacia la otra.
- Si se completa una tarjeta se moverá directamente a la lista definida como "especial". Debe haber una lista especial para completar tarjetas.
- No se permite crear listas con nombres duplicados.

## Créditos
En el fichero [CREDITOS.md](CREDITOS.md) se presenta información sobre en qué ha participado cada componente del grupo para reflejar la participación de todos los miembros del equipo.

## Anexo

Para información más detallada, características concretas de la arquitectura, documentación de los puertos y servicios y otras reglas de negocio, revisar la carpeta [docs](docs) y el [Domain Driven Design](docs/Domain%20Driven%20Design.pdf) dentro del repositorio.