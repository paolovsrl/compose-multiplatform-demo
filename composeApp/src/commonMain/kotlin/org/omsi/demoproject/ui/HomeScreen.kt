package org.omsi.demoproject.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.omsi.demoproject.viewmodel.MainViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen (){
    val viewModel: MainViewModel= koinInject()

    var status by remember { mutableStateOf(false) }
    Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()){
        Button(modifier = Modifier.padding(start = 20.dp, top = 10.dp),
            onClick = {
                if(!status){
                    viewModel.connect()
                } else{
                    viewModel.disconnect()
                }
                status=!status
            }) {
            Text(if(status) "stop" else "start")
        }
    }

}