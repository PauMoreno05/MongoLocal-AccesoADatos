package org.example

import com.mongodb.client.MongoClients
import org.bson.Document
import org.bson.json.JsonWriterSettings
import java.io.File

fun exportarDatos() {
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

    // Nombre del archivo por defecto
    val nombreArchivo = "peliculas_export.json"

    try {
        val settings = JsonWriterSettings.builder().indent(true).build()
        val file = File(nombreArchivo)

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
        println("✅ Exportación completada exitosamente en: $nombreArchivo")

    } catch (e: Exception) {
        println("❌ Error al exportar: ${e.message}")
    } finally {
        cliente.close()
    }
}


fun importarDatos() {
    val nombreArchivo = "peliculas_export.json"
    val jsonFile = File(nombreArchivo)

    if (!jsonFile.exists()) {
        println("❌ No se encuentra el archivo '$nombreArchivo'. Primero exporta algo.")
        return
    }

    println("Iniciando importación desde $nombreArchivo...")

    // 1. Conexión
    val cliente = MongoClients.create(NOM_SRV)
    val db = cliente.getDatabase(NOM_BD)
    val coleccion = db.getCollection(NOM_COLECCION)

    try {
        // 2. Leer el texto del archivo
        val jsonText = jsonFile.readText()

        // 3. Parsear el JSON
        // TRUCO: Como el archivo es un Array [...] y Document.parse espera un Objeto {...},
        // envolvemos el texto en una propiedad temporal para que Mongo lo parsee por nosotros.
        // Así no necesitas la librería 'org.json'.
        val jsonWrapper = "{ \"lista\": $jsonText }"
        val docWrapper = Document.parse(jsonWrapper)
        val listaDocumentos = docWrapper.getList("lista", Document::class.java)

        if (listaDocumentos.isNullOrEmpty()) {
            println("⚠️ El archivo JSON está vacío o no tiene formato correcto.")
            return
        }

        coleccion.drop()
        println("   -> Colección anterior borrada.")

        val docsAInsertar = listaDocumentos.map { doc ->
            doc
        }

        // 6. Insertar
        coleccion.insertMany(docsAInsertar)
        println("✅ Importación completada: ${docsAInsertar.size} películas insertadas.")

    } catch (e: Exception) {
        println("❌ Error al importar: ${e.message}")
        e.printStackTrace()
    } finally {
        cliente.close()
    }
}