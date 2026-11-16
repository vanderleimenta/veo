package com.example.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.R

val PressStartFamily = FontFamily(Font(R.font.press_start_2p))
val OrbitronFamily = FontFamily(Font(R.font.orbitron))
val BebasNeueFamily = FontFamily(Font(R.font.bebas_neue))
val MonotonFamily = FontFamily(Font(R.font.monoton))
val AntonFamily = FontFamily(Font(R.font.anton))
val PacificoFamily = FontFamily(Font(R.font.pacifico))
val VT323Family = FontFamily(Font(R.font.vt323))
val RussoOneFamily = FontFamily(Font(R.font.russo_one))

enum class MarqueeDirection(val label: String, val iconName: String) {
  RIGHT_TO_LEFT("Direita para Esquerda (◀)", "arrow_back"),
  LEFT_TO_RIGHT("Esquerda para Direita (▶)", "arrow_forward"),
  STATIC("Estático / Pausado (⏸)", "pause")
}

enum class MarqueeFont(val displayName: String, val sampleText: String) {
  PRESS_START("Arcade 8-Bit", "RETRO ARCADE"),
  ORBITRON("Digital Sci-Fi", "CYBER MATRIX"),
  BEBAS_NEUE("Bebas Poster", "ALTO IMPACTO"),
  MONOTON("Monoton Neon", "NEON RETRO"),
  ANTON("Anton Black", "LETRA PESADA"),
  PACIFICO("Pacifico Script", "Letras Cursivas"),
  VT323("Terminal CRT", "DOS TERMINAL 1980"),
  RUSSO_ONE("Russo Moderno", "BLOCO INDUSTRIAL"),
  LED_DOTS("Matriz de LED", "PONTOS DIGITAIS"),
  SANS_BOLD("Moderno Sans", "LIMPO E DIRETO"),
  SERIF("Serifa Clássica", "ESTILO ELEGANTE"),
  MONOSPACE("Terminal Mono", "LARGURA FIXA"),
  CONDENSED("Compacto", "ALTO E ESTREITO"),
  CURSIVE("Manuscrito", "CURSIVO FLUIDO");

  fun getFontFamily(): FontFamily {
    return when (this) {
      PRESS_START -> PressStartFamily
      ORBITRON -> OrbitronFamily
      BEBAS_NEUE -> BebasNeueFamily
      MONOTON -> MonotonFamily
      ANTON -> AntonFamily
      PACIFICO -> PacificoFamily
      VT323 -> VT323Family
      RUSSO_ONE -> RussoOneFamily
      LED_DOTS -> FontFamily.Monospace
      SANS_BOLD -> FontFamily.SansSerif
      SERIF -> FontFamily.Serif
      MONOSPACE -> FontFamily.Monospace
      CONDENSED -> FontFamily.SansSerif
      CURSIVE -> FontFamily.Cursive
    }
  }

  fun getFontWeight(): FontWeight {
    return when (this) {
      PRESS_START, ANTON, RUSSO_ONE, LED_DOTS -> FontWeight.Black
      ORBITRON, BEBAS_NEUE, SANS_BOLD, CONDENSED -> FontWeight.ExtraBold
      MONOSPACE, SERIF -> FontWeight.Bold
      else -> FontWeight.Normal
    }
  }

  fun getFontStyle(): FontStyle {
    return when (this) {
      CURSIVE -> FontStyle.Italic
      else -> FontStyle.Normal
    }
  }
}

data class MarqueeSettings(
  val text: String = "BEM-VINDO AO LETREIRO LED!",
  val textColor: Long = 0xFFFFEB3B, // Amarelo Neon
  val backgroundColor: Long = 0xFF000000, // Preto Puro
  val textSizeSp: Float = 110f, // 40f to 220f
  val speedFactor: Float = 1.5f, // 0.2f to 4.0f
  val direction: MarqueeDirection = MarqueeDirection.RIGHT_TO_LEFT,
  val isMirrored: Boolean = false,
  val font: MarqueeFont = MarqueeFont.LED_DOTS,
  val isBlinking: Boolean = false,
  val isGlowEnabled: Boolean = true,
  val isLedGridEnabled: Boolean = true,
  val isNeonBorderEnabled: Boolean = true
)

data class ColorOption(val name: String, val color: Color, val hexValue: Long)

object MarqueePresets {
  val TextColors = listOf(
    ColorOption("Amarelo Neon", Color(0xFFFFEB3B), 0xFFFFEB3B),
    ColorOption("Ciano Laser", Color(0xFF00F0FF), 0xFF00F0FF),
    ColorOption("Verde Matrix", Color(0xFF00FF66), 0xFF00FF66),
    ColorOption("Rosa Choque", Color(0xFFFF007F), 0xFFFF007F),
    ColorOption("Laranja Fogo", Color(0xFFFF6600), 0xFFFF6600),
    ColorOption("Vermelho Alerta", Color(0xFFFF1744), 0xFFFF1744),
    ColorOption("Branco Puro", Color(0xFFFFFFFF), 0xFFFFFFFF),
    ColorOption("Roxo Neon", Color(0xFFB026FF), 0xFFB026FF),
    ColorOption("Azul Elétrico", Color(0xFF2979FF), 0xFF2979FF),
    ColorOption("Verde Limão", Color(0xFFAEEA00), 0xFFAEEA00)
  )

  val BackgroundColors = listOf(
    ColorOption("Preto Total", Color(0xFF000000), 0xFF000000),
    ColorOption("Azul Noite", Color(0xFF0A0F24), 0xFF0A0F24),
    ColorOption("Vinho Profundo", Color(0xFF20050E), 0xFF20050E),
    ColorOption("Verde Escuro", Color(0xFF04180A), 0xFF04180A),
    ColorOption("Cinza Asfalto", Color(0xFF1A1A1A), 0xFF1A1A1A),
    ColorOption("Roxo Cósmico", Color(0xFF17082A), 0xFF17082A),
    ColorOption("Azul Petróleo", Color(0xFF051B20), 0xFF051B20),
    ColorOption("Vermelho Rubro", Color(0xFF330000), 0xFF330000)
  )

  val QuickPhrases = listOf(
    "SOS 🚨",
    "TAXI 🚕",
    "CHEGUEI! 👋",
    "UBER 🚗",
    "PARABÉNS! 🎂",
    "SILÊNCIO 🤫",
    "PROMOÇÃO 🔥",
    "BEM-VINDO! ✨",
    "SHOW TIME 🎸",
    "VOLTO JÁ ⏳"
  )
}
