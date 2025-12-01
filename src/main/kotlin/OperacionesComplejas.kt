package org.example

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import org.bson.Document

fun duracionMas2Pelis() {
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println(" Películas que duran MÁS de 2 horas")

    col.find(Filters.gt("duracionHorasJSON", 2)).forEach { doc ->
        val id = doc.get("idPeliculaJSON")
        val titulo = doc.getString("tituloPeliJSON") ?: "Sin título"
        val director = doc.getString("directorJSON") ?: "Desconocido"
        val duracion = doc.get("duracionHorasJSON")
        val recomendada = if (doc.getBoolean("esRecomendadaJSON") == true) "Sí" else "No"

        println("[$id] $titulo ($director): $duracion h - Recomendada: $recomendada")

    }
    client.close()
}

fun duracionMenos2Pelis() {
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println(" Películas que duran MENOS de 2 horas ")

    col.find(Filters.lt("duracionHorasJSON", 2)).forEach { doc ->
        val id = doc.get("idPeliculaJSON")
        val titulo = doc.getString("tituloPeliJSON") ?: "Sin título"
        val director = doc.getString("directorJSON") ?: "Desconocido"
        val duracion = doc.get("duracionHorasJSON")
        val recomendada = if (doc.getBoolean("esRecomendadaJSON") == true) "Sí" else "No"

        println("[$id] $titulo ($director): $duracion h - Recomendada: $recomendada")
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

    col.aggregate(pipeline).forEach { doc ->
        val id = doc.get("idPeliculaJSON")
        val titulo = doc.getString("tituloPeliJSON") ?: "Sin título"
        val director = doc.getString("directorJSON") ?: "Desconocido"
        val duracion = doc.get("duracionHorasJSON")
        val recomendada = if (doc.getBoolean("esRecomendadaJSON") == true) "Sí" else "No"

        println("[$id] $titulo ($director): $duracion h - Recomendada: $recomendada")

    }

    client.close()
}

fun tituloPelis() {
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println("Listado solo de Títulos")

    col.find().projection(Projections.include("tituloPeliJSON")).forEach { doc ->

        val id = doc.get("idPeliculaJSON")
        val titulo = doc.getString("tituloPeliJSON") ?: "Sin título"
        val director = doc.getString("directorJSON") ?: "Desconocido"
        val duracion = doc.get("duracionHorasJSON")
        val recomendada = if (doc.getBoolean("esRecomendadaJSON") == true) "Sí" else "No"

        println("[$id] $titulo ($director): $duracion h - Recomendada: $recomendada")

    }
    client.close()
}

fun duracionMedia() {
    val client = MongoClients.create(NOM_SRV)
    val col = client.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println("Estadística: Duración Media")

    val pipeline = listOf(
        Document("\$group", Document("_id", null).append("mediaCalculada", Document("\$avg", "\$duracionHorasJSON")))
    )

    val resultado = col.aggregate(pipeline).firstOrNull()

    if (resultado != null) {
        val media = resultado.getDouble("mediaCalculada")

        println("La Duración Media es: %.2f horas ****".format(media))
    } else {
        println("No hay datos para calcular la media.")
    }

    client.close()
}