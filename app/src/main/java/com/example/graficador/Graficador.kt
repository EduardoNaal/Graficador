package com.example.graficador

import kotlin.math.pow

// Clase para manejar todo el proceso de graficación
class Graficador {
    // Define la prioridad de cada operador matemático
    private val prioridadOperadores = mapOf(
        '+' to 1,
        '-' to 1,
        '*' to 2,
        '/' to 2,
        '^' to 3
    )

    // Convierte expresión normal a notación postfija (más fácil de calcular)
    fun convertirInfijaAPostfija(expresion: String): String {
        val pilaOperadores = Pila<Char>()
        val resultado = StringBuilder()
        val expresionLimpia = expresion.replace(" ", "")

        for (caracter in expresionLimpia) {
            when {
                // Si es número o variable x, lo añadimos directamente
                caracter.isDigit() || caracter == 'x' -> resultado.append(caracter)

                // Si es paréntesis que abre, lo metemos a la pila
                caracter == '(' -> pilaOperadores.push(caracter)

                // Si es paréntesis que cierra, vaciamos hasta encontrar el que abre
                caracter == ')' -> {
                    while (!pilaOperadores.estaVacia() && pilaOperadores.peek() != '(') {
                        resultado.append(pilaOperadores.pop())
                    }
                    pilaOperadores.pop() // Quitamos el '(' de la pila
                }

                // Si es operador matemático (+, -, *, /, ^)
                else -> {
                    // Reorganizamos los operadores según su prioridad
                    while (!pilaOperadores.estaVacia() &&
                        pilaOperadores.peek() != '(' &&
                        prioridadOperadores[pilaOperadores.peek()]!! >= prioridadOperadores[caracter]!!
                    ) {
                        resultado.append(pilaOperadores.pop())
                    }
                    pilaOperadores.push(caracter)
                }
            }
        }

        // Vaciamos los operadores que quedaron en la pila
        while (!pilaOperadores.estaVacia()) {
            resultado.append(pilaOperadores.pop())
        }

        return resultado.toString()
    }

    // Calcula el valor de y para un valor específico de x
    fun calcularPunto(x: Double, expresionPostfija: String): Double {
        val pilaNumeros = Pila<Double>()

        for (caracter in expresionPostfija) {
            when {
                // Si es la variable x, usamos el valor proporcionado
                caracter == 'x' -> pilaNumeros.push(x)

                // Si es número, lo convertimos a Double
                caracter.isDigit() -> pilaNumeros.push(caracter.toString().toDouble())

                // Si es operador, hacemos el cálculo
                else -> {
                    val numeroB = pilaNumeros.pop()
                    val numeroA = pilaNumeros.pop()
                    val resultado = realizarOperacion(numeroA, numeroB, caracter)
                    pilaNumeros.push(resultado)
                }
            }
        }

        return pilaNumeros.pop()
    }

    // Genera múltiples puntos para hacer la gráfica
    fun generarPuntos(inicio: Double, fin: Double, cantidadPuntos: Int, postfija: String): Array<DoubleArray> {
        // Validaciones básicas
        if (cantidadPuntos <= 0) throw Exception("Debes pedir al menos 1 punto")
        if (inicio > fin) throw Exception("El inicio no puede ser mayor al final")

        val puntos = Array(cantidadPuntos) { DoubleArray(2) }
        val distanciaEntrePuntos = if (cantidadPuntos == 1) 0.0 else (fin - inicio) / (cantidadPuntos - 1)

        // Calculamos cada punto
        for (i in 0 until cantidadPuntos) {
            val x = inicio + i * distanciaEntrePuntos
            puntos[i][0] = x
            puntos[i][1] = calcularPunto(x, postfija)
        }

        return puntos
    }

    // Realiza las operaciones matemáticas básicas
    private fun realizarOperacion(a: Double, b: Double, operador: Char): Double {
        return when (operador) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> {
                if (b == 0.0) throw Exception("No se puede dividir entre cero")
                a / b
            }
            '^' -> a.pow(b)
            else -> throw Exception("Operador desconocido: $operador")
        }
    }
}