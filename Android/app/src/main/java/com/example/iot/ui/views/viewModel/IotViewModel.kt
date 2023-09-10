package com.example.iot.ui.views.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot.repositories.IotApiRepository
import com.example.iot.repositories.model.DeviceInfoModel
import com.example.iot.ui.models.VerticalTable
import com.example.iot.ui.views.viewModel.states.IotViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IotViewModel @Inject constructor(
    private val repository : IotApiRepository
): ViewModel() {

    //Init state
    private val _mainState = MutableStateFlow<IotViewModelState>(IotViewModelState())
    var mainState = _mainState

    fun getDeviceInfo(){

        setLoadingState(true)
        setErrorState("")

        Log.d("IotViewModel", "getDeviceInfo")

        viewModelScope.launch {

            try {
                Log.d("IotViewModel", "call repository")
                val fake_result =  DeviceInfoModel(pin0 = 0, pin2 = 0, pin4 = 0, pin5 = 0, pin12 = 0, pin13 = 0, pin14 = 0, pin15 = 0)
                val result = fake_result//repository.retrofitService.getDeviceInfo()

                //Create vertical table
                var verticalTable: MutableList<VerticalTable> = mutableListOf()

                verticalTable.add(VerticalTable("pin0", result.pin0.toString()))
                verticalTable.add(VerticalTable("pin2", result.pin2.toString()))
                verticalTable.add(VerticalTable("pin4", result.pin4.toString()))
                verticalTable.add(VerticalTable("pin5", result.pin5.toString()))
                verticalTable.add(VerticalTable("pin12", result.pin12.toString()))
                verticalTable.add(VerticalTable("pin13", result.pin13.toString()))
                verticalTable.add(VerticalTable("pin14", result.pin14.toString()))
                verticalTable.add(VerticalTable("pin15", result.pin15.toString()))

                _mainState.update { IotViewModelState ->
                    IotViewModelState.copy(
                        infos = verticalTable
                    )
                }
            }catch (ex: Exception){
                Log.e("IotViewModel", ex.toString())
                setErrorState(ex.toString())
            }finally {
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(loading: Boolean){
        _mainState.update {
                IotViewModelState -> IotViewModelState.copy(loading = loading)
        }
    }

    private fun setErrorState(error: String){
        _mainState.update {
                IotViewModelState -> IotViewModelState.copy(error = error)
        }
    }
}