package org.example

import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Sorts
import org.bson.Document

fun buscarVideoclubPorTituloPeli() {
    print("Introduce el título de la película buscada: ")
    val tituloBusqueda = scanner.nextLine()

    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION2)

    val pipeline = listOf(
        Document(
            "\$lookup", Document()
                .append("from", NOM_COLECCION)
                .append("localField", "idPeliculaMasVendida")
                .append("foreignField", "idPeliculaJSON")
                .append("as", "infoPeli")
        ),

        Document("\$unwind", "\$infoPeli"),

        Document("\$match", Document("infoPeli.tituloPeliJSON",
            Document("\$regex", tituloBusqueda).append("\$options", "i"))
        )
    )

    println("\n--- Resultados de búsqueda para: \"$tituloBusqueda\" ---")

    var encontrados = 0
    col.aggregate(pipeline).forEach { doc ->
        encontrados++

        // Datos del Videoclub
        val nombreClub = doc.getString("VideoClub")
        val empleados = doc.getInteger("NumeroEmpleados")

        val peliDoc = doc["infoPeli"] as Document
        val tituloPeli = peliDoc.getString("tituloPeliJSON")
        val director = peliDoc.getString("directorJSON")

        println("Videoclub: $nombreClub (Empleados: $empleados)")
        println("   -> Película Top Ventas: $tituloPeli (Dir. $director)")
        println("------------------------------------------------")
    }

    if (encontrados == 0) {
        println("No se encontraron videoclubs donde la película más vendida sea '$tituloBusqueda'.")
    }
}

fun listarClientesCompleto() {
    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION3)

    println("=== LISTADO COMPLETO DE CLIENTES ===")

    val pipeline = listOf(

        Document("\$lookup", Document()
            .append("from", NOM_COLECCION)
            .append("localField", "idPeliculaMasVista")
            .append("foreignField", "idPeliculaJSON")
            .append("as", "datosPeli")
        ),
        Document("\$unwind", "\$datosPeli"),

        // 2. SEGUNDO CRUCE: Traer datos del Videoclub
        Document("\$lookup", Document()
            .append("from", NOM_COLECCION2)
            .append("localField", "IdVideoclubFav")
            .append("foreignField", "ID")
            .append("as", "datosClub")
        ),
        Document("\$unwind", "\$datosClub")
    )

    col.aggregate(pipeline).forEach { doc ->
        // Datos del Cliente
        val nombreCliente = doc.getString("Nombre")

        // Datos extraídos de la Película
        val peliDoc = doc["datosPeli"] as Document
        val tituloPeli = peliDoc.getString("tituloPeliJSON")

        // Datos extraídos del Videoclub
        val clubDoc = doc["datosClub"] as Document
        val nombreClub = clubDoc.getString("VideoClub")

        // Imprimir resultado
        println("Cliente: $nombreCliente")
        println(" - Película más vista: $tituloPeli")
        println(" - Videoclub Favorito: $nombreClub")
        println("----------------------------------")
    }
}

fun buscarFansDePelicula() {
    print("Introduce el título de la película a investigar: ")
    val tituloBusqueda = scanner.nextLine()

    // 1. Empezamos buscando en la colección de PELÍCULAS
    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION)

    val pipeline = listOf(
        Document("\$match", Document("tituloPeliJSON",
            Document("\$regex", tituloBusqueda).append("\$options", "i"))
        ),

        Document("\$lookup", Document()
            .append("from", NOM_COLECCION2)
            .append("localField", "idPeliculaJSON")
            .append("foreignField", "PeliFavoritaDueñoVideoclub")
            .append("as", "videoclubsFans")
        ),

        Document("\$lookup", Document()
            .append("from", NOM_COLECCION3)
            .append("localField", "idPeliculaJSON")
            .append("foreignField", "idPeliculaMasVista")
            .append("as", "clientesFans")
        )
    )

    val resultados = col.aggregate(pipeline).toList()

    if (resultados.isEmpty()) {
        println("No se encontró ninguna película con el título '$tituloBusqueda'.")
    } else {
        resultados.forEach { doc ->
            val titulo = doc.getString("tituloPeliJSON")
            println("=== RESULTADOS PARA: $titulo ===")

            // A) Imprimir Videoclubs
            val listaClubs = doc["videoclubsFans"] as List<Document>
            if (listaClubs.isEmpty()) {
                println("Ningún dueño de videoclub tiene esta peli como favorita.")
            } else {
                println("Es la favorita del dueño de:")
                listaClubs.forEach { club ->
                    println("- ${club.getString("VideoClub")}")
                }
            }

            // B) Imprimir Clientes
            val listaClientes = doc["clientesFans"] as List<Document>
            if (listaClientes.isEmpty()) {
                println("Ningún cliente tiene esta peli como su más vista.")
            } else {
                println("Es la más vista por los clientes:")
                listaClientes.forEach { cli ->
                    println("- ${cli.getString("Nombre")}")
                }
            }
            println("======================================")
        }
    }
}

fun tutorialAggregates() {
    val colPelis = coleccionPeliculas

    println("\n--- 1. MATCH y PROJECT (Filtrar y Moldear) ---")
    // Equivalente a: SELECT titulo, duracion FROM peliculas WHERE duracion > 2.5
    val etapa1 = listOf(
        // $match: Filtra documentos (WHERE)
        Aggregates.match(Filters.gt("duracionHorasJSON", 2.5)),

        // $project: Selecciona campos (SELECT)
        Aggregates.project(
            Projections.fields(
            Projections.include("tituloPeliJSON", "duracionHorasJSON"),
            Projections.excludeId()
        ))
    )
    colPelis.aggregate(etapa1).forEach { println(it.toJson()) }


    println("\n--- 2. SORT, SKIP y LIMIT (Paginación) ---")
    // Equivalente: Ordenar por duración desc, saltar la 1ª y coger las 2 siguientes
    val etapa2 = listOf(
        // $sort: Ordena resultados
        Aggregates.sort(Sorts.descending("duracionHorasJSON")),

        // $skip: Omite documentos
        Aggregates.skip(1),

        // $limit: Limita resultados
        Aggregates.limit(2)
    )
    colPelis.aggregate(etapa2).forEach {
        println("Peli: ${it.getString("tituloPeliJSON")} - ${it.getDouble("duracionHorasJSON")}h")
    }


    println("\n--- 3. GROUP y COUNT (Estadísticas) ---")
    // $group: Agrupa documentos (GROUP BY)
    // Aquí agrupamos por NADA (null) para calcular la media total
    val etapa3 = listOf(
        Aggregates.group(null,
            Accumulators.avg("duracionPromedio", "\$duracionHorasJSON"),
            Accumulators.sum("totalPeliculas", 1)
        )
    )
    val estadisticas = colPelis.aggregate(etapa3).firstOrNull()
    println("Estadísticas Globales: $estadisticas")

    // $count: Cuenta documentos resultantes
    // Ejemplo simple: ¿Cuántas pelis de 'Drama' hay?
    // Nota: $count suele ir al final del pipeline
    val etapaCount = listOf(
        Aggregates.match(Filters.eq("generosJSON", "Drama")), // Filtramos primero
        Aggregates.count("totalDramas")
    )
    val conteo = colPelis.aggregate(etapaCount).firstOrNull()
    println("Total de Dramas encontrados: ${conteo?.get("totalDramas")}")


    println("\n--- 4. LOOKUP y UNWIND (Relaciones) ---")
    // $lookup: Unión entre colecciones (JOIN)
    // $unwind: Descompone arrays
    val etapa4 = listOf(
        // Buscamos clientes que hayan visto la película ID 1
        Aggregates.match(Filters.eq("idPeliculaMasVista", 1)),

        Aggregates.lookup(
            "peliculas",            // Colección destino (from)
            "idPeliculaMasVista",   // Campo local (localField)
            "idPeliculaJSON",       // Campo foráneo (foreignField)
            "infoPeli"              // Nombre del array resultante (as)
        ),

        // Como lookup devuelve un Array, unwind lo convierte en objeto
        Aggregates.unwind("\$infoPeli")
    )

    // Ejecutamos sobre la colección de CLIENTES
    coleccionClientes.aggregate(etapa4).forEach { doc ->
        val cliente = doc.getString("Nombre")
        val peli = doc.get("infoPeli", Document::class.java).getString("tituloPeliJSON")
        println("Cliente: $cliente -> Vio: $peli")
    }
}