package com.mrocks

import java.util.Date

data class UsersLocation(
    val latitude: Double,
    val longitude: Double,
    val monotonicTimestampNanoseconds: Long,
    val time: Date,
    val speed: Float,
    val bearing: Float,
    val altitude: Double,
    val accuracyHorizontal: Float,
    val provider: String,
    val bearingAccuracy: Float?,
    val speedAccuracy: Float?,
    val verticalAccuracy: Float?,
) {
    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else if (other != null && this.javaClass == other.javaClass) {
            val other: UsersLocation =
                other as UsersLocation
            if (this.latitude != other.latitude && this.longitude != other.longitude) {
                false
            } else if (monotonicTimestampNanoseconds != other.monotonicTimestampNanoseconds) {
                false
            } else if (time != other.time) {
                false
            } else if (speed != other.speed) {
                false
            } else if (bearing != other.bearing) {
                false
            } else if (altitude != other.altitude) {
                false
            } else if (accuracyHorizontal != other.accuracyHorizontal) {
                false
            } else if (provider != other.provider) {
                false
            } else if (bearingAccuracy != other.bearingAccuracy) {
                false
            } else if (speedAccuracy != other.speedAccuracy) {
                false
            } else {
                verticalAccuracy == other.verticalAccuracy
            }
        } else {
            false
        }
    }
}