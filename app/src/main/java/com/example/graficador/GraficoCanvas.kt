package com.example.graficador

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.*

// Composable que dibuja puntos y curvas en un canvas
@Composable
fun GraficoCanvas(
    puntos: Array<DoubleArray>,
    esPuntoUnico: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val ancho = size.width
        val alto = size.height
        val centroX = ancho / 2f
        val centroY = alto / 2f

        val valoresX = puntos.map { it[0] }
        val valoresY = puntos.map { it[1] }
        val (minX, maxX) = calcularMinMax(valoresX)
        val (minY, maxY) = calcularMinMax(valoresY)
        val rangoX = max(abs(minX), abs(maxX)).coerceAtLeast(1.0)
        val rangoY = max(abs(minY), abs(maxY)).coerceAtLeast(1.0)
        val rangoMaximo = max(rangoX, rangoY)
        val escala = (min(ancho, alto) / 2f / rangoMaximo.toFloat())

        // Convierte coordenada x de valor a posición en pantalla
        fun convertX(x: Double): Float = centroX + (x * escala).toFloat()
        // Convierte coordenada y de valor a posición en pantalla
        fun convertY(y: Double): Float = centroY - (y * escala).toFloat()

        // Dibuja cuadrícula
        dibujarCuadricula(rangoMaximo, escala, centroX, centroY, ancho, alto)
        // Dibuja ejes
        dibujarEjes(centroX, centroY, ancho, alto)

        // Dibuja cada punto
        puntos.forEach { punto ->
            drawCircle(
                color = if (esPuntoUnico) Color.Red else Color.Blue,
                radius = if (esPuntoUnico) 10f else 5f,
                center = Offset(convertX(punto[0]), convertY(punto[1]))
            )
        }

        // Si es rango, conecta puntos con línea
        if (!esPuntoUnico && puntos.size > 1) {
            val ruta = Path().apply {
                moveTo(convertX(puntos.first()[0]), convertY(puntos.first()[1]))
                puntos.drop(1).forEach { punto ->
                    lineTo(convertX(punto[0]), convertY(punto[1]))
                }
            }
            drawPath(ruta, Color.Blue.copy(alpha = 0.5f), style = Stroke(3f))
        }
    }
}

// Calcula min y max con margen
private fun calcularMinMax(valores: List<Double>): Pair<Double, Double> {
    if (valores.isEmpty()) return Pair(-1.0, 1.0)
    val min = valores.minOrNull()!!
    val max = valores.maxOrNull()!!
    val margen = max((max - min) * 0.1, 1.0)
    return Pair(min - margen, max + margen)
}

// Dibuja la cuadrícula con líneas espaciadas
private fun DrawScope.dibujarCuadricula(
    rangoMaximo: Double,
    escala: Float,
    centroX: Float,
    centroY: Float,
    ancho: Float,
    alto: Float
) {
    val paso = calcularPasoCuadricula(rangoMaximo)
    val colorCuadricula = Color.LightGray.copy(alpha = 0.3f)

    var x = -rangoMaximo
    while (x <= rangoMaximo) {
        if (abs(x) > 1e-6) {
            val px = centroX + (x * escala).toFloat()
            drawLine(colorCuadricula, Offset(px, 0f), Offset(px, alto), 1f)
        }
        x += paso
    }

    var y = -rangoMaximo
    while (y <= rangoMaximo) {
        if (abs(y) > 1e-6) {
            val py = centroY - (y * escala).toFloat()
            drawLine(colorCuadricula, Offset(0f, py), Offset(ancho, py), 1f)
        }
        y += paso
    }
}

// Calcula el paso de la cuadrícula según el rango
private fun calcularPasoCuadricula(rangoMaximo: Double): Double {
    val log = floor(log10(rangoMaximo))
    val pasoBase = 10.0.pow(log)
    return when {
        rangoMaximo / pasoBase >= 5 -> pasoBase * 5
        rangoMaximo / pasoBase >= 2 -> pasoBase * 2
        else -> pasoBase
    }
}

// Dibuja los ejes X e Y
private fun DrawScope.dibujarEjes(
    centroX: Float,
    centroY: Float,
    ancho: Float,
    alto: Float
) {
    drawLine(Color.Black, Offset(0f, centroY), Offset(ancho, centroY), 2f)
    drawLine(Color.Black, Offset(centroX, 0f), Offset(centroX, alto), 2f)
}