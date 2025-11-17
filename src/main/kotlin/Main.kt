package org.example

import java.util.Scanner
import com.mongodb.client.MongoClients

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

fun main() {
    println("========================")
    println("   Listado Peliculas    ")
    println("========================")
    println("  ID | Titulo | Director | Generos | Duracion Horas")
    mostrarPeliclas()

}