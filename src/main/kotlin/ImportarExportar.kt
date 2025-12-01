package org.example

import com.mongodb.client.MongoClients
import org.bson.Document
import org.bson.json.JsonWriterSettings
import java.io.File

fun exportarDatos() {
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

    // 1. Construimos la ruta dinámica a src/main/resources
    val rutaProyecto = System.getProperty("user.dir")
    val rutaResources = "$rutaProyecto/src/main/resources"
    val nombreArchivo = "peliculas_export.json"

    // Objeto File completo
    val file = File(rutaResources, nombreArchivo)

    try {
        // Asegurarnos de que la carpeta existe (por si acaso)
        val carpeta = File(rutaResources)
        if (!carpeta.exists()) {
            carpeta.mkdirs()
        }

        println("Guardando en: ${file.absolutePath}")

        val settings = JsonWriterSettings.builder().indent(true).build()

        file.printWriter().use { out ->
            out.println("[")
            val cursor = coleccion.find().iterator()
            var first = true
            while (cursor.hasNext()) {
                if (!first) out.println(",")
                val doc = cursor.next()
                out.print(doc.toJson(settings))
                first = false
            }
            out.println()
            out.println("]")
            cursor.close()
        }
        println("Exportación completada.")

    } catch (e: Exception) {
        println("Error al exportar: ${e.message}")
    } finally {
        cliente.close()
    }
}


fun importarDatos() {
    // 1. Construimos la misma ruta dinámica
    val rutaProyecto = System.getProperty("user.dir")
    val rutaResources = "$rutaProyecto/src/main/resources"
    val nombreArchivo = "peliculas_export.json"

    val jsonFile = File(rutaResources, nombreArchivo)

    println("Buscando archivo en: ${jsonFile.absolutePath}")

    if (!jsonFile.exists()) {
        println("No se encuentra el archivo en resources. Primero usa la opción Exportar.")
        return
    }

    println("Iniciando importación...")

    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

    try {
        val jsonText = jsonFile.readText()

        // TRUCO: Envolvemos el array en un objeto para parsearlo nativamente con Mongo Driver
        val jsonWrapper = "{ \"lista\": $jsonText }"
        val docWrapper = Document.parse(jsonWrapper)
        val listaDocumentos = docWrapper.getList("lista", Document::class.java)

        if (listaDocumentos.isNullOrEmpty()) {
            println("⚠️ El archivo JSON está vacío.")
            return
        }

        coleccion.drop() // Borramos lo anterior
        println("-> Colección limpiada.")

        coleccion.insertMany(listaDocumentos)
        println("Importación completada: ${listaDocumentos.size} películas insertadas.")

    } catch (e: Exception) {
        println("Error al importar: ${e.message}")
        e.printStackTrace()
    } finally {
        cliente.close()
    }
}