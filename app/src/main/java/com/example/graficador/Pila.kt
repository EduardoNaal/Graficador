package com.example.graficador

//Clase genérica que implementa una pila (estructura LIFO: Last In, First Out).

class Pila<T> {
    private var tope: Nodo<T>? = null  // Referencia al nodo en la cima de la pila
    private var tamaño: Int = 0  // Cantidad de elementos en la pila

    // Clase interna para representar un nodo de la pila

    private class Nodo<T>(val dato: T, var siguiente: Nodo<T>?)

    //Verifica si la pila está vacía.

    fun estaVacia(): Boolean = tope == null

    //Retorna el tamaño actual de la pila.

    fun tamaño(): Int = tamaño

    //Agrega un nuevo elemento a la pila.

    fun push(elemento: T) {
        val nuevoNodo = Nodo(elemento, tope)
        tope = nuevoNodo
        tamaño++
    }

    //Elimina y devuelve el elemento en la cima de la pila.

    fun pop(): T {
        if (estaVacia()) throw IllegalStateException("La pila está vacía")
        val dato = tope!!.dato
        tope = tope!!.siguiente
        tamaño--
        return dato
    }

    //Devuelve el elemento en la cima de la pila sin eliminarlo.

    fun peek(): T {
        if (estaVacia()) throw IllegalStateException("La pila está vacía")
        return tope!!.dato
    }

    //Elimina todos los elementos de la pila.

    fun limpiar() {
        tope = null
        tamaño = 0
    }

    //Convierte la pila en una lista para facilitar su visualización.

    fun toList(): List<T> {
        val lista = mutableListOf<T>()
        var actual = tope
        while (actual != null) {
            lista.add(actual.dato)
            actual = actual.siguiente
        }
        return lista
    }
}