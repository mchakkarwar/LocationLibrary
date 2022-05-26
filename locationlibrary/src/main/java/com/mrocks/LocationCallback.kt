package com.mrocks

interface LocationCallbacks {
    fun onLocationReceived(location: UsersLocation)
    fun onAccessLocationGranted()
    fun onAccessLocationDenied()
    fun onLocationSettingEnabled()
    fun onLocationSettingDisabled()
}