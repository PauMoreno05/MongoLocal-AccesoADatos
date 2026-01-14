package org.example

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import org.bson.Document
import java.util.Scanner



val scanner = Scanner(System.`in`)

// -------------- PELÍCULAS ------------------

fun mostrarPeliclas() {
    val coleccion = coleccionPeliculas

    println()
    println("**** Listado de Películas:")


    coleccion.find().forEach { doc ->
        val id = doc.get("idPeliculaJSON")
        val titulo = doc.getString("tituloPeliJSON") ?: "Sin título"
        val director = doc.getString("directorJSON") ?: "Desconocido"
        val duracion = doc.get("duracionHorasJSON")
        val recomendada = if (doc.getBoolean("esRecomendadaJSON") == true) "Sí" else "No"

        println("[$id] $titulo ($director): $duracion h - Recomendada: $recomendada")
    }

}
fun insertarPelicula() {
    //conectar con la BD
    val coleccion = coleccionPeliculas

    var id_pelicula: Int? = null
    while (id_pelicula == null) {
        print("ID de la Pelicula: ")
        val entrada = scanner.nextLine()
        id_pelicula = entrada.toIntOrNull()
        if (id_pelicula == null) {
            println("El ID debe ser un número !!!")
        }
    }

    print("Titulo de la pelicula: ")
    val titulo_peli = scanner.nextLine()
    print("Director de la pelicula: ")
    val director_peli = scanner.nextLine()
    var duracion_peli: Double? = null
    while (duracion_peli == null) {
        print("Duracion (en h): ")
        val entrada = scanner.nextLine()
        duracion_peli = entrada.toDoubleOrNull()
        if (duracion_peli == null) {
            println("La duracion debe ser un número")
        }
    }

    // --- NUEVO BLOQUE PARA GÉNEROS ---
    print("Introduce los géneros separados por comas (ej: Acción, Terror, Drama): ")
    val entradaGeneros = scanner.nextLine()

    // 1. Separamos por la coma (split)
    // 2. Quitamos los espacios en blanco de los lados (trim)
    val listaGeneros = if (entradaGeneros.isNotBlank()) {
        entradaGeneros.split(",").map { it.trim() }
    } else {
        emptyList<String>() // Si no escribe nada, guardamos lista vacía
    }
    // ---------------------------------


    var recomendada_peli: Boolean = false
    print("Recomiedas la Pelicula(introduce -SI- en para cambiar, por defecto -false-:  ")
    val recomendaRes = scanner.nextLine()
    if (recomendaRes.uppercase() == "SI") {
        recomendada_peli = true
    } else {
        println("Dejando en no recomendada")
    }

    val doc = Document("idPeliculaJSON", id_pelicula)
        .append("tituloPeliJSON", titulo_peli)
        .append("directorJSON", director_peli)
        .append("duracionHorasJSON", duracion_peli)
        .append("generosJSON", listaGeneros)
        .append("esRecomendadaJSON", recomendada_peli)


    coleccion.insertOne(doc)
    println("Pelicula insertada con ID: ${doc.getObjectId("_id")}")

}


fun actualizarDuracion() {
    //conectar con la BD
    val coleccion = coleccionPeliculas

    var id_pelicula: Int? = null
    while (id_pelicula == null) {
        print("ID de la Pelicula: ")
        val entrada = scanner.nextLine()
        id_pelicula = entrada.toIntOrNull()
        if (id_pelicula == null) {
            println("El ID debe ser un número !!!")
        }
    }

    //comprobar si existe una planta con el id_planta proporcionado por consola
    val pelicula = coleccion.find(Filters.eq("idPeliculaJSON", id_pelicula)).firstOrNull()
    if (pelicula == null) {
        println("No se encontró ninguna pelicula con id_planta = \"$id_pelicula\".")
    }
    else {
        // Mostrar información de la planta encontrada
        println("Planta encontrada: ${pelicula.getString("tituloPeliJSON")} (duracion: ${pelicula.get("duracionHorasJSON")}.h)")

        //pedir nueva altura
        var hora: Double? = null
        while (hora == null) {
            print("Nueva duracion (ej. 2.30): ")
            val entrada = scanner.nextLine()
            hora = entrada.toDoubleOrNull()
            if (hora == null) {
                println("¡¡¡ La hora debe ser un número !!!")
            }
        }

        // Actualizar el documento
        val result = coleccion.updateOne(
            Filters.eq("idPeliculaJSON", id_pelicula),
            Document("\$set", Document("duracionHorasJSON", hora))
        )

        if (result.modifiedCount > 0)
            println("Duracion actualizada correctamente (${result.modifiedCount} documento modificado).")
        else
            println("No se modificó ningún documento (la duracion quizá ya era la misma).")
    }

}


fun eliminarPelicula() {
    //conectar con la BD
    val coleccion = coleccionPeliculas

    var id_pelicula: Int? = null
    while (id_pelicula == null) {
        print("ID de la Pelicula: ")
        val entrada = scanner.nextLine()
        id_pelicula = entrada.toIntOrNull()
        if (id_pelicula == null) {
            println("El ID debe ser un número !!!")
        }
    }


    val result = coleccion.deleteOne(Filters.eq("idPeliculaJSON", id_pelicula))
    if (result.deletedCount > 0)
        println("Pelicula eliminada correctamente.")
    else {
        println("No se encontró ninguna pelicula con ese nombre.")
    }

}

// -------------- VIDEOCLUBS ------------------

fun mostrarVideoclubs() {
    val coleccion = coleccionVideoclubs

    println()
    println("**** Listado de Videoclubs:")

    coleccion.find().forEach { doc ->
        val id = doc.get("ID")
        val nombre = doc.getString("VideoClub") ?: "Sin nombre"
        val empleados = doc.getInteger("NumeroEmpleados")
        val peliMasVendida = doc.getInteger("idPeliculaMasVendida")
        val peliFavDueno = doc.getInteger("PeliFavoritaDueñoVideoclub")

        println("[$id] $nombre - Empleados: $empleados (Peli Top: $peliMasVendida | Fav Dueño: $peliFavDueno)")
    }
}

fun insertarVideoclub() {
    val coleccion = coleccionVideoclubs

    var id: Int? = null
    while (id == null) {
        print("ID del Videoclub: ")
        val entrada = scanner.nextLine()
        id = entrada.toIntOrNull()
        if (id == null) println("El ID debe ser un número.")
    }

    print("Nombre del Videoclub: ")
    val nombre = scanner.nextLine()

    var empleados: Int? = null
    while (empleados == null) {
        print("Número de empleados: ")
        val entrada = scanner.nextLine()
        empleados = entrada.toIntOrNull()
        if (empleados == null) println("Introduce un número válido.")
    }

    // Pedimos las referencias a películas (simuladas)
    print("ID de la película más vendida: ")
    val peliTop = scanner.nextLine().toIntOrNull() ?: 0

    print("ID de la película favorita del dueño: ")
    val peliFav = scanner.nextLine().toIntOrNull() ?: 0

    val doc = Document("ID", id)
        .append("VideoClub", nombre)
        .append("NumeroEmpleados", empleados)
        .append("idPeliculaMasVendida", peliTop)
        .append("PeliFavoritaDueñoVideoclub", peliFav)

    coleccion.insertOne(doc)
    println("Videoclub insertado correctamente.")
}

fun insertarVariosVideoclubs() {
    val coleccion = coleccionVideoclubs

    // 1. Creamos una lista mutable para ir guardando los documentos
    val listaDocumentos = mutableListOf<Document>()

    var continuar = true
    var contador = 1

    println("--- INSERCIÓN MASIVA DE VIDEOCLUBS ---")

    while (continuar) {
        println("\nDatos del Videoclub #$contador:")

        // --- BLOQUE DE PEDIR DATOS (Igual que antes) ---
        var id: Int? = null
        while (id == null) {
            print("ID del Videoclub: ")
            val entrada = scanner.nextLine()
            id = entrada.toIntOrNull()
            if (id == null) println("El ID debe ser un número.")
        }

        print("Nombre del Videoclub: ")
        val nombre = scanner.nextLine()

        var empleados: Int? = null
        while (empleados == null) {
            print("Número de empleados: ")
            val entrada = scanner.nextLine()
            empleados = entrada.toIntOrNull()
            if (empleados == null) println("Introduce un número válido.")
        }

        print("ID de la película más vendida: ")
        val peliTop = scanner.nextLine().toIntOrNull() ?: 0

        print("ID de la película favorita del dueño: ")
        val peliFav = scanner.nextLine().toIntOrNull() ?: 0
        // -----------------------------------------------

        // 2. Creamos el documento
        val doc = Document("ID", id)
            .append("VideoClub", nombre)
            .append("NumeroEmpleados", empleados)
            .append("idPeliculaMasVendida", peliTop)
            .append("PeliFavoritaDueñoVideoclub", peliFav)

        // 3. En lugar de insertar, lo AÑADIMOS a la lista
        listaDocumentos.add(doc)
        contador++

        // 4. Preguntamos si quiere seguir
        print("¿Quieres añadir otro videoclub? (s/n): ")
        val respuesta = scanner.nextLine()
        if (respuesta.lowercase() != "s") {
            continuar = false
        }
    }

    // 5. Ejecutamos el insertMany con la lista completa
    if (listaDocumentos.isNotEmpty()) {
        try {
            coleccion.insertMany(listaDocumentos)
            println("\n¡Éxito! Se han insertado ${listaDocumentos.size} videoclubs correctamente.")
        } catch (e: Exception) {
            println("Error al insertar los documentos: ${e.message}")
        }
    } else {
        println("No se ha insertado ningún videoclub.")
    }
}



fun actualizarEmpleadosVideoclub() {
    val coleccion = coleccionVideoclubs

    print("ID del Videoclub a actualizar: ")
    val id = scanner.nextLine().toIntOrNull()

    if (id == null) {
        println("ID no válido.")
        return
    }

    val videoclub = coleccion.find(Filters.eq("ID", id)).firstOrNull()

    if (videoclub == null) {
        println("No existe videoclub con ID $id.")
    } else {
        println("Videoclub: ${videoclub.getString("VideoClub")} (Empleados actuales: ${videoclub.get("NumeroEmpleados")})")

        print("Nuevo número de empleados: ")
        val nuevosEmpleados = scanner.nextLine().toIntOrNull()

        if (nuevosEmpleados != null) {
            coleccion.updateOne(
                Filters.eq("ID", id),
                Document("\$set", Document("NumeroEmpleados", nuevosEmpleados))
            )
            println("Videoclub actualizado.")
        } else {
            println("Dato incorrecto. Cancelando.")
        }
    }
}

fun demostracionDeFinds() {
    val coleccion = coleccionVideoclubs

    println("--- 1. EJEMPLO findOne (Buscar videoclub ID 1) ---")
    // Equivalente a findOne({ID: 1})
    val unVideoclub = coleccion.find(Filters.eq("ID", 1)).firstOrNull()
    println("Resultado: ${unVideoclub?.toJson()}")

    println("\n--- 2. EJEMPLO find() (Todos los videoclubs) ---")
    // Equivalente a find()
    coleccion.find().forEach { doc ->
        println(" - ${doc.getString("VideoClub")}")
    }

    println("\n--- 3. EJEMPLO find(criterio, proyección) ---")
    println("(Videoclubs con más de 3 empleados, mostrando solo el nombre)")

    // Equivalente a find({NumeroEmpleados: {$gt: 3}}, {VideoClub: 1, _id: 0})
    coleccion.find(Filters.gt("NumeroEmpleados", 3))
        .projection(Projections.fields(
            Projections.include("VideoClub"),
            Projections.excludeId()
        ))
        .forEach { doc ->
            // Como ocultamos el ID y resto de campos, aquí solo llegará el nombre
            println(" -> ${doc.toJson()}")
        }
}

fun demostracionDeUpdates() {
    val coleccion = coleccionVideoclubs
    println("--- ESTADO INICIAL ---")
    mostrarVideoclubs()

    // 1. updateOne: Cambiar nombre del videoclub 1
    println("\n> Ejecutando updateOne (Cambiar nombre ID 1)...")
    coleccion.updateOne(
        Filters.eq("ID", 1),
        Document("\$set", Document("VideoClub", "VideoMania UPDATE"))
    )

    // 2. updateMany: Poner a 0 la peli favorita del dueño en TODOS los videoclubs
    println("> Ejecutando updateMany (Resetear peli favorita a 0 en todos)...")
    coleccion.updateMany(
        Filters.exists("ID"), // Filtro que selecciona todo lo que tenga ID
        Document("\$set", Document("PeliFavoritaDueñoVideoclub", 0))
    )

    // 3. replaceOne: Reemplazar el videoclub 2 por uno nuevo
    println("> Ejecutando replaceOne (Sustituir ID 2 completo)...")
    val videoclubNuevo = Document("ID", 2)
        .append("VideoClub", "NUEVO CINEPLUS")
        .append("NumeroEmpleados", 50)
        .append("idPeliculaMasVendida", 999)
        .append("PeliFavoritaDueñoVideoclub", 0)

    coleccion.replaceOne(Filters.eq("ID", 2), videoclubNuevo)

    println("\n--- ESTADO FINAL ---")
    mostrarVideoclubs()
}

fun eliminarVideoclub() {
    val coleccion = coleccionVideoclubs

    print("ID del Videoclub a eliminar: ")
    val id = scanner.nextLine().toIntOrNull()

    if (id != null) {
        val result = coleccion.deleteOne(Filters.eq("ID", id))
        if (result.deletedCount > 0) println("Videoclub eliminado.")
        else println("No se encontró ese ID.")
    } else {
        println("ID inválido.")
    }
}

// -------------- CLIENTES ------------------

fun mostrarClientes() {
    val coleccion = coleccionClientes

    println()
    println("**** Listado de Clientes:")

    coleccion.find().forEach { doc ->
        val id = doc.get("ID")
        val nombre = doc.getString("Nombre") ?: "Sin nombre"
        val peliMasVista = doc.getInteger("idPeliculaMasVista")
        val clubFav = doc.getInteger("IdVideoclubFav")

        println("[$id] $nombre - Club Fav: $clubFav - Peli más vista: $peliMasVista")
    }
}

fun insertarCliente() {
    val coleccion = coleccionClientes

    var id: Int? = null
    while (id == null) {
        print("ID del Cliente: ")
        id = scanner.nextLine().toIntOrNull()
        if (id == null) println("El ID debe ser un número.")
    }

    print("Nombre del Cliente: ")
    val nombre = scanner.nextLine()

    print("ID de su película más vista: ")
    val peliVista = scanner.nextLine().toIntOrNull() ?: 0

    print("ID de su videoclub favorito: ")
    val clubFav = scanner.nextLine().toIntOrNull() ?: 0

    val doc = Document("ID", id)
        .append("Nombre", nombre)
        .append("idPeliculaMasVista", peliVista)
        .append("IdVideoclubFav", clubFav)

    coleccion.insertOne(doc)
    println("Cliente insertado correctamente.")
}

fun actualizarVideoclubFavCliente() {
    val coleccion = coleccionClientes

    print("ID del Cliente a actualizar: ")
    val id = scanner.nextLine().toIntOrNull()

    if (id == null) return

    val cliente = coleccion.find(Filters.eq("ID", id)).firstOrNull()

    if (cliente == null) {
        println("Cliente no encontrado.")
    } else {
        println("Cliente: ${cliente.getString("Nombre")} (Videoclub Fav actual ID: ${cliente.get("IdVideoclubFav")})")

        print("Nuevo ID de Videoclub Favorito: ")
        val nuevoClub = scanner.nextLine().toIntOrNull()

        if (nuevoClub != null) {
            coleccion.updateOne(
                Filters.eq("ID", id),
                Document("\$set", Document("IdVideoclubFav", nuevoClub))
            )
            println("Preferencia actualizada.")
        } else {
            println("ID inválido.")
        }
    }
}

fun eliminarCliente() {
    val coleccion = coleccionClientes

    print("ID del Cliente a eliminar: ")
    val id = scanner.nextLine().toIntOrNull()

    if (id != null) {
        val result = coleccion.deleteOne(Filters.eq("ID", id))
        if (result.deletedCount > 0) println("Cliente eliminado.")
        else println("No se encontró ese ID.")
    } else {
        println("Entrada inválida.")
    }
}


fun gestionDeIndices() {
    val col = coleccionPeliculas

    println("--- 1. CREAR ÍNDICE (createIndex) ---")
    // Equivalente a: db.peliculas.createIndex({tituloPeliJSON: 1})
    // El '1' significa orden Ascendente (A-Z)
    val nombreIndice = col.createIndex(Document("tituloPeliJSON", 1))
    println("Índice creado exitosamente: $nombreIndice")


    println("\n--- 2. LISTAR ÍNDICES (getIndexes) ---")
    // Equivalente a: db.peliculas.getIndexes()
    println("Índices actuales en la colección:")
    col.listIndexes().forEach { indexDoc ->
        // Imprimimos el documento JSON que describe el índice
        println(" - ${indexDoc.toJson()}")
    }


    println("\n--- 3. ELIMINAR ÍNDICE (dropIndex) ---")
    // Equivalente a: db.peliculas.dropIndex("tituloPeliJSON_1")
    // Nota: MongoDB suele llamar al índice "campo_1".

    // Verificamos si existe antes de borrar para evitar errores
    try {
        col.dropIndex("tituloPeliJSON_1")
        println("Índice 'tituloPeliJSON_1' eliminado.")
    } catch (e: Exception) {
        println("No se pudo borrar el índice (quizá no existía).")
    }

    // Comprobación final
    println("\n(Comprobación) Índices restantes:")
    col.listIndexes().forEach { println(" - ${it["name"]}") }
}