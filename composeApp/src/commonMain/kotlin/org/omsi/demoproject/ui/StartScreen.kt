package org.omsi.demoproject.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.utils.EmptyContent.status
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.omsi.demoproject.viewmodel.MainViewModel


@Composable
fun HomeScreen (navigateTo:(String) -> Unit = {}){
    //val viewModel: MainViewModel = koinInject()

    //val viewModel = koinViewModel<MainViewModel>()
    val viewModel = koinViewModel<MainViewModel>()



    Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()){
        Button(modifier = Modifier.padding(start = 20.dp, top = 10.dp),
            onClick = {
                if(!viewModel.status){
                    viewModel.connect()
                } else{
                    viewModel.disconnect()
                }
                viewModel.status=!viewModel.status
            }) {
            Text(if(viewModel.status) "stop" else "start")
        }

        Button(modifier = Modifier.padding(start = 20.dp, top = 10.dp),
            onClick = {
                navigateTo(AppScreen.Parameter.name)
            }) {
            Text("Parameter")
        }
        Button(modifier = Modifier.padding(start = 20.dp, top = 10.dp),
            onClick = {
               navigateTo(AppScreen.Settings.name)
            }) {
            Text("Settings")
        }
    }

}