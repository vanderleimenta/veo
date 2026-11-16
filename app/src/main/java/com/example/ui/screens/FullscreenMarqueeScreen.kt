package com.example.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.model.MarqueeDirection
import com.example.model.MarqueeSettings
import com.example.ui.components.MarqueeRenderer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FullscreenMarqueeScreen(
  settings: MarqueeSettings,
  onUpdateSettings: (MarqueeSettings) -> Unit,
  onExitFullscreen: () -> Unit
) {
  val context = LocalContext.current
  val activity = context as? Activity
  val scope = rememberCoroutineScope()

  // Handle Android back button to smoothly exit fullscreen
  BackHandler {
    onExitFullscreen()
  }

  // Hide system bars (immersive sticky), cover display cutouts/status bar & keep screen awake
  DisposableEffect(Unit) {
    val window = activity?.window
    if (window != null) {
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        window.attributes.layoutInDisplayCutoutMode =
          WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
      } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        window.attributes.layoutInDisplayCutoutMode =
          WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
      }
      WindowCompat.setDecorFitsSystemWindows(window, false)
      val insetsController = WindowCompat.getInsetsController(window, window.decorView)
      insetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      insetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    onDispose {
      val win = activity?.window
      if (win != null) {
        win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        win.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val insetsController = WindowCompat.getInsetsController(win, win.decorView)
        insetsController.show(WindowInsetsCompat.Type.systemBars())
      }
    }
  }

  // HUD controls visibility & autohide timer
  var isHudVisible by remember { mutableStateOf(true) }
  var feedbackMessage by remember { mutableStateOf<String?>(null) }
  var showGestureHelpDialog by remember { mutableStateOf(false) }

  // Auto-hide feedback message after 2.5s
  LaunchedEffect(feedbackMessage) {
    if (feedbackMessage != null) {
      delay(2200)
      feedbackMessage = null
    }
  }

  // Auto-hide HUD after 4.5 seconds
  LaunchedEffect(isHudVisible) {
    if (isHudVisible) {
      delay(4500)
      isHudVisible = false
    }
  }

  fun triggerFeedback(msg: String) {
    feedbackMessage = msg
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(settings.backgroundColor))
      // Gestures: Pinch to zoom text size, Pan for speed/direction
      .pointerInput(settings) {
        detectTransformGestures { _, pan, zoom, _ ->
          // Handle pinch to zoom text size
          if (zoom != 1f) {
            val newSize = (settings.textSizeSp * zoom).coerceIn(40f, 350f)
            if (kotlin.math.abs(newSize - settings.textSizeSp) > 1.5f) {
              onUpdateSettings(settings.copy(textSizeSp = newSize))
              val sizeMsg = if (newSize >= 335f) "Tamanho: MÁXIMO (Tela Toda 📐)" else "Tamanho: ${newSize.toInt()} sp 🔍"
              triggerFeedback(sizeMsg)
            }
          }

          // Handle horizontal pan to flip scroll direction
          if (kotlin.math.abs(pan.x) > 40f) {
            val newDir = if (pan.x > 0) MarqueeDirection.LEFT_TO_RIGHT else MarqueeDirection.RIGHT_TO_LEFT
            if (newDir != settings.direction) {
              val resumeSpeed = if (settings.speedFactor <= 0.05f) 1.5f else settings.speedFactor
              onUpdateSettings(settings.copy(direction = newDir, speedFactor = resumeSpeed))
              val dirArrow = if (newDir == MarqueeDirection.LEFT_TO_RIGHT) "▶" else "◀"
              triggerFeedback("Direção: $dirArrow")
            }
          }

          // Handle vertical pan to adjust speed (from 0.0x stopped to 10.0x ultra-fast)
          if (kotlin.math.abs(pan.y) > 30f) {
            val speedDelta = if (pan.y < 0) 0.35f else -0.35f
            val newSpeed = (settings.speedFactor + speedDelta).coerceIn(0.0f, 10.0f)
            onUpdateSettings(settings.copy(speedFactor = newSpeed))
            val speedMsg = when {
              newSpeed <= 0.05f -> "Velocidade: 0.0x (Parado ⏸️)"
              newSpeed >= 8.0f -> "Velocidade: ${"%.1f".format(newSpeed)}x 🚀 (Ultra Rápido)"
              else -> "Velocidade: ${"%.1f".format(newSpeed)}x ⚡"
            }
            triggerFeedback(speedMsg)
          }
        }
      }
      // Gestures: Tap for HUD, Double Tap for Mirror, Long press to exit
      .pointerInput(settings) {
        detectTapGestures(
          onTap = {
            isHudVisible = !isHudVisible
          },
          onDoubleTap = {
            val newMirror = !settings.isMirrored
            onUpdateSettings(settings.copy(isMirrored = newMirror))
            val mirrorStatus = if (newMirror) "ATIVADO (Espelhado 🪞)" else "DESATIVADO (Normal)"
            triggerFeedback("Modo Espelho: $mirrorStatus")
          },
          onLongPress = {
            onExitFullscreen()
          }
        )
      }
  ) {
    // High-performance Marquee display
    MarqueeRenderer(
      settings = settings,
      modifier = Modifier.fillMaxSize()
    )

    // Temporary floating gesture feedback pill
    AnimatedVisibility(
      visible = feedbackMessage != null,
      enter = fadeIn() + slideInVertically { it / 2 },
      exit = fadeOut() + slideOutVertically { it / 2 },
      modifier = Modifier
        .align(Alignment.Center)
        .padding(16.dp)
    ) {
      Surface(
        color = Color(0xDD111827),
        contentColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
          Text(
            text = feedbackMessage ?: "",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFFFFEB3B)
            )
          )
        }
      }
    }

    // Floating HUD Overlay
    AnimatedVisibility(
      visible = isHudVisible,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.fillMaxSize()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Top Action Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Exit button
          FilledIconButton(
            onClick = { onExitFullscreen() },
            colors = IconButtonDefaults.filledIconButtonColors(
              containerColor = Color(0xCC000000),
              contentColor = Color.White
            ),
            modifier = Modifier.testTag("exit_fullscreen_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Fechar tela cheia e voltar para edição"
            )
          }

          // Center status tags
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (settings.isMirrored) {
              Surface(
                color = Color(0xDD333333),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
              ) {
                Text(
                  text = "🪞 ESPELHADO",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }
            Surface(
              color = Color(0xCC000000),
              contentColor = Color.White,
              shape = RoundedCornerShape(12.dp)
            ) {
              val topSpeedText = if (settings.speedFactor <= 0.05f || settings.direction == MarqueeDirection.STATIC) "⏸ PARADO" else "${"%.1f".format(settings.speedFactor)}x"
              Text(
                text = topSpeedText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          // Top right controls (Mirror Toggle & Help)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Mirror quick toggle
            FilledIconButton(
              onClick = {
                val newMirror = !settings.isMirrored
                onUpdateSettings(settings.copy(isMirrored = newMirror))
                val mirrorStatus = if (newMirror) "ATIVADO" else "DESATIVADO"
                triggerFeedback("Modo Espelho: $mirrorStatus")
              },
              colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (settings.isMirrored) Color(0xFF444444) else Color(0xCC000000),
                contentColor = Color.White
              ),
              modifier = Modifier.testTag("mirror_toggle_button")
            ) {
              Icon(
                imageVector = Icons.Default.Flip,
                contentDescription = "Alternar Modo Espelho"
              )
            }

            // Gesture instructions
            FilledIconButton(
              onClick = { showGestureHelpDialog = true },
              colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color(0xCC000000),
                contentColor = Color.White
              ),
              modifier = Modifier.testTag("help_gesture_button")
            ) {
              Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = "Ajuda de Gestos"
              )
            }
          }
        }

        // Bottom Quick Controls Bar
        Card(
          colors = CardDefaults.cardColors(
            containerColor = Color(0xEE1E1E1E),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(20.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Edit settings button
            IconButton(
              onClick = { onExitFullscreen() },
              modifier = Modifier.testTag("bottom_edit_button")
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar texto e cores",
                tint = Color.White
              )
            }

            // Direction: Right to Left
            IconButton(
              onClick = {
                onUpdateSettings(settings.copy(direction = MarqueeDirection.RIGHT_TO_LEFT))
                triggerFeedback("Direção: ◀ (Direita p/ Esquerda)")
              },
              modifier = Modifier.testTag("dir_left_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Rolar para esquerda",
                tint = if (settings.direction == MarqueeDirection.RIGHT_TO_LEFT) Color.White else Color(0xFF777777)
              )
            }

            // Pause / Play
            IconButton(
              onClick = {
                val newDir = if (settings.direction == MarqueeDirection.STATIC) {
                  MarqueeDirection.RIGHT_TO_LEFT
                } else {
                  MarqueeDirection.STATIC
                }
                onUpdateSettings(settings.copy(direction = newDir))
                val stateText = if (newDir == MarqueeDirection.STATIC) "Pausado ⏸" else "Reproduzindo ▶"
                triggerFeedback(stateText)
              },
              modifier = Modifier.testTag("pause_play_button")
            ) {
              Icon(
                imageVector = if (settings.direction == MarqueeDirection.STATIC) Icons.Default.PlayArrow else Icons.Default.Pause,
                contentDescription = "Pausar ou reproduzir letreiro",
                tint = if (settings.direction == MarqueeDirection.STATIC) Color.White else Color(0xFFAAAAAA)
              )
            }

            // Direction: Left to Right
            IconButton(
              onClick = {
                onUpdateSettings(settings.copy(direction = MarqueeDirection.LEFT_TO_RIGHT))
                triggerFeedback("Direção: ▶ (Esquerda p/ Direita)")
              },
              modifier = Modifier.testTag("dir_right_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Rolar para direita",
                tint = if (settings.direction == MarqueeDirection.LEFT_TO_RIGHT) Color.White else Color(0xFF777777)
              )
            }

            // Slower speed (down to 0.0x Parado)
            IconButton(
              onClick = {
                val newSpeed = (settings.speedFactor - 0.5f).coerceIn(0.0f, 10.0f)
                onUpdateSettings(settings.copy(speedFactor = newSpeed))
                val msg = if (newSpeed <= 0.05f) "Velocidade: 0.0x (Parado ⏸️)" else "Velocidade: ${"%.1f".format(newSpeed)}x 🐢"
                triggerFeedback(msg)
              },
              modifier = Modifier.testTag("speed_decrease_button")
            ) {
              Text(
                text = "-V",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
              )
            }

            // Faster speed (up to 10.0x Ultra Rápido)
            IconButton(
              onClick = {
                val newSpeed = (settings.speedFactor + 0.5f).coerceIn(0.0f, 10.0f)
                onUpdateSettings(settings.copy(speedFactor = newSpeed))
                val msg = if (newSpeed >= 8.0f) "Velocidade: ${"%.1f".format(newSpeed)}x 🚀 (Ultra Rápido)" else "Velocidade: ${"%.1f".format(newSpeed)}x ⚡"
                triggerFeedback(msg)
              },
              modifier = Modifier.testTag("speed_increase_button")
            ) {
              Text(
                text = "+V",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
              )
            }

            // Blink toggle
            IconButton(
              onClick = {
                val newBlink = !settings.isBlinking
                onUpdateSettings(settings.copy(isBlinking = newBlink))
                triggerFeedback(if (newBlink) "Modo Piscar: ATIVADO ⚡" else "Modo Piscar: DESATIVADO")
              },
              modifier = Modifier.testTag("blink_toggle_button")
            ) {
              Icon(
                imageVector = if (settings.isBlinking) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Alternar modo piscar",
                tint = if (settings.isBlinking) Color.White else Color(0xFF777777)
              )
            }
          }
        }
      }
    }
  }

  // Gesture Help Dialog
  if (showGestureHelpDialog) {
    androidx.compose.material3.AlertDialog(
      onDismissRequest = { showGestureHelpDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = null,
            tint = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Controle por Gestos", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          GestureGuideItem(gesture = "👆 1 Toque na tela", desc = "Mostra ou oculta a barra de botões flutuantes.")
          GestureGuideItem(gesture = "✌️ 2 Toques rápidos", desc = "Ativa ou desativa o Modo Espelho (inverte horizontalmente).")
          GestureGuideItem(gesture = "↔️ Deslizar horizontal", desc = "Muda o sentido do letreiro (esquerda / direita).")
          GestureGuideItem(gesture = "↕️ Deslizar vertical", desc = "Ajusta a velocidade de rolagem.")
          GestureGuideItem(gesture = "🤏 Pinçar (2 dedos)", desc = "Aumenta ou diminui o tamanho das letras (Zoom).")
          GestureGuideItem(gesture = "⏱️ Toque longo / Voltar", desc = "Retorna para a tela de configurações.")
        }
      },
      confirmButton = {
        androidx.compose.material3.Button(
          onClick = { showGestureHelpDialog = false },
          modifier = Modifier.testTag("dismiss_gesture_dialog_button"),
          colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
          )
        ) {
          Text("Entendi")
        }
      }
    )
  }
}

@Composable
private fun GestureGuideItem(gesture: String, desc: String) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = gesture,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF00F0FF),
      modifier = Modifier.width(130.dp),
      fontSize = 13.sp
    )
    Text(
      text = desc,
      fontSize = 13.sp,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
