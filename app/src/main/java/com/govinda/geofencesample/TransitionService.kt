package com.govinda.geofencesample

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class TransitionService : JobIntentService() {

    lateinit var repositoryMy: ReminderData

    override fun onHandleWork(intent: Intent) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        repositoryMy = ReminderData(this)

        if (geofencingEvent.hasError()) {
            Log.e(Constants.LOG_TAG, "An error occured")
            return
        }
        handleEvent(geofencingEvent)
    }

    private fun handleEvent(event: GeofencingEvent) {

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            val reminder = getFirstReminder(event.triggeringGeofences)
            val message = reminder?.message
            val latLng = reminder?.latLng
            if (message != null && latLng != null) {
                sendNotification(this, message, latLng)
            }
        }
    }

    private fun getFirstReminder(triggeringGeofences: List<Geofence>): ReminderDataModel? {
        val firstGeofence = triggeringGeofences[0]
        return repositoryMy.get(firstGeofence.requestId)
    }
}
