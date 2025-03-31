package com.example.graficador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.graficador.ui.theme.GraficadorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraficadorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GraficadorScreen()
                }
            }
        }
    }
}

// Pantalla principal que permite ingresar una expresión infija y convertirla a postfija, evaluarla y graficarla.

@Composable
fun GraficadorScreen() {
    val graficador = remember { Graficador() }
    var expression by remember { mutableStateOf("") }
    var postfija by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }
    var puntos by remember { mutableStateOf<Array<DoubleArray>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de entrada para la expresión matemática

        OutlinedTextField(
            value = expression,
            onValueChange = { expression = it },
            label = { Text("Expresión (ej: 3*x+2)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Botón para calcular y graficar la expresión

        Button(
            onClick = {
                try {
                    postfija = graficador.infijaAPostfija(expression)
                    puntos = graficador.evaluarRango(-10.0, 10.0, 100, postfija)
                    resultado = "Postfija: $postfija\n" +
                            "Ejemplo (x=2): ${"%.2f".format(graficador.evaluarPunto(2.0, postfija))}"
                    error = null
                } catch (e: Exception) {
                    error = "Error: ${e.message}"
                    resultado = ""
                    puntos = null
                }
            },
            enabled = expression.isNotEmpty(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Calcular y Graficar")
        }

        // Mostrar resultados de la conversión y evaluación

        if (resultado.isNotEmpty()) {
            Text(
                text = resultado,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Mostrar mensajes de error

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Mostrar el gráfico de la función

        if (puntos != null) {
            GraficoBasico(
                puntos = puntos!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}

/**
 * Componente para dibujar la gráfica de la función.
 * @param puntos Lista de puntos (x, y) generados a partir de la función matemática.
 * @param modifier Modificadores para ajustar la apariencia del gráfico.
 */

@Composable
fun GraficoBasico(
    puntos: Array<DoubleArray>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val scaleX = size.width / 20f  // Escala para el eje X (rango -10 a 10)
        val scaleY = size.height / 20f // Escala para el eje Y

        // Dibujar los ejes coordenados
        drawLine(
            color = Color.Black,
            start = Offset(0f, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = 2f
        )

        drawLine(
            color = Color.Black,
            start = Offset(centerX, 0f),
            end = Offset(centerX, size.height),
            strokeWidth = 2f
        )

        // Dibujar la función en el gráfico
        for (i in 0 until puntos.size - 1) {
            try {
                val x1 = ((puntos[i][0] + 10) * scaleX).toFloat()
                val y1 = (centerY - (puntos[i][1] * scaleY)).toFloat()
                val x2 = ((puntos[i+1][0] + 10) * scaleX).toFloat()
                val y2 = (centerY - (puntos[i+1][1] * scaleY)).toFloat()

                if (y1.isFinite() && y2.isFinite()) {
                    drawLine(
                        color = Color.Blue,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 2f
                    )
                }
            } catch (e: Exception) {
                // Ignorar puntos inválidos para evitar fallos
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GraficadorPreview() {
    GraficadorTheme {
        GraficadorScreen()
    }
}
