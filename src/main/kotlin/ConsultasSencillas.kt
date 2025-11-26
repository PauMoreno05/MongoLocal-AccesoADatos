package org.example

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import org.bson.Document
import java.util.Scanner

const val NOM_SRV = "mongodb://localhost:27017"
const val NOM_BD = "filmoteca"
const val NOM_COLECCION = "peliculas"

val scanner = Scanner(System.`in`)

fun mostrarPeliclas() {
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

    val cursor = coleccion.find().iterator()
    cursor.use {
        while (it.hasNext()) {
            val doc = it.next()
            println(doc.toJson())
        }
    }

    cliente.close()
}
fun insertarPelicula() {
    //conectar con la BD
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

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
            println("¡¡¡ La altura debe ser un número !!!")
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

    cliente.close()
    println("Conexión cerrada")
}


fun actualizarDuracion() {
    //conectar con la BD
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

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

    cliente.close()
    println("Conexión cerrada.")
}


fun eliminarPelicula() {
    //conectar con la BD
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

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
    else
        println("No se encontró ninguna pelicula con ese nombre.")

    cliente.close()
    println("Conexión cerrada.")
}