package com.example.iot.ui.views

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.iot.BuildConfig
import com.example.iot.R
import com.example.iot.ui.components.LoadingComponent
import com.example.iot.ui.components.PermissionCheck
import com.example.iot.ui.views.viewModel.CameraViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CameraView(navController: NavController){

    //Init view model
    val vm: CameraViewModel = hiltViewModel()

    //state
    val state by vm.mainState.collectAsState()

    //Context used to get external dir
    val context = LocalContext.current

    //Save into state external cache directory
    LaunchedEffect(key1 = Unit, block = {
        Log.d("CameraView", "save external dir cache")
        vm.setExternalDir(context.externalCacheDir!!)
    })

    //Used to take a picture from camera with cameraLauncher.launch()
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            //Upload file
            vm.uploadFile(state.file!!)

            //Show origin file with Image component
            vm.setFileOrigin(state.file!!)
        }

    fun captureClick(){

        //Create new empty file where to store original image
        val file: File = vm.createImageFile("origin")

        //Update vm with file
        vm.setFile(file)

        //Keep track of new camera new file
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )

        //Launch camera to get photo
        cameraLauncher.launch(uri)
    }

    //Snackbar
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = Unit, block = {
        scope.launch {
            val result = snackbarHostState
                .showSnackbar(
                    message = "Click below button to start",
                    // Defaults to SnackbarDuration.Short
                    duration = SnackbarDuration.Short
                )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    /* Handle snackbar action performed */
                }
                SnackbarResult.Dismissed -> {
                    /* Handle snackbar dismissed */
                }
            }
        }
    })

    PermissionCheck(
        listOf(Manifest.permission.CAMERA)
    ) {

        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(
                            verticalAlignment = Alignment.Bottom
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
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { captureClick() }) {
                    IconButton(onClick = {
                        captureClick()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_a_photo_white_24dp),
                            contentDescription = "photo_detection")
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { contentPadding ->

            Box(modifier = Modifier.padding(contentPadding)) {


                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (state.error.isNotEmpty()) {
                            Column {
                                Text(text = state.error)
                            }
                            Unit
                        } else {
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                item {
                                    if (state.fileOrigin != null) {
                                        Text("Original image")
                                        Image(
                                            modifier = Modifier
                                                //.padding(16.dp, 8.dp)
                                                .height(400.dp),
                                            painter = rememberAsyncImagePainter(state.fileOrigin!!.toUri()),
                                            contentDescription = "original"
                                        )
                                        Spacer(modifier = Modifier.height(5.dp))
                                    }
                                }

                                item {
                                    if (state.loading) {
                                        LoadingComponent("circular")
                                        Unit
                                    }else {
                                        //if(state.imgBitmap != null) {
                                        //Text("Detected image \uD83D\uDD0E")
                                        Image(
                                            modifier = Modifier
                                                //.padding(16.dp, 8.dp)
                                                .height(400.dp),
                                            painter = rememberAsyncImagePainter(state.imgBitmap),
                                            contentDescription = "detected"
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }
}