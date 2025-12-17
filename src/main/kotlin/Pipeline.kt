package org.example

import org.bson.Document

fun buscarVideoclubPorTituloPeli() {
    // Pedimos el título al usuario
    print("Introduce el título de la película buscada: ")
    val tituloBusqueda = scanner.nextLine()

    // 1. Trabajamos sobre la colección de VIDEOCLUBS (porque queremos listar videoclubs)
    val col = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION2)

    val pipeline = listOf(
        // LOOKUP: Unimos Videoclubs con Películas
        Document(
            "\$lookup", Document()
                .append("from", NOM_COLECCION)          // Tabla destino: "peliculas"
                .append("localField", "idPeliculaMasVendida") // Campo en videoclubs
                .append("foreignField", "idPeliculaJSON")     // Campo en peliculas
                .append("as", "infoPeli")               // Nombre del campo resultante
        ),

        // UNWIND: Aplanamos el array para acceder a los datos de la peli directamente
        Document("\$unwind", "\$infoPeli"),

        // MATCH: Filtramos para que solo salgan los videoclubs que tienen esa peli
        // Usamos regex para que no importen mayúsculas/minúsculas
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

        // Datos de la Película (que ahora están dentro de 'infoPeli')
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
        // 1. PRIMER CRUCE: Traer datos de la Película
        Document("\$lookup", Document()
            .append("from", NOM_COLECCION)          // Colección "peliculas"
            .append("localField", "idPeliculaMasVista") // Campo en Clientes
            .append("foreignField", "idPeliculaJSON")   // Campo en Películas
            .append("as", "datosPeli")              // Resultado temporal
        ),
        Document("\$unwind", "\$datosPeli"), // Aplanar array de peli

        // 2. SEGUNDO CRUCE: Traer datos del Videoclub
        Document("\$lookup", Document()
            .append("from", NOM_COLECCION2) // Colección "videoclubs"
            .append("localField", "IdVideoclubFav")   // Campo en Clientes
            .append("foreignField", "ID")             // Campo en Videoclubs
            .append("as", "datosClub")                // Resultado temporal
        ),
        Document("\$unwind", "\$datosClub")  // Aplanar array de videoclub
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