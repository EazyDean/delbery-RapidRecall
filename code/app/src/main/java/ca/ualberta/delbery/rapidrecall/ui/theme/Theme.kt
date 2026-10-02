package ca.ualberta.delbery.rapidrecall.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy950 = Color(0xFF07111F)
val Navy900 = Color(0xFF0C1929)
val Navy800 = Color(0xFF10243B)
val Navy700 = Color(0xFF18334E)
val Teal300 = Color(0xFF54E6C1)
val Teal400 = Color(0xFF2AC9A3)
val Orange300 = Color(0xFFFFB86B)
val Rose300 = Color(0xFFFF8E9E)
val Mist100 = Color(0xFFF1F7FA)
val Mist300 = Color(0xFFB9C7D4)

private val RapidRecallColors = darkColorScheme(
    primary = Teal300,
    onPrimary = Navy950,
    primaryContainer = Navy700,
    onPrimaryContainer = Teal300,
    secondary = Orange300,
    onSecondary = Navy950,
    background = Navy950,
    onBackground = Mist100,
    surface = Navy900,
    onSurface = Mist100,
    surfaceVariant = Navy800,
    onSurfaceVariant = Mist300,
    error = Rose300,
    onError = Navy950,
)

/** Applies the app's high-contrast, distraction-free visual language. */
@Composable
fun RapidRecallTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RapidRecallColors,
        content = content,
    )
}
