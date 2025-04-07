package com.example.graficador

/**
 * Clase Pila simplificada para principiantes
 * Funciona como una pila de platos: el último que entra es el primero que sale
 */
class Pila<T> {
    // Usamos una lista mutable para guardar los elementos
    private val elementos = mutableListOf<T>()

    /**
     * Agrega un elemento a la parte superior de la pila
     * @param elemento El elemento a agregar
     */
    fun push(elemento: T) {
        elementos.add(elemento)
    }

    /**
     * Quita y devuelve el elemento de la parte superior de la pila
     * @return El elemento removido
     * @throws IllegalStateException si la pila está vacía
     */
    fun pop(): T {
        if (estaVacia()) {
            throw IllegalStateException("¡No puedes sacar elementos de una pila vacía!")
        }
        return elementos.removeAt(elementos.size - 1)
    }

    /**
     * Mira el elemento en la parte superior sin quitarlo
     * @return El elemento en la cima
     * @throws IllegalStateException si la pila está vacía
     */
    fun peek(): T {
        if (estaVacia()) {
            throw IllegalStateException("¡No hay nada que ver, la pila está vacía!")
        }
        return elementos.last()
    }

    /**
     * Verifica si la pila está vacía
     * @return true si está vacía, false si tiene elementos
     */
    fun estaVacia(): Boolean = elementos.isEmpty()

    /**
     * Cuenta cuántos elementos hay en la pila
     * @return El número de elementos
     */
    fun tamano(): Int = elementos.size

    /**
     * Vacía toda la pila
     */
    fun limpiar() {
        elementos.clear()
    }

    /**
     * Muestra los elementos como una cadena de texto
     * @return String representando la pila
     */
    override fun toString(): String {
        return "Pila: ${elementos.joinToString(" -> ")} (tope)"
    }
}