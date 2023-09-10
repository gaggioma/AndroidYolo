package com.example.iot.ui.views

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.iot.R
import com.example.iot.ui.components.PermissionCheck
import com.example.iot.ui.views.viewModel.CameraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ObjectDetection(navController: NavController){

    //Init view model
    val vm: CameraViewModel = hiltViewModel()

    //state
    val state by vm.mainState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("\uD83D\uDD75️")
                            Image(
                                modifier = Modifier
                                    .height(24.dp),
                                painter = painterResource(id = R.drawable.android_logo),
                                contentDescription = "android_icon"
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navController.navigate("cameraML") }) {
                                Image(
                                    painter = painterResource(id = R.drawable.photo_camera_white_24dp),
                                    contentDescription = "cameraML"
                                )
                            }

                            IconButton(onClick = { navController.navigate("videoML") }) {
                                Image(
                                    painter = painterResource(id = R.drawable.smart_display_white_24dp),
                                    contentDescription = "videoML"
                                )

                            }
                        }
                }
            })
        }
    ) { contentPadding ->
        val padding = contentPadding
        Box(modifier = Modifier.padding(padding)) {

            PermissionCheck(
                listOf(Manifest.permission.CAMERA)
            ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "${state.frameRate} FPS")
                            //Text(text = "Detection dim ${state.responseDimension} Byte")
                        }

                    }

                    Column() {
                        ImageDrawer(state.imgBitmap, 4F)

                        CameraVideoView(vm = vm)
                    }
                }
            }
    }
}

@Composable
fun ImageDrawer(
    image: Bitmap?,
    scale: Float = 2f
){

        Box(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
                if (image != null) {
                    withTransform({

                        scale(
                            scaleX = scale,
                            scaleY = scale,
                            pivot = Offset(image.width / scale, image.height / scale)
                        )
                        translate(
                            left = 95f,
                            top = 70f
                        )

                    }) {
                        drawImage(
                            image.asImageBitmap()
                        )
                    }
                }
            })
        }

}