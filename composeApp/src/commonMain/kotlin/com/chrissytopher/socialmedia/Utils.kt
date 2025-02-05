package com.chrissytopher.socialmedia

import dev.icerock.moko.geo.LatLng
import dev.icerock.moko.geo.LocationTracker
import dev.icerock.moko.permissions.Permission
import kotlinx.coroutines.flow.firstOrNull

fun locationFormatted(location: LatLng): String {
    return "${location.longitude},${location.latitude}"
}

suspend fun getLocation(locationTracker: LocationTracker): LatLng? = runCatching {
    if (!locationTracker.permissionsController.isPermissionGranted(Permission.LOCATION)) {
        locationTracker.permissionsController.providePermission(Permission.LOCATION)
    }
    locationTracker.startTracking()
    val location = locationTracker.getLocationsFlow().firstOrNull()
    locationTracker.stopTracking()
    return location
}.getOrNull()