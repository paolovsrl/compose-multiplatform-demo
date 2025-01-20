package org.omsi.demoproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.omsi.demoproject.ui.theme.CustomTheme
import org.omsi.demoproject.ui.viewmodel.MainViewModel


@Composable
fun HomeScreen (navigateTo:(String) -> Unit = {}){
    //val viewModel: MainViewModel = koinInject()

    //val viewModel = koinViewModel<MainViewModel>()
    val viewModel = koinViewModel<MainViewModel>()



    Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()){
        Button(modifier = Modifier,
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

        Button(onClick = {viewModel.addEntry()}, content = {
            Text("Add Entry")
        })

        Button(modifier = Modifier,
            onClick = {
                navigateTo(AppScreen.Parameter.name)
            }) {
            Text("Parameter")
        }
        Button(modifier = Modifier,
            onClick = {
               navigateTo(AppScreen.Settings.name)
            }) {
            Text("Settings")
        }
        Button(modifier = Modifier,
            onClick = {
                navigateTo(AppScreen.Summary.name)
            }) {
            Text("Test")
        }
    }

}

@Composable
@Preview
fun StartScreenPreview(){
   CustomTheme {
        HomeScreen()
    }
}