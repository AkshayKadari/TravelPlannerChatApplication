package com.example.akshay.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyLists extends AppCompatActivity implements MyListsAdapter.ItemClickCallBack1{
     RecyclerView recyclerView;
    private MyListsAdapter tripsAdapter;
    private ArrayList<Trips> allTrips = new ArrayList<Trips>();
    TextView tv;
    private FirebaseUser user;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;
    DatabaseReference mFriendRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lists);
        allTrips = new ArrayList<Trips>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView)findViewById(R.id.mylist);
        tv = (TextView)findViewById(R.id.textView9);
        tv.setVisibility(View.INVISIBLE);





        mConditionRef = mRoot.child("Trips");


        mConditionRef.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Trips toadd = dataSnapshot.getValue(Trips.class);
                if(toadd==null)
                {
                    Toast.makeText(MyLists.this,"Logout",Toast.LENGTH_SHORT).show();
                }
                if(user!=null) {


                    if (toadd.getAdminId().equals(user.getUid())) {
                        allTrips.add(toadd);
                        if (allTrips == null) {
                            tv.setText("No Trips Created");
                            tv.setVisibility(View.INVISIBLE);
                        }
                    }
                    Log.d("XXXXXX", allTrips.toString());
                    recyclerView.setLayoutManager(new LinearLayoutManager(MyLists.this, LinearLayoutManager.VERTICAL, false));
                    tripsAdapter = new MyListsAdapter(allTrips, MyLists.this, R.layout.mylistrow, user.getUid());
                    tripsAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(tripsAdapter);
                }
                else
                {
                    finish();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void OnItemClick(Trips selectedUser) {
        Intent it = new Intent(MyLists.this,ChatsActivity.class);
        it.putExtra("currenttrip",selectedUser.getTpId());
        startActivity(it);
    }
}
