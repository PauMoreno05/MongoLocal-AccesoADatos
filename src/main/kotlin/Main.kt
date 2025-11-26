package org.example

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.Document
import java.util.Scanner



fun main() {
    println("========================")
    println("   Listado Peliculas    ")
    println("========================")
    println("  ID | Titulo | Director | Generos | Duracion Horas")
    eliminarPelicula()
    mostrarPeliclas()


}