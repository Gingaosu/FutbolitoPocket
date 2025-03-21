package com.example.futbolitopocket2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.futbolitopocket2.R

// Clase para almacenar los límites de la cancha para el dibujo
data class DrawingBounds(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float,
    val goalWidth: Float,
    val goalHeight: Float,
    val goalLeftX: Float
)

fun calculateDrawingBounds(canvasSize: Size): DrawingBounds {
    val left = canvasSize.width * 0.01f
    val right = canvasSize.width * 0.99f
    val top = canvasSize.height * 0.01f
    val bottom = canvasSize.height * 0.99f
    val goalWidth = (right - left) * 0.13f
    val goalHeight = canvasSize.height * 0.025f
    val goalLeftX = left + ((right - left) - goalWidth) / 2f
    return DrawingBounds(left, right, top, bottom, goalWidth, goalHeight, goalLeftX)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FutbolitoGame()
                }
            }
        }
    }
}

@Composable
fun FutbolitoGame() {
    // Variables para la interfaz de usuario
    var canvasSize = Size.Zero
    val ballRadius = 20f
    // Posición estática de la pelota en el centro del canvas
    var ballX = 0f
    var ballY = 0f
    val scoreTeam1 = 0
    val scoreTeam2 = 0

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.cancha),
            contentDescription = "Cancha de fútbol",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasSize = size
            // Ubicamos la pelota en el centro
            ballX = size.width / 2f
            ballY = size.height / 2f

            val drawingBounds = calculateDrawingBounds(size)

            // Dibujo de las porterías
            drawRect(
                color = Color.Red.copy(alpha = 0.6f),
                topLeft = Offset(drawingBounds.goalLeftX, drawingBounds.top + size.height * 0.06f),
                size = Size(drawingBounds.goalWidth, drawingBounds.goalHeight)
            )

            drawRect(
                color = Color.Blue.copy(alpha = 0.6f),
                topLeft = Offset(drawingBounds.goalLeftX, drawingBounds.bottom - drawingBounds.goalHeight - size.height * 0.06f),
                size = Size(drawingBounds.goalWidth, drawingBounds.goalHeight)
            )

            // Dibujo de la pelota
            drawCircle(
                color = Color.White,
                radius = ballRadius,
                center = Offset(ballX, ballY)
            )
        }

        // Marcadores de goles
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Goles: $scoreTeam1",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Goles: $scoreTeam2",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}
