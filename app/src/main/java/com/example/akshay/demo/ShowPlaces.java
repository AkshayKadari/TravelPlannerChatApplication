package com.example.akshay.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.akshay.demo.R.drawable.user;

public class ShowPlaces extends AppCompatActivity implements adapterPlaces.placepicker {
    RecyclerView rv;
    private FirebaseUser user;
    String id;
    String placeId;
    int PLACE_PICKER_REQUEST = 2;
    private ArrayList<PlaceDetails> allplaces = new ArrayList<PlaceDetails>();
    private adapterPlaces placesAdapter;
    DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_places);
        rv = (RecyclerView) findViewById(R.id.siv);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        id = String.valueOf(getIntent().getExtras().get("trpid"));
        placesAdapter = new adapterPlaces(this, allplaces);
        placesAdapter.notifyDataSetChanged();
        rv.setAdapter(placesAdapter);
        DatabaseReference placeRef = mRoot.child("Trips").child(id).child("Places");
        ValueEventListener postList = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allplaces = new ArrayList<PlaceDetails>();
                allplaces.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceDetails info = postSnapshot.getValue(PlaceDetails.class);
                    if (info != null) {
                        allplaces.add(info);
                    }

                }
                if (allplaces.isEmpty()) {
                    //title.setText("No Places added to the Trip");
                } else {
                    // title.setText("Places Added To Trip");
                    placesAdapter = new adapterPlaces(ShowPlaces.this, allplaces);
                    placesAdapter.notifyDataSetChanged();
                    rv.setAdapter(placesAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        placeRef.addValueEventListener(postList);

    }


    @Override
    public void changethelocation(String id) {
        if(id != null) {
            placeId = id;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(ShowPlaces.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
