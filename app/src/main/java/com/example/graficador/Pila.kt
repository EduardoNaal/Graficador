package com.example.graficador

/*Clase que representa un nodo en una lista enlazada
* Cada nodo guarda un dato y tiene referencia al siguiente nodo
*/
class Nodo<T>(val dato: T, var siguiente: Nodo<T>? = null)

// Clase que implementa una pila usando nodos enlazados
class Pila<T> {
    private var cima: Nodo<T>? = null  // El nodo en la parte superior de la pila

    // Mete un elemento a la pila
    fun push(elemento: T) {
        val nuevoNodo = Nodo(elemento, cima)  // Crea nodo que apunta a la vieja cima
        cima = nuevoNodo  // La nueva cima es nuestro nodo
    }

    // Saca el elemento de la cima de la pila
    fun pop(): T {
        if (estaVacia()) throw NoSuchElementException("Pila vacía")
        val dato = cima!!.dato  // Guardamos el dato antes de sacarlo
        cima = cima!!.siguiente  // La nueva cima es el siguiente nodo
        return dato
    }

    // Mira el elemento de la cima sin sacarlo
    fun peek(): T {
        if (estaVacia()) throw NoSuchElementException("Pila vacía")
        return cima!!.dato
    }

    // Nos dice si la pila está vacía
    fun estaVacia(): Boolean = cima == null

    // Vacía la pila
    fun limpiar() {
        cima = null  // Simplemente quitamos la referencia a la cima
    }
}