package com.example.akshay.demo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 1;
    String tpid;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();

    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_maps);
         tpid= String.valueOf(getIntent().getExtras().get("tpid"));

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
               LatLng lat = place.getLatLng();
                Log.d("lat",lat.toString());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                Log.d("places",place.toString());
                final PlaceDetails toSend = new PlaceDetails();
                toSend.setLat(place.getLatLng().latitude);
                toSend.setLng(place.getLatLng().longitude);
                toSend.setPlacename(place.getName().toString());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String key1 = mRoot.child("Trips").child(tpid).child("Places").push().getKey();
                toSend.setPid(key1);
                toSend.setTpid(tpid);
                mRoot.child("Trips").child(tpid).child("Places").child(key1).setValue(toSend);
                finish();

            }
        }
    }

    // TODO: Please implement GoogleApiClient.OnConnectionFailedListener to
    // handle connection failures.
}