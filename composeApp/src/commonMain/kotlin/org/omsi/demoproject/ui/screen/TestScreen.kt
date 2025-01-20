package org.omsi.demoproject.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.sign
import kotlin.random.Random
import kotlin.uuid.Uuid.Companion.random

@Composable
fun TestScreen(){
    /*
    Test for creating graphics that can be dragged and dropped and zoomed in and out.
    Content bigger than the screen
     */
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {


        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        var scale by remember { mutableStateOf(1f) }

        var testPoints by remember{mutableStateOf(listOf<Offset>())}

        LaunchedEffect(""){
            testPoints = generateTestSequence()
        }

        Canvas(modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x*(1/scale)
                    offsetY += dragAmount.y*(1/scale)
                }
                // Not working?
                detectTransformGestures{centroid, pan, zoom, rotation ->
                    scale *= zoom
                }
            }
            .scale(scale)
        ){
            val canvasQuadrantSize = Size(30f, 30f)
            var xMult = 10f
            var yMult = 10f
            var counter = 0f
            for(p in testPoints) {

                drawRect(
                    topLeft = Offset(offsetX+p.x, offsetY+p.y),
                    color =  Color.Blue,
                    size = canvasQuadrantSize,
                )

                //Drw different color if intersection

            }
        }

        Column(modifier = Modifier.align(Alignment.BottomEnd), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick={ scale *= 1.1f}){
                Text("+")
            }
            Button(onClick={ scale *= 0.9f}){
                Text("-")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick={offsetX = 0f; offsetY = 0f; scale=1f}){
                Text("RESET")
            }
        }


    }
}





//Generate a sequence of points
fun generateTestSequence(): List<Offset> {
    var points = mutableListOf<Offset>()

    var xMult = 10f
    var yMult = 10f
    var counter = 0f
    for(i in 1 .. 1000) {
        if (i % 100 == 0) {
            xMult = xMult * -1
            yMult = yMult + Random.nextInt(25, 40)
        }
        counter = counter+xMult

        points.add(Offset(counter, yMult))

    }

    return points
}
