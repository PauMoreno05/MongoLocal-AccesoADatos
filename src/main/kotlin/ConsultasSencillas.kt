package org.example

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
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