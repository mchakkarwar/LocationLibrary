package com.example

interface LocationCallbacks {
    fun receiveLocation()
    fun onAccessLocationGranted()
    fun onAccessLocationDenied()
    fun onLocationSettingEnabled()
    fun onLocationSettingDisabled()
}