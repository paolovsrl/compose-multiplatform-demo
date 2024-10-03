package org.omsi.demoproject

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.ui.tooling.preview.Preview

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(400.dp, 300.dp),
        position = WindowPosition(300.dp, 300.dp)
    )
  //  StatusPrinter.print(LoggerFactory.getILoggerFactory() as LoggerContext)
    Window(
        onCloseRequest = ::exitApplication,
        title = "Local Time App",
        state = state
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}