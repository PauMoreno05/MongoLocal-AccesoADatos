package org.example

import com.mongodb.client.MongoClients
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Sorts
import org.bson.Document

fun duracionMas2Pelis() {

    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println(" Películas que duran MÁS de 2 horas")

    col.find(Filters.gt("duracionHorasJSON", 2)).forEach { doc ->
        val id = doc.get("idPeliculaJSON")
        val titulo = doc.getString("tituloPeliJSON") ?: "Sin título"
        val director = doc.getString("directorJSON") ?: "Desconocido"
        val duracion = doc.get("duracionHorasJSON")
        val recomendada = if (doc.getBoolean("esRecomendadaJSON") == true) "Sí" else "No"

        println("[$id] $titulo ($director): $duracion h - Recomendada: $recomendada")

    }
}

fun duracionMenos2Pelis() {

    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println(" Películas que duran MENOS de 2 horas ")

    col.find(Filters.lt("duracionHorasJSON", 2)).forEach { doc ->
        val id = doc.get("idPeliculaJSON")
        val titulo = doc.getString("tituloPeliJSON") ?: "Sin título"
        val director = doc.getString("directorJSON") ?: "Desconocido"
        val duracion = doc.get("duracionHorasJSON")
        val recomendada = if (doc.getBoolean("esRecomendadaJSON") == true) "Sí" else "No"

        println("[$id] $titulo ($director): $duracion h - Recomendada: $recomendada")
    }
}

fun tresPelisMasLargas() {

    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

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
}

fun tituloPelis() {
    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION)
    println("Listado solo de Títulos")
    col.find().projection(Projections.include("tituloPeliJSON")).forEach { doc ->
        val titulo = doc.getString("tituloPeliJSON") ?: "Sin título"
        println("- $titulo")
    }
}

fun estadisticasDeCantidad() {
    // 1. Contar TOTAL de películas (sin filtro)
    // Equivalente a: db.peliculas.countDocuments()
    val totalPelis = coleccionPeliculas.countDocuments()
    println("Hay un total de $totalPelis películas en la base de datos.")

    // 2. Contar con FILTRO (Películas Recomendadas)
    // Equivalente a: db.peliculas.countDocuments({esRecomendadaJSON: true})
    val totalRecomendadas = coleccionPeliculas.countDocuments(
        Filters.eq("esRecomendadaJSON", true)
    )
    println("-> $totalRecomendadas son recomendadas.")

    // 3. Contar Videoclubs con muchos empleados (Mayor o igual a 5)
    // Equivalente a: db.videoclubs.countDocuments({NumeroEmpleados: {$gte: 5}})
    val videoclubsGrandes = coleccionVideoclubs.countDocuments(
        Filters.gte("NumeroEmpleados", 5)
    )
    println("-> Hay $videoclubsGrandes videoclubs con 5 o más empleados.")
}



fun duracionMedia() {

    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    println("Estadística: Duración Media")

    val pipeline = listOf(
        Document("\$group", Document("_id", null).append("mediaCalculada", Document("\$avg", "\$duracionHorasJSON")))
    )

    val resultado = col.aggregate(pipeline).firstOrNull()

    if (resultado != null) {
        val media = resultado.getDouble("mediaCalculada")

        println("La Duración Media es: %.2f horas".format(media))
    } else {
        println("No hay datos para calcular la media.")
    }

}

fun demoTecnicaCompleta() {
    println("\n=============================================")
    println("   DEMOSTRACIÓN TÉCNICA: FUNCIONES AVANZADAS")
    println("   (Stats, Índices, Pagination, Replace)")
    println("=============================================\n")

    val db = cliente.getDatabase(NOM_BD)

    // ---------------------------------------------------------
    // 1. ESTADÍSTICAS DE LA BD (db.stats())
    // ---------------------------------------------------------
    println("1. [STATS] Estado de la Base de Datos:")
    // Ejecutamos el comando "dbStats" directamente contra la BD
    val stats = db.runCommand(Document("dbStats", 1))

    // Extraemos datos interesantes del JSON resultante
    val numColecciones = stats.getInteger("collections")
    val numObjetos = stats.getInteger("objects")
    val mediaTamano = stats.getDouble("avgObjSize")

    println("   - Base de Datos: $NOM_BD")
    println("   - Colecciones activas: $numColecciones")
    println("   - Total de Documentos: $numObjetos")
    println("   - Tamaño medio por doc: %.2f bytes".format(mediaTamano))


    // ---------------------------------------------------------
    // 2. GESTIÓN DE ÍNDICES (createIndex / listIndexes)
    // ---------------------------------------------------------
    println("\n2. [ÍNDICES] Optimizando búsquedas:")

    // Creamos un índice real sobre el campo "Nombre" de Clientes para buscar rápido
    // 1 = Ascendente
    val nombreIndice = coleccionClientes.createIndex(Document("Nombre", 1))
    println("   - Índice creado exitosamente: '$nombreIndice'")

    println("   - Listado de índices actuales en 'Clientes':")
    coleccionClientes.listIndexes().forEach { index ->
        val nombre = index.getString("name")
        val keys = index["key"] as Document
        println("     * Nombre: $nombre | Claves: ${keys.toJson()}")
    }


    // ---------------------------------------------------------
    // 3. PAGINACIÓN CON SKIP (Aggregates $skip)
    // ---------------------------------------------------------
    println("\n3. [PAGINACIÓN] Uso de \$sort, \$skip y \$limit:")
    println("   (Mostrando la 'Página 2' de películas, ordenadas alfabéticamente)")
    println("   (Saltamos las 2 primeras y mostramos las 2 siguientes)")

    val pipeline = listOf(
        Aggregates.sort(Sorts.ascending("tituloPeliJSON")), // 1. Ordenar A-Z
        Aggregates.skip(2),                                 // 2. Saltar las 2 primeras
        Aggregates.limit(2)                                 // 3. Coger solo 2
    )

    var contador = 1
    coleccionPeliculas.aggregate(pipeline).forEach { doc ->
        println("   Result $contador: ${doc.getString("tituloPeliJSON")}")
        contador++
    }


    // ---------------------------------------------------------
    // 4. REEMPLAZO TOTAL (replaceOne)
    // ---------------------------------------------------------
    println("\n4. [REPLACE] Sustitución completa de un documento:")

    // Vamos a usar el Cliente con ID 1 para el ejemplo
    val idCliente = 1
    val clienteOriginal = coleccionClientes.find(Filters.eq("ID", idCliente)).firstOrNull()

    if (clienteOriginal != null) {
        println("   - Original: ${clienteOriginal.getString("Nombre")} (Club Fav: ${clienteOriginal.get("IdVideoclubFav")})")

        // Creamos un documento NUEVO. Fíjate que si no pongo "idPeliculaMasVista", se pierde.
        // replaceOne NO mezcla datos, borra el viejo y pone el nuevo.
        val clienteNuevo = Document("ID", idCliente)
            .append("Nombre", "Juan (MODIFICADO POR REPLACE)")
            .append("IdVideoclubFav", 999) // Cambiamos el club
            .append("idPeliculaMasVista", 1) // Mantenemos la peli
            .append("Nota", "Cliente vip actualizado") // Campo nuevo extra

        val resultado = coleccionClientes.replaceOne(Filters.eq("ID", idCliente), clienteNuevo)

        println("   - Documentos modificados: ${resultado.modifiedCount}")

        // Verificamos
        val clienteVerificado = coleccionClientes.find(Filters.eq("ID", idCliente)).first()
        println("   - Ahora es: ${clienteVerificado.getString("Nombre")} (Club Fav: ${clienteVerificado.get("IdVideoclubFav")})")

    } else {
        println("   (No se encontró el cliente ID 1 para la prueba)")
    }

    println("\n=============================================")
}