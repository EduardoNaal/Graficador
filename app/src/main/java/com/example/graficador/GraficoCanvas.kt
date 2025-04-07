package com.example.graficador

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun GraficoCanvas(
    puntos: Array<DoubleArray>,
    esPuntoUnico: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2

        // Calcular máximos absolutos para escalado simétrico
        val maxX = puntos.maxOfOrNull { abs(it[0]) } ?: 1.0
        val maxY = puntos.maxOfOrNull { abs(it[1]) } ?: 1.0
        val maxRange = max(maxX, maxY).coerceAtLeast(1.0)

        // Calcular factor de escalado común
        val scale = min(
            width / (2 * maxRange),
            height / (2 * maxRange)
        ).coerceAtLeast(0.1)

        // Funciones de conversión de coordenadas
        fun Double.toXCoord() = centerX + (this * scale).toFloat()
        fun Double.toYCoord() = centerY - (this * scale).toFloat()

        // Dibujar ejes
        drawLine(
            color = Color.Gray,
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(centerX, 0f),
            end = Offset(centerX, height),
            strokeWidth = 1f
        )

        // Dibujar gráfica
        if (esPuntoUnico && puntos.size == 1) {
            drawCircle(
                color = Color.Red,
                radius = 8f,
                center = Offset(
                    puntos[0][0].toXCoord(),
                    puntos[0][1].toYCoord()
                )
            )
        } else {
            val path = Path().apply {
                moveTo(
                    puntos[0][0].toXCoord(),
                    puntos[0][1].toYCoord()
                )
                for (i in 1 until puntos.size) {
                    lineTo(
                        puntos[i][0].toXCoord(),
                        puntos[i][1].toYCoord()
                    )
                }
            }
            drawPath(
                path = path,
                color = Color.Blue,
                style = Stroke(width = 2f)
            )
        }

        // Borde del área del gráfico
        drawRect(
            color = Color.Gray,
            style = Stroke(width = 1f),
            topLeft = Offset.Zero,
            size = size
        )
    }
}