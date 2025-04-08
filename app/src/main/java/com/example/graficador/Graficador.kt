package com.example.graficador

import kotlin.math.*

class Graficador {
    // Tabla de prioridad para los operadores matemáticos
    // Entre más alto el número, más prioridad tiene
    private val prioridadOperadores = mapOf(
        '(' to 0,
        '+' to 1,
        '-' to 1,
        '*' to 2,
        '/' to 2,
        '^' to 4
    )

    // Convierte una expresión normal (infija) a postfija
    // Ej: "3 + 4" -> "3 4 +"
    fun convertirInfijaAPostfija(expresion: String): String {
        val pilaOperadores = Pila<Char>()  // Pila temporal para operadores
        val resultado = StringBuilder()    // Aquí construimos el resultado
        var numeroTemp = StringBuilder()   // Acumula dígitos para números multi-dígito

        // Recorremos cada carácter de la expresión limpia
        for (caracter in expresion.replace(" ", "")) {
            when {
                // Si es número o punto decimal, lo acumulamos
                caracter.isDigit() || caracter == '.' -> numeroTemp.append(caracter)

                // Si es una 'x' la agregamos directamente al resultado
                caracter == 'x' -> {
                    if (numeroTemp.isNotEmpty()) {
                        resultado.append(numeroTemp).append(" ")
                        numeroTemp.clear()
                    }
                    resultado.append("x ")  // Agregamos la variable
                }

                // Si es un operador o paréntesis
                else -> {
                    if (numeroTemp.isNotEmpty()) {
                        resultado.append(numeroTemp).append(" ")
                        numeroTemp.clear()
                    }
                    when (caracter) {
                        '(' -> pilaOperadores.push(caracter)
                        ')' -> {
                            while (!pilaOperadores.estaVacia() && pilaOperadores.peek() != '(') {
                                resultado.append(pilaOperadores.pop()).append(" ")
                            }
                            pilaOperadores.pop()  // Quitamos el '('
                        }
                        else -> {
                            while (!pilaOperadores.estaVacia() &&
                                prioridadOperadores[pilaOperadores.peek()]!! >= prioridadOperadores[caracter]!!
                            ) {
                                resultado.append(pilaOperadores.pop()).append(" ")
                            }
                            pilaOperadores.push(caracter)
                        }
                    }
                }
            }
        }

        if (numeroTemp.isNotEmpty()) resultado.append(numeroTemp).append(" ")
        while (!pilaOperadores.estaVacia()) {
            resultado.append(pilaOperadores.pop()).append(" ")
        }

        return resultado.toString().trim()  // Quitamos espacios extras
    }

    // Calcula el valor de la expresión postfija para un valor de x dado
    fun calcularPunto(x: Double, expresionPostfija: String): Double {
        val pilaNumeros = Pila<Double>()  // Pila para operandos
        val tokens = expresionPostfija.split(" ").filter { it.isNotBlank() }

        for (token in tokens) {
            when {
                token == "x" -> pilaNumeros.push(x)  // Inserto el valor de x
                token.matches(Regex("-?\\d+(\\.\\d+)?")) -> pilaNumeros.push(token.toDouble())
                else -> {
                    val b = pilaNumeros.pop()  // Segundo operando
                    val a = pilaNumeros.pop()  // Primer operando
                    when (token) {
                        "+" -> pilaNumeros.push(a + b)
                        "-" -> pilaNumeros.push(a - b)
                        "*" -> pilaNumeros.push(a * b)
                        "/" -> pilaNumeros.push(a / b)
                        "^" -> pilaNumeros.push(a.pow(b))
                    }
                }
            }
        }
        return pilaNumeros.pop()  // Resultado final
    }

    // Genera puntos equiespaciados entre inicio y fin usando la expresión postfija
    fun generarPuntos(inicio: Double, fin: Double, cantidadPuntos: Int, postfija: String): Array<DoubleArray> {
        if (cantidadPuntos <= 0) throw Exception("Debe haber al menos 1 punto")
        if (inicio > fin) throw Exception("Inicio debe ser menor que fin")

        val puntos = Array(cantidadPuntos) { DoubleArray(2) }
        val paso = if (cantidadPuntos == 1) 0.0 else (fin - inicio) / (cantidadPuntos - 1)

        for (i in 0 until cantidadPuntos) {
            val x = inicio + i * paso
            puntos[i][0] = x
            puntos[i][1] = calcularPunto(x, postfija)
        }
        return puntos
    }

    // Preprocesa la expresión: quita espacios y añade '*' implícitos
    fun preprocesarExpresion(expresion: String): String {
        return expresion.replace(" ", "")
            .replace(Regex("(?<=[0-9x)])(?=[(x])"), "*")  // Inserta '*' 0*x, 1*x, etc.
            .replace(Regex("(?<=[^0-9x)])(?=-)"), "0")    // Inserta '0' 0-x, 1-x, etc.
    }
}
