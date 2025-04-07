package com.example.graficador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.graficador.ui.theme.GraficadorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraficadorTheme {
                InterfazGraficadora()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterfazGraficadora() {
    var expresion by remember { mutableStateOf("x") }
    var modoEvaluacion by remember { mutableStateOf("rango") }
    var valorXPunto by remember { mutableStateOf("0") }
    var inicioRango by remember { mutableStateOf("-5") }
    var finRango by remember { mutableStateOf("5") }
    var numPuntos by remember { mutableStateOf("100") }
    var mostrarAyuda by remember { mutableStateOf(false) }
    var puntos by remember { mutableStateOf<Array<DoubleArray>?>(null) }
    var error by remember { mutableStateOf("") }

    val graficador = remember { Graficador() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Graficador 2D",
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = { mostrarAyuda = true }) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Ayuda")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de modo
        Text("Tipo de evaluación:", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = modoEvaluacion == "punto",
                onClick = { modoEvaluacion = "punto" }
            )
            Text("Un punto", modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = modoEvaluacion == "rango",
                onClick = { modoEvaluacion = "rango" }
            )
            Text("Rango")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Entrada de expresión
        OutlinedTextField(
            value = expresion,
            onValueChange = { expresion = it },
            label = { Text("Expresión (ej: x^2 + 3*x - 5)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Usa 'x' como variable") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campos dinámicos
        if (modoEvaluacion == "punto") {
            OutlinedTextField(
                value = valorXPunto,
                onValueChange = { valorXPunto = it },
                label = { Text("Valor de x") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        } else {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = inicioRango,
                        onValueChange = { inicioRango = it },
                        label = { Text("Inicio") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = finRango,
                        onValueChange = { finRango = it },
                        label = { Text("Fin") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = numPuntos,
                    onValueChange = { numPuntos = it },
                    label = { Text("Número de puntos") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de acción
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    expresion = "x"
                    valorXPunto = "0"
                    inicioRango = "-5"
                    finRango = "5"
                    numPuntos = "100"
                    puntos = null
                    error = ""
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) { Text("Limpiar") }

            Button(
                onClick = {
                    try {
                        val postfija = graficador.convertirInfijaAPostfija(expresion)
                        puntos = if (modoEvaluacion == "punto") {
                            val x = valorXPunto.toDouble()
                            arrayOf(doubleArrayOf(x, graficador.calcularPunto(x, postfija)))
                        } else {
                            graficador.generarPuntos(
                                inicioRango.toDouble(),
                                finRango.toDouble(),
                                numPuntos.toInt(),
                                postfija
                            )
                        }
                        error = ""
                    } catch (e: Exception) {
                        error = "Error: ${e.message}"
                        puntos = null
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Graficar") }
        }

        // Mostrar errores
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp))
        }

        // Área del gráfico
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 24.dp)
                .border(1.dp, Color.Gray)
        ) {
            if (puntos != null && puntos!!.isNotEmpty()) {
                GraficoCanvas(
                    puntos = puntos!!,
                    esPuntoUnico = modoEvaluacion == "punto"
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (error.isEmpty()) "Ingrese una función" else " ",
                        color = Color(0xFF1976D2))
                }
            }
        }
    }

    // Diálogo de ayuda
    if (mostrarAyuda) {
        Dialog(onDismissRequest = { mostrarAyuda = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Instrucciones:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Usa operadores: + - * / ^")
                    Text("2. Escribe 'x' como variable")
                    Text("3. El origen (0,0) está al centro")
                    Text("4. Valores decimales con punto (ej: 2.5)")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { mostrarAyuda = false },
                        modifier = Modifier.align(Alignment.End)
                    ) { Text("Entendido") }
                }
            }
        }
    }
}