# Filmoteca Kotlin - Gestión Integral con MongoDB

Proyecto de gestión de bases de datos NoSQL utilizando **Kotlin** y **MongoDB** (en memoria). La aplicación permite administrar una filmoteca completa con películas, videoclubs y clientes, realizando desde operaciones básicas (CRUD) hasta consultas complejas de agregación y relaciones entre colecciones.

---

## Descripción General

Esta aplicación de consola simula un sistema de gestión para una cadena de videoclubs. Los datos son persistentes a través de archivos JSON que se importan al iniciar y se exportan al cerrar la aplicación. El núcleo del proyecto demuestra el uso del driver de MongoDB para Java/Kotlin, implementando tuberías de agregación (`pipelines`) para cruzar datos entre colecciones sin usar SQL.

---

## Estructura del Proyecto

El código fuente se encuentra en el paquete `org.example` y se divide en los siguientes módulos:

* **`Main.kt`**:
    * Controlador principal.
    * Gestiona la conexión a la base de datos (MongoServer en memoria).
    * Contiene la lógica de navegación de los menús.
    * Orquesta la importación y exportación de datos.

* **`ConsultasSencillas.kt`** (Gestión CRUD):
    * Contiene la lógica para **Crear, Leer, Actualizar y Borrar** documentos.
    * Funciones para: `Peliculas`, `Videoclubs` y `Clientes`.
    * Ejemplos: `insertarPelicula`, `actualizarEmpleadosVideoclub`, `eliminarCliente`.

* **`OperacionesComplejas.kt`** (Consultas Analíticas):
    * Uso de filtros avanzados (`Filters.gt`, `Filters.lt`).
    * Proyecciones para optimizar resultados (`Projections`).
    * Agregaciones básicas (`$avg`, `$sort`, `$limit`).
    * Ejemplos: Calcular duración media, Top 3 películas más largas.

* **`Pipeline.kt`** (Relaciones y Joins):
    * Consultas avanzadas que cruzan múltiples colecciones (`$lookup`).
    * Desglose de arrays (`$unwind`).
    * Ejemplos: Listar clientes con su videoclub favorito y película más vista.

* **`ImportarExportar.kt`**:
    * Manejo de entrada/salida de archivos.
    * Transformación de JSON a `Document` JSON y viceversa.

---

## Funcionalidades y Menús

El sistema cuenta con un menú principal dividido en tres grandes bloques:

### 1. Gestión de Colecciones (CRUD)
Submenús específicos para **Películas**, **Videoclubs** y **Clientes**.
* **Listar:** Muestra todos los datos formateados en consola.
* **Insertar:** Formulario paso a paso para añadir nuevos registros.
* **Actualizar:**
    * Películas: Modificar duración.
    * Videoclubs: Modificar número de empleados.
    * Clientes: Modificar videoclub favorito.
* **Eliminar:** Borrado por ID.

### 2. Consultas Avanzadas
Operaciones sobre la colección de Películas:
1.  **Películas > 2 horas:** Filtro de duración mayor.
2.  **Películas < 2 horas:** Filtro de duración menor.
3.  **Listado solo de Títulos:** Proyección de un solo campo.
4.  **Duración Media:** Cálculo estadístico con `$group` y `$avg`.
5.  **Top 3 más largas:** Ordenamiento descendente y límite de resultados.

### 3. Consultas Multitabla (Relaciones)
Operaciones que relacionan las tres colecciones:
1.  **Videoclub por Película:** Busca qué videoclubs tienen una película específica como la "más vendida".
2.  **Listado Completo de Clientes:** Muestra el cliente cruzando datos para obtener el *Nombre de la Película* y el *Nombre del Videoclub* (en lugar de solo sus IDs).
3.  **Buscador de Fans:** Dado un título, encuentra qué dueños de videoclub y qué clientes la prefieren.

---

## Modelo de Datos (JSON)

Los datos se almacenan en `src/main/resources/`.

### `peliculas_export.json`
```json
{
  "idPeliculaJSON": 1,
  "tituloPeliJSON": "El Origen",
  "directorJSON": "Christopher Nolan",
  "duracionHorasJSON": 2.48,
  "esRecomendadaJSON": true
}