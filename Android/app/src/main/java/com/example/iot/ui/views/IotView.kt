package com.example.iot.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.iot.ui.components.ErrorDialog
import com.example.iot.ui.views.viewModel.IotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IotView(){

    //Init view model
    val vm: IotViewModel = hiltViewModel()

    //State
    val state by vm.mainState.collectAsState()

    //Pin list
    val pinList: List<String> = listOf("pin0", "pin2", "pin4", "pin5")

    //Load datas on first run
    LaunchedEffect(key1 = Unit, block = {
        vm.getDeviceInfo()
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simple Scaffold Screen") },
                navigationIcon = {
                    IconButton(
                        onClick = { /* "Open nav drawer" */ }
                    ) {
                        Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* fab click handler */ }
            ) {
                Text("Inc")
            }
        }
    ){contentPadding ->

        // Screen content
        Box(modifier = Modifier.padding(contentPadding)) {

            //Loading
            Row(
                modifier = Modifier.height(5.dp)
            ) {

                //Loading
                if(state.loading){
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        color = Color.Red
                    )
                }
            }

            //Info
            if(state.infos.isNotEmpty()){

                Column() {

                    state.infos.forEach { verticalTable ->


                    ElevatedCard(
                        modifier = Modifier.width(200.dp),
                        shape = RoundedCornerShape(10.dp)
                        //.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {

                            Text(
                                fontSize = 30.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold,
                                text = verticalTable.text
                            )


                            Text(
                                fontSize = 30.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold,
                                text = verticalTable.value
                            )

                        }
                    }
                }
                }
            }
        }

    }

    if(!state.error.isNullOrEmpty()){
        ErrorDialog(state.error)
    }
}