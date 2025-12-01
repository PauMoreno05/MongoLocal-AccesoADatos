package org.example

import java.util.InputMismatchException

fun main() {
    var opcion: Int = -1

    while (opcion != 0) {
        println("========================")
        println("   GESTIÓN FILMOTECA    ")
        println("========================")
        println("1. Mostrar todas las películas")
        println("2. Insertar película")
        println("3. Actualizar duración")
        println("4. Eliminar película")
        println("5. MENU DE CONSULTAS --->")
        println("-------------------------")
        println("0. Salir")
        print("Selecciona una opción: ")

        try {
            val entrada = scanner.nextLine()
            opcion = if (entrada.isBlank()) -1 else entrada.toInt()

            when (opcion) {
                1 -> mostrarPeliclas()
                2 -> insertarPelicula()
                3 -> actualizarDuracion()
                4 -> eliminarPelicula()
                5 -> menuConsultas()
                0 -> println("Cerrando aplicación")
                else -> println("Opción no válida. Inténtalo de nuevo.")
            }
        } catch (e: NumberFormatException) {
            println("Error: Por favor, introduce un número válido.")
        } catch (e: Exception) {
            println("Error inesperado: ${e.message}")
        }
    }
}


fun menuConsultas() {
    var opcionConsulta: Int = -1

    while (opcionConsulta != 0) {
        println("\n   --- CONSULTAS AVANZADAS ---")
        println("   1. Películas > 2 horas (Filters.gt)")
        println("   2. Películas < 2 horas (Filters.lt)")
        println("   3. Ver solo títulos (Projections)")
        println("   4. Ver duración media (Aggregates \$group)")
        println("   5. Top 3 más largas (Aggregates \$limit)")
        println("   ---------------------------")
        println("   0. Volver al menú principal")
        print("   >> Elige una consulta: ")

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
                else -> println("   ❌ Opción de consulta no válida.")
            }

            if (opcionConsulta in 1..5) {
                println("   (Presiona ENTER para continuar)")
                scanner.nextLine()
            }

        } catch (e: NumberFormatException) {
            println("Error: Introduce un número para la consulta.")
        }
    }
}