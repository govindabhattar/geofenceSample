package com.govinda.geofencesample

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.govinda.geofencesample.Constants.Companion.PREFS_NAME
import com.govinda.geofencesample.Constants.Companion.REMINDERS

class ReminderData(private val context: Context) {
    private val geofencingClient = LocationServices.getGeofencingClient(context)


    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun add(reminderDataModel: ReminderDataModel, success: () -> Unit, failure: (error: String) -> Unit)
    {
        val geofence = buildGeofence(reminderDataModel)
        if (geofence != null && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(buildGeofencingRequest(geofence), geofencePendingIntent)
                .addOnSuccessListener {
                    saveAll(getAll() + reminderDataModel)
                    success()
                }
                .addOnFailureListener {
                    failure("An error occured")
                }

        }
    }

    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(0)
            .addGeofences(listOf(geofence))
            .build()
    }


    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeoFenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


    private fun buildGeofence(reminderDataModel: ReminderDataModel): Geofence? {
        val latitude = reminderDataModel.latLng?.latitude
        val longitude = reminderDataModel.latLng?.longitude
        val radius = reminderDataModel.radius

        if (latitude != null && longitude != null && radius != null) {
            return Geofence.Builder()
                .setRequestId(reminderDataModel.id)
                .setCircularRegion(
                    latitude,
                    longitude,
                    radius.toFloat()
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        return null
    }

    fun remove(reminderDataModel: ReminderDataModel) {
        val list = getAll() - reminderDataModel
        saveAll(list)
    }

    private fun saveAll(list: List<ReminderDataModel>) {
        preferences
            .edit()
            .putString(REMINDERS, gson.toJson(list))
            .apply()
    }

    fun getAll(): List<ReminderDataModel> {
        if (preferences.contains(REMINDERS)) {
            val remindersString = preferences.getString(REMINDERS, null)
            val arrayOfReminders = gson.fromJson(
                remindersString,
                Array<ReminderDataModel>::class.java
            )
            if (arrayOfReminders != null) {
                return arrayOfReminders.toList()
            }
        }
        return listOf()
    }

    fun get(requestId: String?) = getAll().firstOrNull { it.id == requestId }

    fun getLast() = getAll().lastOrNull()
}