package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarqueeDirection
import com.example.model.MarqueeFont
import com.example.model.MarqueeSettings
import kotlin.math.roundToInt

@Composable
fun MarqueeRenderer(
  settings: MarqueeSettings,
  modifier: Modifier = Modifier,
  isPreview: Boolean = false,
  onMirrorToggleRequested: (() -> Unit)? = null
) {
  val density = LocalDensity.current
  val textMeasurer = rememberTextMeasurer()

  // Background and text colors
  val bgColor = Color(settings.backgroundColor)
  val textColor = Color(settings.textColor)

  // Blink animation
  val infiniteTransition = rememberInfiniteTransition(label = "MarqueeEffects")
  val blinkAlpha by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (settings.isBlinking) 0.08f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 380, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "BlinkAlpha"
  )

  // Border glow pulsing
  val borderGlowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "BorderGlowAlpha"
  )

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(bgColor)
      .then(
        if (settings.isNeonBorderEnabled) {
          Modifier.border(
            width = if (isPreview) 2.dp else 4.dp,
            brush = Brush.horizontalGradient(
              listOf(
                textColor.copy(alpha = borderGlowAlpha * 0.4f),
                textColor.copy(alpha = borderGlowAlpha),
                textColor.copy(alpha = borderGlowAlpha * 0.4f)
              )
            ),
            shape = RoundedCornerShape(if (isPreview) 12.dp else 0.dp)
          )
        } else {
          Modifier
        }
      )
  ) {
    val containerWidthPx = constraints.maxWidth.toFloat()
    val containerHeightPx = constraints.maxHeight.toFloat()
    val containerHeightSp = with(density) { containerHeightPx.toSp().value }

    // Proportional font sizing:
    // Text occupies the EXACT same percentage of the container height in both preview and fullscreen!
    val sizeFraction = ((settings.textSizeSp - 40f) / (350f - 40f)).coerceIn(0f, 1f)
    val heightRatio = 0.18f + sizeFraction * (0.86f - 0.18f)
    val effectiveFontSizeSp = (containerHeightSp * heightRatio).coerceAtLeast(10f)

    // Neon text shadow/glow
    val shadow = if (settings.isGlowEnabled) {
      Shadow(
        color = textColor.copy(alpha = 0.85f),
        offset = Offset(0f, 0f),
        blurRadius = if (isPreview) 10f else 28f
      )
    } else {
      Shadow.None
    }

    val textStyle = TextStyle(
      fontFamily = settings.font.getFontFamily(),
      fontWeight = settings.font.getFontWeight(),
      fontStyle = settings.font.getFontStyle(),
      fontSize = effectiveFontSizeSp.sp,
      color = textColor,
      shadow = shadow,
      letterSpacing = if (settings.font == MarqueeFont.LED_DOTS) 2.sp else 1.sp
    )

    // Sanitize text: keep strictly single line without any line breaks
    val singleLineText = remember(settings.text) {
      if (settings.text.isBlank()) " "
      else settings.text.replace("\r\n", " ").replace("\n", " ").replace("\r", " ")
    }

    // Calculate text pixel width strictly on a single line
    val measuredResult = remember(singleLineText, textStyle, effectiveFontSizeSp) {
      textMeasurer.measure(
        text = singleLineText,
        style = textStyle,
        maxLines = 1,
        softWrap = false
      )
    }
    val textWidthPx = measuredResult.size.width.toFloat()
    val textHeightPx = measuredResult.size.height.toFloat()

    val isStopped = settings.speedFactor <= 0.05f || settings.direction == MarqueeDirection.STATIC

    // Scroll coordinate tracking
    var currentOffsetPx by remember(
      singleLineText,
      settings.direction,
      containerWidthPx,
      textWidthPx,
      isStopped
    ) {
      val initial = if (isStopped) {
        (containerWidthPx - textWidthPx) / 2f
      } else {
        when (settings.direction) {
          MarqueeDirection.RIGHT_TO_LEFT -> containerWidthPx
          MarqueeDirection.LEFT_TO_RIGHT -> -textWidthPx
          MarqueeDirection.STATIC -> (containerWidthPx - textWidthPx) / 2f
        }
      }
      mutableFloatStateOf(initial)
    }

    // Continuous smooth frame loop
    LaunchedEffect(
      settings.direction,
      settings.speedFactor,
      containerWidthPx,
      textWidthPx,
      singleLineText,
      isStopped
    ) {
      if (isStopped) {
        currentOffsetPx = (containerWidthPx - textWidthPx) / 2f
        return@LaunchedEffect
      }

      // Speed proportional to container width so travel duration matches in both preview and fullscreen
      val speedRatio = (settings.speedFactor * 0.28f) + (settings.speedFactor * settings.speedFactor * 0.040f)
      val speedPxPerSec = containerWidthPx * speedRatio

      var lastFrameTimeNanos = 0L

      while (true) {
        withFrameNanos { frameTimeNanos ->
          if (lastFrameTimeNanos != 0L) {
            val deltaSec = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000f

            when (settings.direction) {
              MarqueeDirection.RIGHT_TO_LEFT -> {
                currentOffsetPx -= speedPxPerSec * deltaSec
                if (currentOffsetPx < -textWidthPx) {
                  currentOffsetPx = containerWidthPx
                }
              }
              MarqueeDirection.LEFT_TO_RIGHT -> {
                currentOffsetPx += speedPxPerSec * deltaSec
                if (currentOffsetPx > containerWidthPx) {
                  currentOffsetPx = -textWidthPx
                }
              }
              MarqueeDirection.STATIC -> {
                currentOffsetPx = (containerWidthPx - textWidthPx) / 2f
              }
            }
          }
          lastFrameTimeNanos = frameTimeNanos
        }
      }
    }

    // Content container with optional horizontal mirror flip
    Box(
      modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
          // Horizontal mirror flip (scaleX = -1f)
          scaleX = if (settings.isMirrored) -1f else 1f
        }
        .alpha(if (settings.isBlinking) blinkAlpha else 1f),
      contentAlignment = Alignment.CenterStart
    ) {
      // Vertical centering: compute y offset so letters are vertically centered
      val verticalY = ((containerHeightPx - textHeightPx) / 2f).roundToInt()

      // Secondary glow layer if glow is enabled
      if (settings.isGlowEnabled) {
        Text(
          text = singleLineText,
          style = textStyle.copy(
            color = textColor.copy(alpha = 0.4f),
            shadow = Shadow(
              color = textColor.copy(alpha = 0.9f),
              offset = Offset(0f, 0f),
              blurRadius = if (isPreview) 20f else 48f
            )
          ),
          maxLines = 1,
          softWrap = false,
          overflow = TextOverflow.Visible,
          modifier = Modifier
            .offset {
              IntOffset(
                x = currentOffsetPx.roundToInt(),
                y = verticalY
              )
            }
        )
      }

      // Primary text layer
      Text(
        text = singleLineText,
        style = textStyle,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Visible,
        modifier = Modifier
          .offset {
            IntOffset(
              x = currentOffsetPx.roundToInt(),
              y = verticalY
            )
          }
      )
    }

    // Authentic physical LED matrix dot grille overlay
    if (settings.isLedGridEnabled) {
      LedDotGridOverlay(
        dotRadius = if (isPreview) 1.2f else 2.2f,
        dotSpacing = if (isPreview) 6f else 9f,
        dotColor = Color.Black.copy(alpha = 0.40f)
      )
    }
  }
}
