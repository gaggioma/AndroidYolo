package com.example.iot.ui.views

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iot.ui.components.PermissionCheck

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainView() {

    //Navigation controller
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "videoML") {

        //iot path
        composable("iot") {
            //Permission check control
            PermissionCheck(
                listOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            ){
                IotView()
            }
        }

        //camera ml path
        composable("cameraML") {
                CameraView(navController)
        }

        //Camera video
        composable("videoML"){
            ObjectDetection(navController)
        }

    }
}