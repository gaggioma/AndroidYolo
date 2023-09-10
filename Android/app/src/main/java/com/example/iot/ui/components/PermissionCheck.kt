package com.example.iot.ui.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PermissionCheck(
    permissions: List<String>,
    content: @Composable() () -> Unit){

    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = permissions
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        //Check permission
        multiplePermissionsState.permissions.forEach { perm ->
            Log.d("PermissionCheck", "check ${perm.permission}")
            getPermission(perm)
        }

        //Only if all permission are granted show content
        if(multiplePermissionsState.allPermissionsGranted){
            content()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun getPermission(
    perm: PermissionState
) {
    //If perm not allowed try to request in different forms
    if(!perm.status.isGranted){
        Column {
            if (perm.status.shouldShowRationale) {
                Text(
                    "The ${perm.permission} is important for this app. Please grant the permission."
                )

            } else {
                Text(
                    "The ${perm.permission} required for this feature to be available. Please grant the permission."
                )
            }

            Button(onClick = {
                Log.d("PermissionCheck", "Launch ${perm.permission}")
                perm.launchPermissionRequest() //Show permission dialog
            }) {
                Text("Request ${perm.permission}")
            }
        }
    }
}
