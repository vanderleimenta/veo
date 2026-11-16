package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ColorOption
import com.example.model.MarqueeDirection
import com.example.model.MarqueeFont
import com.example.model.MarqueePresets
import com.example.model.MarqueeSettings
import com.example.ui.components.MarqueeRenderer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(
  settings: MarqueeSettings,
  onUpdateSettings: (MarqueeSettings) -> Unit,
  onLaunchFullscreen: () -> Unit
) {
  val focusManager = LocalFocusManager.current
  var showGestureModal by remember { mutableStateOf(false) }
  var showTextColorDialog by remember { mutableStateOf(false) }
  var showBgColorDialog by remember { mutableStateOf(false) }
  var showFontDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.logo_hollywood_led_1794278088215),
              contentDescription = "Logo IVEO MSG",
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.outlineVariant,
                  shape = RoundedCornerShape(8.dp)
                )
            )
            Text(
              text = "IVEO MSG",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
            )
          }
        },
        actions = {
          IconButton(
            onClick = { showGestureModal = true },
            modifier = Modifier.testTag("help_topbar_button")
          ) {
            Icon(
              imageVector = Icons.Default.HelpOutline,
              contentDescription = "Guia de gestos"
            )
          }
          IconButton(
            onClick = {
              onUpdateSettings(
                MarqueeSettings(
                  text = "BEM-VINDO AO IVEO MSG!",
                  textColor = 0xFFFFEB3B,
                  backgroundColor = 0xFF000000,
                  textSizeSp = 110f,
                  speedFactor = 1.5f,
                  direction = MarqueeDirection.RIGHT_TO_LEFT,
                  isMirrored = false,
                  font = MarqueeFont.LED_DOTS,
                  isBlinking = false,
                  isGlowEnabled = true,
                  isLedGridEnabled = true,
                  isNeonBorderEnabled = true
                )
              )
            },
            modifier = Modifier.testTag("reset_defaults_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Restaurar padrões"
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
      ) {
        Box(modifier = Modifier.padding(16.dp)) {
          Button(
            onClick = onLaunchFullscreen,
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("start_fullscreen_cta_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFFF5F5F5),
              contentColor = Color(0xFF121212)
            ),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Fullscreen,
              contentDescription = null,
              modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "INICIAR TELA CHEIA",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
              )
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Pinned Live Interactive Preview Card (Tamanho diminuído e na mesma proporção da tela cheia)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Black),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onLaunchFullscreen() }
            .testTag("marquee_preview_card")
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            MarqueeRenderer(
              settings = settings,
              modifier = Modifier.fillMaxSize(),
              isPreview = true
            )

            // Preview Overlay Tags (Mirror indicator if active)
            if (settings.isMirrored) {
              Surface(
                color = Color(0xFF333333),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFF555555)),
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(4.dp)
              ) {
                Text(
                  text = "🪞 ESPELHADO",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // Scrollable settings section
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {

      // 2. Text Input and Quick Phrase Presets
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Mensagem do Letreiro",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = settings.text,
              onValueChange = { newText ->
                val singleLine = newText.replace("\r\n", " ").replace("\n", " ").replace("\r", " ")
                onUpdateSettings(settings.copy(text = singleLine))
              },
              singleLine = true,
              maxLines = 1,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("text_input_field"),
              placeholder = { Text("Digite sua frase ou palavra...") },
              trailingIcon = {
                if (settings.text.isNotEmpty()) {
                  IconButton(
                    onClick = { onUpdateSettings(settings.copy(text = "")) },
                    modifier = Modifier.testTag("clear_text_button")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Clear,
                      contentDescription = "Limpar texto"
                    )
                  }
                }
              },
              shape = RoundedCornerShape(12.dp),
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
              keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Frases rápidas prontas:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Quick preset chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(MarqueePresets.QuickPhrases) { phrase ->
                SuggestionChip(
                  onClick = {
                    onUpdateSettings(settings.copy(text = phrase))
                    focusManager.clearFocus()
                  },
                  label = { Text(phrase) }
                )
              }
            }
          }
        }
      }

      // 3. Bloco de Cores (Apenas botões preenchidos com as cores selecionadas)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Botão preenchido com a Cor das Letras
            val textColorObj = Color(settings.textColor)
            val textLum = 0.299f * textColorObj.red + 0.587f * textColorObj.green + 0.114f * textColorObj.blue
            val textContrastColor = if (textLum > 0.5f) Color.Black else Color.White

            Button(
              onClick = { showTextColorDialog = true },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .border(
                  width = 1.5.dp,
                  color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(12.dp)
                )
                .testTag("text_color_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = textColorObj,
                contentColor = textContrastColor
              )
            ) {
              Icon(
                imageVector = Icons.Default.TextFields,
                contentDescription = "Cor das Letras",
                tint = textContrastColor,
                modifier = Modifier.size(24.dp)
              )
            }

            // Botão preenchido com a Cor do Fundo
            val bgColorObj = Color(settings.backgroundColor)
            val bgLum = 0.299f * bgColorObj.red + 0.587f * bgColorObj.green + 0.114f * bgColorObj.blue
            val bgContrastColor = if (bgLum > 0.5f) Color.Black else Color.White

            Button(
              onClick = { showBgColorDialog = true },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .border(
                  width = 1.5.dp,
                  color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(12.dp)
                )
                .testTag("bg_color_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = bgColorObj,
                contentColor = bgContrastColor
              )
            ) {
              Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "Cor do Fundo",
                tint = bgContrastColor,
                modifier = Modifier.size(24.dp)
              )
            }
          }
        }
      }

      // 4. Bloco de Sentido, Modo Espelho e Tipo de Texto (Fonte)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Setas indicando sentido
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Seta Esquerda (◀)
              FilledTonalIconButton(
                onClick = {
                  onUpdateSettings(settings.copy(direction = MarqueeDirection.RIGHT_TO_LEFT))
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = if (settings.direction == MarqueeDirection.RIGHT_TO_LEFT) {
                    Color(0xFF383838)
                  } else {
                    MaterialTheme.colorScheme.surface
                  },
                  contentColor = if (settings.direction == MarqueeDirection.RIGHT_TO_LEFT) {
                    Color.White
                  } else {
                    Color(0xFF888888)
                  }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .size(46.dp)
                  .testTag("direction_left_btn")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Rolar para esquerda",
                  modifier = Modifier.size(24.dp)
                )
              }

              // Pausar / Estático (⏸)
              FilledTonalIconButton(
                onClick = {
                  val newDir = if (settings.direction == MarqueeDirection.STATIC) {
                    MarqueeDirection.RIGHT_TO_LEFT
                  } else {
                    MarqueeDirection.STATIC
                  }
                  onUpdateSettings(settings.copy(direction = newDir))
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = if (settings.direction == MarqueeDirection.STATIC) {
                    Color(0xFF383838)
                  } else {
                    MaterialTheme.colorScheme.surface
                  },
                  contentColor = if (settings.direction == MarqueeDirection.STATIC) {
                    Color.White
                  } else {
                    Color(0xFF888888)
                  }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .size(46.dp)
                  .testTag("direction_pause_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.Pause,
                  contentDescription = "Pausado",
                  modifier = Modifier.size(24.dp)
                )
              }

              // Seta Direita (▶)
              FilledTonalIconButton(
                onClick = {
                  onUpdateSettings(settings.copy(direction = MarqueeDirection.LEFT_TO_RIGHT))
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = if (settings.direction == MarqueeDirection.LEFT_TO_RIGHT) {
                    Color(0xFF383838)
                  } else {
                    MaterialTheme.colorScheme.surface
                  },
                  contentColor = if (settings.direction == MarqueeDirection.LEFT_TO_RIGHT) {
                    Color.White
                  } else {
                    Color(0xFF888888)
                  }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .size(46.dp)
                  .testTag("direction_right_btn")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = "Rolar para direita",
                  modifier = Modifier.size(24.dp)
                )
              }
            }

            // Divisor vertical
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
            )

            // Grupo unindo Botão de Espelho e Botão de Tipo de Texto (Fonte)
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Botão de Modo Espelho
              FilledTonalIconButton(
                onClick = {
                  onUpdateSettings(settings.copy(isMirrored = !settings.isMirrored))
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = if (settings.isMirrored) Color(0xFF383838) else MaterialTheme.colorScheme.surface,
                  contentColor = if (settings.isMirrored) Color.White else Color(0xFF888888)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .size(46.dp)
                  .testTag("mirror_mode_switch")
              ) {
                Icon(
                  imageVector = Icons.Default.Flip,
                  contentDescription = "Modo Espelho Invertido",
                  modifier = Modifier.size(24.dp)
                )
              }

              // Botão de Tipo de Texto (Fonte) - Apresenta o nome e o modelo exato da fonte
              Surface(
                onClick = { showFontDialog = true },
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                  .height(46.dp)
                  .widthIn(min = 96.dp)
                  .testTag("font_picker_button")
              ) {
                Row(
                  modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Text(
                    text = settings.font.displayName,
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontFamily = settings.font.getFontFamily(),
                      fontWeight = settings.font.getFontWeight(),
                      fontStyle = settings.font.getFontStyle(),
                      fontSize = 13.sp,
                      color = Color.White
                    ),
                    maxLines = 1
                  )
                }
              }
            }
          }
        }
      }

      // 5. Bloco de Velocidade e Tamanho (Apenas ícone da função e sinais de menos e mais na mesma linha)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Lado Esquerdo: Velocidade na mesma linha (Ícone + Menor + Maior)
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Speed,
                  contentDescription = "Velocidade",
                  tint = Color(0xFFE0E0E0),
                  modifier = Modifier.size(24.dp)
                )
              }

              // Menor (-)
              FilledTonalIconButton(
                onClick = {
                  val newSpeed = (settings.speedFactor - 0.5f).coerceIn(0.0f, 10.0f)
                  onUpdateSettings(settings.copy(speedFactor = newSpeed))
                },
                modifier = Modifier
                  .size(44.dp)
                  .testTag("speed_decrease_button"),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = MaterialTheme.colorScheme.surface,
                  contentColor = Color.White
                )
              ) {
                Icon(
                  imageVector = Icons.Default.Remove,
                  contentDescription = "Menor velocidade",
                  modifier = Modifier.size(22.dp)
                )
              }

              // Maior (+)
              FilledTonalIconButton(
                onClick = {
                  val newSpeed = (settings.speedFactor + 0.5f).coerceIn(0.0f, 10.0f)
                  onUpdateSettings(settings.copy(speedFactor = newSpeed))
                },
                modifier = Modifier
                  .size(44.dp)
                  .testTag("speed_increase_button"),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = MaterialTheme.colorScheme.surface,
                  contentColor = Color.White
                )
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Maior velocidade",
                  modifier = Modifier.size(22.dp)
                )
              }
            }

            // Divisor vertical
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
            )

            // Lado Direito: Tamanho na mesma linha (Ícone + Menor + Maior)
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.TextFields,
                  contentDescription = "Tamanho",
                  tint = Color(0xFFE0E0E0),
                  modifier = Modifier.size(24.dp)
                )
              }

              // Menor (-)
              FilledTonalIconButton(
                onClick = {
                  val newSize = (settings.textSizeSp - 25f).coerceIn(40f, 350f)
                  onUpdateSettings(settings.copy(textSizeSp = newSize))
                },
                modifier = Modifier
                  .size(44.dp)
                  .testTag("size_decrease_button"),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = MaterialTheme.colorScheme.surface,
                  contentColor = Color.White
                )
              ) {
                Icon(
                  imageVector = Icons.Default.Remove,
                  contentDescription = "Menor tamanho",
                  modifier = Modifier.size(22.dp)
                )
              }

              // Maior (+)
              FilledTonalIconButton(
                onClick = {
                  val newSize = (settings.textSizeSp + 25f).coerceIn(40f, 350f)
                  onUpdateSettings(settings.copy(textSizeSp = newSize))
                },
                modifier = Modifier
                  .size(44.dp)
                  .testTag("size_increase_button"),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                  containerColor = MaterialTheme.colorScheme.surface,
                  contentColor = Color.White
                )
              ) {
                Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Maior tamanho",
                  modifier = Modifier.size(22.dp)
                )
              }
            }
          }
        }
      }



      // Bottom Spacer for FAB/Bar
      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

  // Gesture Modal
  if (showGestureModal) {
    androidx.compose.material3.AlertDialog(
      onDismissRequest = { showGestureModal = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = painterResource(id = R.drawable.logo_hollywood_led_1794278088215),
            contentDescription = "Logo IVEO MSG",
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(6.dp))
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text("Controles por Gestos", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Na tela cheia, use gestos naturais:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
          )
          GestureDetailItem(icon = "👆", title = "1 Toque", desc = "Exibe ou oculta os botões rápidos na tela.")
          GestureDetailItem(icon = "✌️", title = "2 Toques Rápidos", desc = "Liga/desliga o Modo Espelho instantaneamente.")
          GestureDetailItem(icon = "↔️", title = "Deslize Horizontal", desc = "Altera o sentido da rolagem (esquerda ou direita).")
          GestureDetailItem(icon = "↕️", title = "Deslize Vertical", desc = "Aumenta ou diminui a velocidade da rolagem.")
          GestureDetailItem(icon = "🤏", title = "Pinçar com 2 dedos", desc = "Ajusta o tamanho das letras (zoom in / zoom out).")
          GestureDetailItem(icon = "⏱️", title = "Toque Longo", desc = "Retorna imediatamente para o painel de edição.")
        }
      },
      confirmButton = {
        Button(
          onClick = { showGestureModal = false },
          modifier = Modifier.testTag("close_help_dialog_button")
        ) {
          Text("OK, Entendi!")
        }
      }
    )
  }

  // Dialog de seleção da Cor das Letras
  if (showTextColorDialog) {
    AlertDialog(
      onDismissRequest = { showTextColorDialog = false },
      title = {
        Text("Cor das Letras", fontWeight = FontWeight.Bold)
      },
      text = {
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          MarqueePresets.TextColors.forEach { colorOpt ->
            ColorCircleItem(
              colorOption = colorOpt,
              isSelected = settings.textColor == colorOpt.hexValue
            ) {
              onUpdateSettings(settings.copy(textColor = colorOpt.hexValue))
              showTextColorDialog = false
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showTextColorDialog = false }) {
          Text("Fechar")
        }
      }
    )
  }

  // Dialog de seleção da Cor do Fundo
  if (showBgColorDialog) {
    AlertDialog(
      onDismissRequest = { showBgColorDialog = false },
      title = {
        Text("Cor do Fundo", fontWeight = FontWeight.Bold)
      },
      text = {
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          MarqueePresets.BackgroundColors.forEach { colorOpt ->
            ColorCircleItem(
              colorOption = colorOpt,
              isSelected = settings.backgroundColor == colorOpt.hexValue,
              showBorder = true
            ) {
              onUpdateSettings(settings.copy(backgroundColor = colorOpt.hexValue))
              showBgColorDialog = false
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showBgColorDialog = false }) {
          Text("Fechar")
        }
      }
    )
  }

  // Dialog de seleção de Tipo de Texto (Fonte)
  if (showFontDialog) {
    AlertDialog(
      onDismissRequest = { showFontDialog = false },
      title = {
        Text("Escolha a Fonte", fontWeight = FontWeight.Bold)
      },
      text = {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 440.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(MarqueeFont.values()) { fontOption ->
            val isSelected = settings.font == fontOption
            OutlinedCard(
              onClick = {
                onUpdateSettings(settings.copy(font = fontOption))
                showFontDialog = false
              },
              colors = CardDefaults.outlinedCardColors(
                containerColor = if (isSelected) Color(0xFF333333) else MaterialTheme.colorScheme.surface
              ),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                  if (isSelected) listOf(Color.White, Color.LightGray)
                  else listOf(Color.Transparent, Color.Transparent)
                ),
                width = if (isSelected) 2.dp else 1.dp
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = fontOption.displayName,
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                  if (isSelected) {
                    Text(
                      text = "ATIVO ✓",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                      )
                    )
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = fontOption.sampleText,
                  fontFamily = fontOption.getFontFamily(),
                  fontWeight = fontOption.getFontWeight(),
                  fontStyle = fontOption.getFontStyle(),
                  fontSize = 15.sp,
                  maxLines = 1,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showFontDialog = false }) {
          Text("Fechar")
        }
      }
    )
  }
}


@Composable
private fun ColorCircleItem(
  colorOption: ColorOption,
  isSelected: Boolean,
  showBorder: Boolean = false,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(colorOption.color)
        .border(
          width = if (isSelected) 3.dp else if (showBorder) 1.dp else 0.dp,
          color = if (isSelected) Color(0xFF00F0FF) else Color.DarkGray,
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      if (isSelected) {
        Icon(
          imageVector = Icons.Default.Done,
          contentDescription = "Selecionado",
          tint = if (colorOption.color == Color.White || colorOption.color == Color(0xFFFFEB3B)) Color.Black else Color.White,
          modifier = Modifier.size(24.dp)
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = colorOption.name.split(" ").firstOrNull() ?: "",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun GestureDetailItem(icon: String, title: String, desc: String) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Text(text = icon, fontSize = 16.sp, modifier = Modifier.width(30.dp))
    Column {
      Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
      Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
