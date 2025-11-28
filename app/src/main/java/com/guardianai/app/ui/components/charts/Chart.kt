package com.guardianai.app.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guardianai.app.ui.theme.GuardianAITypography
import com.guardianai.app.ui.theme.chartColorByMetric
import kotlin.math.max
import kotlin.math.min

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<ChartDataPoint>,
    color: Color,
    title: String = "",
    showGrid: Boolean = true,
    height: Int = 200
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp),
        colors = CardDefaults.cardColors(
            containerColor = com.guardianai.app.ui.theme.GlassmorphismColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = GuardianAITypography.BodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = com.guardianai.app.ui.theme.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (title.isNotEmpty()) 40.dp else 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                drawLineChart(data, color, showGrid)
            }
        }
    }
}

@Composable
fun RiskGauge(
    modifier: Modifier = Modifier,
    riskLevel: String,
    value: Float = 0f, // 0f to 1f
    size: Int = 200
) {
    val color = riskLevelColor(riskLevel)

    Card(
        modifier = modifier
            .size(size.dp),
        colors = CardDefaults.cardColors(
            containerColor = com.guardianai.app.ui.theme.GlassmorphismColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size((size * 0.8f).dp)
            ) {
                drawRiskGauge(value, color)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = riskLevel,
                    style = GuardianAITypography.MetricValue,
                    color = color
                )
                Text(
                    text = "Risk Level",
                    style = GuardianAITypography.MetricLabel,
                    color = com.guardianai.app.ui.theme.WhiteSecondary
                )
            }
        }
    }
}

@Composable
fun SimpleMetricChart(
    modifier: Modifier = Modifier,
    metricType: String,
    data: List<Float>,
    height: Int = 150
) {
    val color = chartColorByMetric(metricType)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp),
        colors = CardDefaults.cardColors(
            containerColor = com.guardianai.app.ui.theme.GlassmorphismColors.Surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                drawSimpleBarChart(data, color)
            }
        }
    }
}

data class ChartDataPoint(
    val x: Float,
    val y: Float,
    val label: String = ""
)

private fun DrawScope.drawLineChart(
    data: List<ChartDataPoint>,
    color: Color,
    showGrid: Boolean
) {
    if (data.size < 2) return

    val width = size.width
    val height = size.height
    val padding = 20f

    // Calculate min/max for normalization
    val minX = data.minOf { it.x }
    val maxX = data.maxOf { it.x }
    val minY = data.minOf { it.y }
    val maxY = data.maxOf { it.y }

    val xRange = maxX - minX
    val yRange = maxY - minY

    // Draw grid if enabled
    if (showGrid) {
        drawGrid(width, height, padding)
    }

    // Normalize data points
    val normalizedPoints = data.map { point ->
        Offset(
            x = padding + ((point.x - minX) / xRange) * (width - 2 * padding),
            y = height - padding - ((point.y - minY) / yRange) * (height - 2 * padding)
        )
    }

    // Draw the line chart
    val path = Path().apply {
        moveTo(normalizedPoints.first().x, normalizedPoints.first().y)
        normalizedPoints.drop(1).forEach { point ->
            lineTo(point.x, point.y)
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 3f)
    )

    // Draw data points
    normalizedPoints.forEach { point ->
        drawCircle(
            center = point,
            radius = 4f,
            color = color
        )
    }
}

private fun DrawScope.drawGrid(width: Float, height: Float, padding: Float) {
    val gridColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.1f)

    // Vertical lines
    for (i in 0..4) {
        val x = padding + (i * (width - 2 * padding) / 4)
        drawLine(
            start = Offset(x, padding),
            end = Offset(x, height - padding),
            color = gridColor,
            strokeWidth = 1f
        )
    }

    // Horizontal lines
    for (i in 0..4) {
        val y = padding + (i * (height - 2 * padding) / 4)
        drawLine(
            start = Offset(padding, y),
            end = Offset(width - padding, y),
            color = gridColor,
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawRiskGauge(value: Float, color: Color) {
    val centerX = size.width / 2
    val centerY = size.height * 0.7f
    val radius = min(size.width, size.height) * 0.3f

    // Draw background arc
    drawArc(
        color = com.guardianai.app.ui.theme.White.copy(alpha = 0.1f),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        size = Size(radius * 2, radius * 2),
        topLeft = Offset(centerX - radius, centerY - radius),
        style = Stroke(width = 8f)
    )

    // Draw value arc
    val sweepAngle = 180f * value.coerceIn(0f, 1f)
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = sweepAngle,
        useCenter = false,
        size = Size(radius * 2, radius * 2),
        topLeft = Offset(centerX - radius, centerY - radius),
        style = Stroke(width = 8f)
    )

    // Draw tick marks
    for (i in 0..4) {
        val angle = 180f + (i * 45f)
        val radians = Math.toRadians(angle.toDouble())
        val x1 = centerX + radius * 0.8f * kotlin.math.cos(radians).toFloat()
        val y1 = centerY + radius * 0.8f * kotlin.math.sin(radians).toFloat()
        val x2 = centerX + radius * kotlin.math.cos(radians).toFloat()
        val y2 = centerY + radius * kotlin.math.sin(radians).toFloat()

        drawLine(
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            color = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawSimpleBarChart(
    data: List<Float>,
    color: Color
) {
    if (data.isEmpty()) return

    val width = size.width
    val height = size.height
    val maxValue = data.maxOrNull() ?: 1f
    val barWidth = width / (data.size * 2f)

    data.forEachIndexed { index, value ->
        val barHeight = (value / maxValue) * height
        val x = index * barWidth * 2f + barWidth / 2f
        val y = height - barHeight

        drawRect(
            color = color,
            topLeft = Offset(x, y),
            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
        )
    }
}

private fun riskLevelColor(riskLevel: String): Color {
    return when (riskLevel.lowercase()) {
        "low" -> com.guardianai.app.ui.theme.RiskLow
        "moderate" -> com.guardianai.app.ui.theme.RiskModerate
        "elevated" -> com.guardianai.app.ui.theme.RiskElevated
        "high" -> com.guardianai.app.ui.theme.RiskHigh
        "critical" -> com.guardianai.app.ui.theme.RiskCritical
        else -> com.guardianai.app.ui.theme.RiskLow
    }
}