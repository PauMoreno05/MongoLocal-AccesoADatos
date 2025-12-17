package org.example

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend
import org.bson.Document
import java.util.InputMismatchException

lateinit var servidor: MongoServer
lateinit var cliente: MongoClient
lateinit var uri: String
lateinit var coleccionPeliculas: MongoCollection<Document>
lateinit var coleccionVideoclubs: MongoCollection<Document>
lateinit var coleccionClientes: MongoCollection<Document>

//BD y colección con la que se trabajará
const val NOM_BD = "filmoteca"
const val NOM_COLECCION = "peliculas"
const val NOM_COLECCION2 = "vieoclubs"
const val NOM_COLECCION3 = "clientes"

// Función para conectar a la BD
fun conectarBD() {
    servidor = MongoServer(MemoryBackend())
    val address = servidor.bind()
    uri = "mongodb://${address.hostName}:${address.port}"

    cliente = MongoClients.create(uri)
    coleccionPeliculas = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION)
    coleccionVideoclubs = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION2)
    coleccionClientes = cliente.getDatabase(NOM_BD).getCollection(NOM_COLECCION3)

    println("Servidor MongoDB en memoria iniciado en $uri")
}

// Función para desconectar a la BD
fun desconectarBD() {
    cliente.close()
    servidor.shutdown()
    println("Servidor MongoDB en memoria finalizado")
}



fun menu() {
    var opcion: Int = -1

    while (opcion != 0) {
        println("========================")
        println("   FILMOTECA KOTLIN     ")
        println("========================")
        println("1. Listar películas")
        println("2. Insertar película")
        println("3. Actualizar duración")
        println("4. Eliminar película")
        println("5. Consultas avanzadas")
        println("6. Consultas con mas de un archivo")
        println("------------------------")
        println("0. Salir")
        print("Elige opción: ")

        try {
            val entrada = scanner.nextLine()
            opcion = if (entrada.isBlank()) -1 else entrada.toInt()

            when (opcion) {
                1 -> mostrarPeliclas()
                2 -> insertarPelicula()
                3 -> actualizarDuracion()
                4 -> eliminarPelicula()
                5 -> menuConsultas()
                6 -> menuMasArchivos()
                0 -> println("Adios!")
                else -> println("Opción incorrecta")
            }
        } catch (e: Exception) {
            println("Error: Introduce un número válido.")
        }
    }
}


fun menuConsultas() {
    var opcionConsulta: Int = -1

    while (opcionConsulta != 0) {
        println("====CONSULTAS AVANZADAS====")
        println("1. Películas > 2 horas (Filters.gt)")
        println("2. Películas < 2 horas (Filters.lt)")
        println("3. Ver solo títulos (Projections)")
        println("4. Ver duración media (Aggregates \$group)")
        println("5. Top 3 más largas (Aggregates \$limit)")
        println("==========================================")
        println("0. Volver al menú principal")
        print("Elige una consulta: ")

        try {
            val entrada = scanner.nextLine()
            opcionConsulta = if (entrada.isBlank()) -1 else entrada.toInt()

            when (opcionConsulta) {
                1 -> duracionMas2Pelis()
                2 -> duracionMenos2Pelis()
                3 -> tituloPelis()
                4 -> duracionMedia()
                5 -> tresPelisMasLargas()
                0 -> println("Volviendo al menú principal...")
                else -> println("Opción de consulta no válida.")
            }

        } catch (e: NumberFormatException) {
            println("Error: Introduce un número para la consulta.")
        }
    }
}

fun menuMasArchivos() {
    var opcionConsulta: Int = -1

    while (opcionConsulta != 0) {
        println("====CONSULTAS CON VARIOS ARCHIVOS====")
        println("1. Listar el videoclub de la pelicula mas vendida en este")
        println("2. Listar el Cliente junto a su pelicula mas vista y su videoclub favorito")
        println("3. Buscar videoclub y cliente por su película favorita")
        println("==========================================")
        println("0. Volver al menú principal")
        print("Elige una consulta: ")

        try {
            val entrada = scanner.nextLine()
            opcionConsulta = if (entrada.isBlank()) -1 else entrada.toInt()

            when (opcionConsulta) {
                1 -> buscarVideoclubPorTituloPeli()
                2 -> listarClientesCompleto()
                3 -> buscarFansDePelicula()
                0 -> println("Volviendo al menú principal...")
                else -> println("Opción de consulta no válida.")
            }

        } catch (e: NumberFormatException) {
            println("Error: Introduce un número para la consulta.")
        }
    }
}

fun main() {
    conectarBD()
    importarBD("src/main/resources/peliculas_export.json", coleccionPeliculas)
    importarBD("src/main/resources/videoclubs_export.json", coleccionVideoclubs)
    importarBD("src/main/resources/clientes_export.json", coleccionClientes)

    menu()

    exportarBD(coleccionPeliculas,"src/main/resources/peliculas_export.json")
    exportarBD(coleccionVideoclubs,"src/main/resources/videoclubs_export.json")
    exportarBD(coleccionClientes,"src/main/resources/clientes_export.json")

    desconectarBD()

}