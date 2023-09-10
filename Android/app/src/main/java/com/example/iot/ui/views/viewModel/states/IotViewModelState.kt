package com.example.iot.ui.views.viewModel.states

import com.example.iot.repositories.model.DeviceInfoModel
import com.example.iot.ui.models.VerticalTable

data class IotViewModelState(
    val infos: List<VerticalTable> = listOf(),
    var loading: Boolean = false,
    var error: String = ""
)
