package org.upesacm.acmacmw.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.model.HeirarchyModel;
import org.upesacm.acmacmw.model.Member;

import java.util.ArrayList;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    Context context;
    private static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();
    Intent broadcastIntent = new Intent();
    int status, position, i;
    List<HeirarchyModel> heirarchyModels = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        // Retrieve the Geofencing intent
        Log.d(TAG, "onReceive Called");
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = GeofenceBroadcastReceiver.getErrorString(geofencingEvent.getErrorCode());
            return;
        }
        FirebaseDatabase.getInstance().getReference()
                .child("Heirarchy")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            HeirarchyModel heirarchyModel = dataSnapshot1.getValue(HeirarchyModel.class);
                            heirarchyModels.add(heirarchyModel);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        Member logedInMember = SessionManager.getInstance(context).getLoggedInMember();
        String sapID = logedInMember.getSap();

        for(i=0; i<heirarchyModels.size();++i){
            if (Long.toString(heirarchyModels.get(i).getSapId()).equals(sapID)){
                position = 1;
                break;
            }
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){

            //Available In campus
            status = 1;
            FirebaseDatabase.getInstance().getReference()
                    .child("Heirarchy")
                    .child(Integer.toString(position))
                    .child("availableInCampus")
                    .setValue(status);

        }
        else if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            //Not Available in Campus

            status = 0;
            FirebaseDatabase.getInstance().getReference()
                    .child("Heirarchy")
                    .child(Integer.toString(position))
                    .child("availableInCampus")
                    .setValue(status);
        }

        /*
        this is commented because i have implemented
        all the methods of GeofenceTransitionsJobIntentService
        */

        //GeofenceTransitionsJobIntentService.enqueueWork(context, intent);
    }

    // Handle errors
    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
    public static String getErrorString( Exception e) {
        if (e instanceof ApiException) {
            return getErrorString(((ApiException) e).getStatusCode());
        } else {
            return "Unknown error";
        }
    }
}
