package com.example.graficador

//Clase para convertir expresiones en notación infija a postfija y evaluar funciones matemáticas.

class Graficador {

    /**
     * Convierte una expresión matemática infija a postfija.
     * Utiliza una pila para manejar la precedencia de los operadores.
     * @param expresion La expresión en notación infija.
     * @return La expresión convertida en notación postfija.
     */

    fun infijaAPostfija(expresion: String): String {
        val pila = Pila<Char>()
        val output = StringBuilder()
        val precedencia = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2, '^' to 3)

        // Itera sobre cada carácter de la expresión (sin espacios)

        for (caracter in expresion.replace(" ", "")) {
            when {
                caracter.isDigit() || caracter == 'x' -> output.append(caracter) // Números y variable 'x'
                caracter == '(' -> pila.push(caracter) // Paréntesis izquierdo
                caracter == ')' -> {

                    // Desapila hasta encontrar el paréntesis izquierdo

                    while (!pila.estaVacia() && pila.peek() != '(') {
                        output.append(pila.pop())
                    }
                    pila.pop() // Elimina el paréntesis izquierdo de la pila
                }
                else -> {

                    // Manejo de operadores según su precedencia

                    while (!pila.estaVacia() &&
                        pila.peek() != '(' &&
                        (precedencia[pila.peek()] ?: 0) >= (precedencia[caracter] ?: 0)) {
                        output.append(pila.pop())
                    }
                    pila.push(caracter)
                }
            }
        }

        // Vacía la pila al final

        while (!pila.estaVacia()) {
            output.append(pila.pop())
        }

        return output.toString()
    }

    /**
     * Evalúa el valor de una expresión en notación postfija para un valor dado de 'x'.
     * @param x Valor de la variable 'x'.
     * @param expresionPostfija Expresión en notación postfija.
     * @return Resultado numérico de la evaluación.
     */

    fun evaluarPunto(x: Double, expresionPostfija: String): Double {
        val pila = Pila<Double>()

        for (caracter in expresionPostfija) {
            when {
                caracter.isDigit() -> pila.push(caracter.toString().toDouble()) // Convierte números en operandos
                caracter == 'x' -> pila.push(x) // Reemplaza 'x' por su valor
                else -> {

                    // Se extraen los dos últimos operandos de la pila

                    val b = pila.pop()
                    val a = pila.pop()

                    // Se realiza la operación correspondiente

                    pila.push(
                        when (caracter) {
                            '+' -> a + b
                            '-' -> a - b
                            '*' -> a * b
                            '/' -> a / b
                            '^' -> Math.pow(a, b)
                            else -> throw IllegalArgumentException("Operador no válido")
                        }
                    )
                }
            }
        }
        return pila.peek()
    }

    /**
     * Evalúa una función matemática en un rango de valores.
     * @param inicio Valor inicial del rango.
     * @param fin Valor final del rango.
     * @param pasos Cantidad de puntos a evaluar.
     * @param expresionPostfija Expresión en notación postfija.
     * @return Un array de pares (x, y) con los valores evaluados.
     */

    fun evaluarRango(
        inicio: Double,
        fin: Double,
        pasos: Int,
        expresionPostfija: String
    ): Array<DoubleArray> {
        val puntos = Array(pasos) { DoubleArray(2) }
        val incremento = (fin - inicio) / (pasos - 1)

        // Calcula los valores de la función para cada punto en el rango

        for (i in 0 until pasos) {
            puntos[i][0] = inicio + i * incremento // Valor de x
            puntos[i][1] = evaluarPunto(puntos[i][0], expresionPostfija) // Valor de y = f(x)
        }
        return puntos
    }
}
