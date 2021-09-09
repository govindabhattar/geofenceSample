package com.govinda.geofencesample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class GeoFenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Constants.enqueueWork(context, intent)
    }
}
