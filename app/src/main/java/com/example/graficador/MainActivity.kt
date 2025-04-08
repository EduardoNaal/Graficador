package com.example.graficador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
    // Estados para guardar los valores de la interfaz
    var expresion by remember { mutableStateOf("x^2 - 3*x + 2") }  // Expresión a graficar
    var modoEvaluacion by remember { mutableStateOf("rango") }      // Modo: punto o rango
    var valorXPunto by remember { mutableStateOf("0") }             // Valor x para modo punto
    var inicioRango by remember { mutableStateOf("0") }             // Inicio del rango
    var finRango by remember { mutableStateOf("1") }                // Fin del rango
    var numPuntos by remember { mutableStateOf("10") }              // Cantidad de puntos
    var mostrarAyuda by remember { mutableStateOf(false) }          // Mostrar ayuda?
    var puntos by remember { mutableStateOf<Array<DoubleArray>?>(null) }  // Puntos calculados
    var error by remember { mutableStateOf("") }                    // Mensaje de error

    val graficador = remember { Graficador() }  // Instancia del graficador

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())  // Permite scroll si hace falta
    ) {
        // Título y botón de ayuda
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Graficador", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { mostrarAyuda = true }) {
                Icon(Icons.Default.Info, contentDescription = "Ayuda")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Selección de modo: un punto o rango
        Text("Tipo de evaluación:", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = modoEvaluacion == "punto",
                onClick = { modoEvaluacion = "punto" }
            )
            Text("Un punto", Modifier.padding(end = 16.dp))
            RadioButton(
                selected = modoEvaluacion == "rango",
                onClick = { modoEvaluacion = "rango" }
            )
            Text("Rango")
        }

        Spacer(Modifier.height(16.dp))

        // Campo para la expresión matemática
        OutlinedTextField(
            value = expresion,
            onValueChange = { expresion = it },
            label = { Text("Expresión (ej: x^2 + 3*x + 2)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Usa 'x' como variable") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
        )

        Spacer(Modifier.height(16.dp))

        // Si modo es punto, pido valor de x
        if (modoEvaluacion == "punto") {
            OutlinedTextField(
                value = valorXPunto,
                onValueChange = { valorXPunto = it },
                label = { Text("Valor de x") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        } else {
            // Si modo es rango, pido inicio, fin y número de puntos
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
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = numPuntos,
                    onValueChange = { numPuntos = it },
                    label = { Text("Número de puntos") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botones Limpiar y Graficar
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    // Resetea todo a valores por defecto
                    expresion = "x"
                    valorXPunto = "0"
                    inicioRango = "0"
                    finRango = "1"
                    numPuntos = "10"
                    puntos = null
                    error = ""
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Limpiar")
            }

            Button(
                onClick = {
                    try {
                        // Paso 1: preprocesar la expresión
                        val expresionProcesada = graficador.preprocesarExpresion(expresion)
                        // Paso 2: convertir a postfija
                        val postfija = graficador.convertirInfijaAPostfija(expresionProcesada)
                        // Paso 3: generar puntos
                        puntos = if (modoEvaluacion == "punto") {
                            arrayOf(doubleArrayOf(
                                valorXPunto.toDouble(),
                                graficador.calcularPunto(valorXPunto.toDouble(), postfija)
                            ))
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
            ) {
                Text("Graficar")
            }
        }

        // Si hay error, lo muestro en rojo
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Área de dibujo o mensaje inicial
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 8.dp)
                .border(1.dp, Color.Gray)
        ) {
            if (puntos != null && puntos!!.isNotEmpty()) {
                GraficoCanvas(puntos = puntos!!, esPuntoUnico = modoEvaluacion == "punto")
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (error.isEmpty()) "Ingrese una función" else "",
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }

    // Diálogo de ayuda
    if (mostrarAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarAyuda = false },
            title = { Text("Instrucciones") },
            text = {
                Column {
                    Text("• Operadores: + - * / ^")
                    Text("• Usa 'x' como variable")
                    Text("• Ejemplo válido: x^2 + 3*x - 5")
                }
            },
            confirmButton = {
                Button(onClick = { mostrarAyuda = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
fun GraficadorTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}