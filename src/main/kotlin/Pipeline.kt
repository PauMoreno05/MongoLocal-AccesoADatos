package org.example

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