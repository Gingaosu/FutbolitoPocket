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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.ricknout.composesensors.accelerometer.rememberAccelerometerSensorValueAsState
import kotlinx.coroutines.delay

// Clases para almacenar los límites de la cancha
data class PhysicsBounds(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float,
    val goalWidth: Float,
    val goalLeftX: Float
)

data class DrawingBounds(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float,
    val goalWidth: Float,
    val goalHeight: Float,
    val goalLeftX: Float
)

fun calculatePhysicsBounds(canvasSize: Size): PhysicsBounds {
    val left = canvasSize.width * 0.02f
    val right = canvasSize.width * 0.98f
    val top = canvasSize.height * 0.1f
    val bottom = canvasSize.height * 0.9f
    val goalWidth = (right - left) * 0.1f
    val goalLeftX = left + ((right - left) - goalWidth) / 2f
    return PhysicsBounds(left, right, top, bottom, goalWidth, goalLeftX)
}

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
    // Lectura del acelerómetro
    val accelerometer by rememberAccelerometerSensorValueAsState()

    // Parámetros del juego
    val ballRadius = 20f
    val sensitivity = 0.2f
    val friction = 0.98f
    val rebote = 0.8f

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var ballX by remember { mutableStateOf(0f) }
    var ballY by remember { mutableStateOf(0f) }
    var velocityX by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }
    var scoreTeam1 by remember { mutableStateOf(0) }
    var scoreTeam2 by remember { mutableStateOf(0) }
    var isInitialized by remember { mutableStateOf(false) }

    fun resetBall() {
        ballX = canvasSize.width / 2f
        ballY = canvasSize.height / 2f
        velocityX = 0f
        velocityY = 0f
    }

    // Loop de física que actualiza el estado del juego
    LaunchedEffect(canvasSize, accelerometer.value) {
        while (true) {
            if (canvasSize.width > 0 && !isInitialized) {
                ballX = canvasSize.width / 2f
                ballY = canvasSize.height / 2f
                isInitialized = true
            }
            val (accX, accY, _) = accelerometer.value
            velocityX += -accX * sensitivity
            velocityY += accY * sensitivity

            velocityX *= friction
            velocityY *= friction
            ballX += velocityX
            ballY += velocityY

            // Obtenemos los límites para la física
            val physicsBounds = calculatePhysicsBounds(canvasSize)

            // Colisiones laterales
            if (ballX - ballRadius < physicsBounds.left) {
                ballX = physicsBounds.left + ballRadius
                velocityX = -velocityX * rebote
            } else if (ballX + ballRadius > physicsBounds.right) {
                ballX = physicsBounds.right - ballRadius
                velocityX = -velocityX * rebote
            }

            // Colisiones con techo y suelo (y detección de goles)
            if (ballY - ballRadius < physicsBounds.top) {
                if (ballX in physicsBounds.goalLeftX..(physicsBounds.goalLeftX + physicsBounds.goalWidth)) {
                    scoreTeam2++
                    resetBall()
                } else {
                    ballY = physicsBounds.top + ballRadius
                    velocityY = -velocityY * rebote
                }
            } else if (ballY + ballRadius > physicsBounds.bottom) {
                if (ballX in physicsBounds.goalLeftX..(physicsBounds.goalLeftX + physicsBounds.goalWidth)) {
                    scoreTeam1++
                    resetBall()
                } else {
                    ballY = physicsBounds.bottom - ballRadius
                    velocityY = -velocityY * rebote
                }
            }

            delay(16)
        }
    }

    // Interfaz de usuario
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.cancha),
            contentDescription = "Cancha de fútbol",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Actualizamos el tamaño del canvas
            canvasSize = size
            // Obtenemos los límites para el dibujo
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