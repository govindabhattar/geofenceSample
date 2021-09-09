package com.govinda.geofencesample

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class ReminderDataModel(val id: String = UUID.randomUUID().toString(),
                             var latLng: LatLng?,
                             var radius: Double?,
                             var message: String?)
