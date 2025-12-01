package org.example

import java.util.InputMismatchException

fun main() {
    menu()
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
        println("------- ARCHIVOS -------")
        println("6. Exportar BD a JSON")
        println("7. Importar BD desde JSON")
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
                6 -> exportarDatos()
                7 -> importarDatos()
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

            if (opcionConsulta in 1..5) {
                println("(Presiona ENTER para continuar)")
                scanner.nextLine()
            }

        } catch (e: NumberFormatException) {
            println("Error: Introduce un número para la consulta.")
        }
    }
}