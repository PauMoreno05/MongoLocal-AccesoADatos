package org.example

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import org.bson.Document

fun  duracionMas2Pelis(){
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println("Peliclas que duran mas de 2 horas")
    col.find(Filters.gt("duracionHorasJSON", 2)).forEach { println(it.toJson()) }
}

fun  duracionMenos2Pelis(){
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println("Peliclas que duran menos de 2 horas")
    col.find(Filters.lt("duracionHorasJSON", 2)).forEach { println(it.toJson()) }
    client.close()
}


fun tituloPelis(){
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println("Titulo de todas las Peliculas")
    col.find().projection(Projections.include("tituloPeliJSON")).forEach { println(it.toJson()) }
    client.close()
}

fun duracionMedia() {
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println("Duracion Media de todas ")
    val pipeline = listOf(
        Document("\$group", Document("_id", null).append("alturaMedia", Document("\$avg", "\$duracionHorasJSON")))
    )
    val aggCursor = col.aggregate(pipeline).iterator()
    aggCursor.use {
        while (it.hasNext()) println(it.next().toJson())
    }

    client.close()
}

fun tresPelisMasLargas() {
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println("Top 3 Películas más largas")

    val pipeline = listOf(
        Document("\$sort", Document("duracionHorasJSON", -1)),
        Document("\$limit", 3)
    )

    val aggCursor = col.aggregate(pipeline).iterator()
    aggCursor.use {
        while (it.hasNext()) println(it.next().toJson())
    }

    client.close()
}