package org.example

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
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
    var duracion: Double? = null
    while (duracion == null) {
        print("Duracion (en h): ")
        val entrada = scanner.nextLine()
        duracion = entrada.toDoubleOrNull()
        if (duracion == null) {
            println("¡¡¡ La altura debe ser un número !!!")
        }
    }
    while (duracion == null) {
        print("Recomiedas la Pelicula(SI/NO):  ")
        val recomenada: Boolean
        val recomendaRes = scanner.nextLine()
        if (recomendaRes == "SI"){
            recomenada = true
        } else (recomendaRes == "NO"){
            recomenada = false
        }

    val doc = Document("idPeliculaJSON", id_pelicula)
        .append("idPeliculaJSON", nombre_comun)
        .append("nombre_cientifico", nombre_cientifico)
        .append("altura", altura)

    coleccion.insertOne(doc)
    println("Planta insertada con ID: ${doc.getObjectId("_id")}")

    cliente.close()
    println("Conexión cerrada")
}


fun actualizarAltura() {
    //conectar con la BD
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

    var id_planta: Int? = null
    while (id_planta == null) {
        print("ID de la planta a actualizar: ")
        val entrada = scanner.nextLine()
        id_planta = entrada.toIntOrNull()
        if (id_planta == null) {
            println("El ID debe ser un número !!!")
        }
    }

    //comprobar si existe una planta con el id_planta proporcionado por consola
    val planta = coleccion.find(Filters.eq("id_planta", id_planta)).firstOrNull()
    if (planta == null) {
        println("No se encontró ninguna planta con id_planta = \"$id_planta\".")
    }
    else {
        // Mostrar información de la planta encontrada
        println("Planta encontrada: ${planta.getString("nombre_comun")} (altura: ${planta.get("altura")} cm)")

        //pedir nueva altura
        var altura: Int? = null
        while (altura == null) {
            print("Nueva altura (en cm): ")
            val entrada = scanner.nextLine()
            altura = entrada.toIntOrNull()
            if (altura == null) {
                println("¡¡¡ La altura debe ser un número !!!")
            }
        }

        // Actualizar el documento
        val result = coleccion.updateOne(
            Filters.eq("id_planta", id_planta),
            Document("\$set", Document("altura", altura))
        )

        if (result.modifiedCount > 0)
            println("Altura actualizada correctamente (${result.modifiedCount} documento modificado).")
        else
            println("No se modificó ningún documento (la altura quizá ya era la misma).")
    }

    cliente.close()
    println("Conexión cerrada.")
}


fun eliminarPlanta() {
    //conectar con la BD
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

    var id_planta: Int? = null
    while (id_planta == null) {
        print("ID de la planta a eliminar: ")
        val entrada = scanner.nextLine()
        id_planta = entrada.toIntOrNull()
        if (id_planta == null) {
            println("El ID debe ser un número !!!")
        }
    }

    val result = coleccion.deleteOne(Filters.eq("id_planta", id_planta))
    if (result.deletedCount > 0)
        println("Planta eliminada correctamente.")
    else
        println("No se encontró ninguna planta con ese nombre.")

    cliente.close()
    println("Conexión cerrada.")
}

fun main() {
    println("========================")
    println("   Listado Peliculas    ")
    println("========================")
    println("  ID | Titulo | Director | Generos | Duracion Horas")
    mostrarPeliclas()


}