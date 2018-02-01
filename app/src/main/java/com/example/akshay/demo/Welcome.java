package com.example.akshay.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Welcome extends AppCompatActivity implements View.OnClickListener,UserAdapter.ItemClickCallBack, Home.OnFragmentInteractionListener,EditProfile.OnFragmentInteractionListener,AlbumAdapter.ItemClickCallBack ,Users.OnFragmentInteractionListener,Tripslist.OnFragmentInteractionListener,TripsAdapter.ItemClickCallBack,OtherUsers.OnFragmentInteractionListener,MyListsAdapter.ItemClickCallBack1{
    private FirebaseAuth mAuth;
    FirebaseUser user;
    User LOGGED_IN_USER;
    private ArrayList<User> memberslist;
    private ArrayList<User> allUsers;

    DatabaseReference  mConditionRef ;
    ImageView buttonUsers,buttonHome,buttonMessage;
TextView textViewCount;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    private void setRefreshedUser()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();



        mRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("Users").hasChild(user.getUid())) {

                    DataSnapshot toCheck = snapshot.child("Users").child(user.getUid());
                    LOGGED_IN_USER = toCheck.getValue(User.class);
                    //Log.d("User Is This",currUser.getFirstName());

                    Home toSend = new Home();
                    toSend.setUserInfo(LOGGED_IN_USER);
                    getFragmentManager().beginTransaction().add(R.id.container, toSend, "home").commitAllowingStateLoss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SetTheAppIcon()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.my_con);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SetTheAppIcon();
        user = FirebaseAuth.getInstance().getCurrentUser();

        buttonUsers = (ImageView) findViewById(R.id.imageViewUser_G);
        buttonHome = (ImageView) findViewById(R.id.imageViewHome_G);
        buttonMessage = (ImageView) findViewById(R.id.imageViewMessage_G);
        textViewCount = (TextView) findViewById(R.id.textViewCount);
        buttonHome.setImageResource(R.drawable.home_sel);
        buttonUsers.setOnClickListener(this);
        buttonHome.setOnClickListener(this);
        buttonMessage.setOnClickListener(this);
        setRefreshedUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imageViewHome_G:
                mConditionRef = mRoot.child("Users").child(user.getUid());
                mConditionRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LOGGED_IN_USER = dataSnapshot.getValue(User.class);
                        setDefaultImages();
                        buttonHome.setImageResource(R.drawable.home_sel);
                        Home toSend = new Home();
                        toSend.setUserInfo(LOGGED_IN_USER);
                        getFragmentManager().beginTransaction().replace(R.id.container, toSend, "home")
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                break;
            case R.id.imageViewUser_G:
                setDefaultImages();
                buttonUsers.setImageResource(R.drawable.user_sel);
                getFragmentManager().beginTransaction().replace(R.id.container,new Users(),"users")
                        .addToBackStack(null)
                        .commit();
                break;


            case R.id.imageViewMessage_G:
                setDefaultImages();
                buttonMessage.setImageResource(R.drawable.message_sel);
                getFragmentManager().beginTransaction().replace(R.id.container,new Tripslist(),"allTrips")
                        .addToBackStack(null)
                        .commit();
                break;
        }

    }
    private void setDefaultImages()
    {
        buttonHome.setImageResource(R.drawable.home);
        buttonMessage.setImageResource(R.drawable.message);
        buttonUsers.setImageResource(R.drawable.user);
    }
    @Override
    public void onFragmentInteraction(User user) {
        EditProfile toEdit = new EditProfile();
        // toEdit.setUserInfo(user);
        getFragmentManager().beginTransaction().replace(R.id.container,new EditProfile(),"editProfile")
                .addToBackStack(null)
                .commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.Logout:
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();



                Intent Login = new Intent(Welcome.this,MainActivity.class);
                startActivity(Login);
                finish();
                return true;


            case R.id.addtrip:
                Intent trips = new Intent(Welcome.this,TripsActivity.class);
                startActivity(trips);
                finish();
                return true;

            case  R.id.Mytrips:
                Intent tp = new Intent(Welcome.this,MyLists.class);
                startActivity(tp);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenu = getMenuInflater();
        myMenu.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFragmentEditInteraction() {
        mConditionRef = mRoot.child("Users").child(user.getUid());
        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(LOGGED_IN_USER!=null) {
                    LOGGED_IN_USER = dataSnapshot.getValue(User.class);
                    setDefaultImages();
                    buttonHome.setImageResource(R.drawable.home_sel);
                    Home toSend = new Home();
                    toSend.setUserInfo(LOGGED_IN_USER);
                    getFragmentManager().beginTransaction().replace(R.id.container, toSend, "home")
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void OnItemClick(Photos selectedPhoto) {

    }

    @Override
    public void onUserFragmentInteraction(Uri uri) {

    }

    @Override
    public void OnItemClick(User selectedUser) {
        OtherUsers toSend = new OtherUsers();
        toSend.setSelectedUser(selectedUser);
        getFragmentManager().beginTransaction().replace(R.id.container,toSend,"otherUsers")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void OnItemClick(final Trips selectedUser) {
        final DatabaseReference membersref = FirebaseDatabase.getInstance().getReference().child("Trips").child(selectedUser.getTpId()).child("members");

        ValueEventListener pl=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memberslist = new ArrayList<User>();
                memberslist.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User info = postSnapshot.getValue(User.class);
                    memberslist.add(info);
                    Log.d("memberslist", info.getFirstName());

                }
                int Count1 = 0;
                for (int i = 0; i < memberslist.size(); i++) {
                    if (memberslist.get(i).getFirstName().equals(LOGGED_IN_USER.getFirstName())) {
                        Count1 = 1;
                        Intent it = new Intent(Welcome.this, ChatsActivity.class);
                        it.putExtra("currenttrip", selectedUser.getTpId());
                        startActivity(it);
                    }
                }
                if (Count1 == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Welcome.this);
                    builder.setTitle("Join Group")
                            .setMessage("Would you like to join group?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("logged in user", LOGGED_IN_USER.getFirstName());
                                    final DatabaseReference memref = FirebaseDatabase.getInstance().getReference().child("Trips").child(selectedUser.getTpId()).child("members");
                                    memref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.hasChild(LOGGED_IN_USER.getUserID())) {
                                                User toInsert = new User();

                                                toInsert.setFirstName(LOGGED_IN_USER.getFirstName());
                                                toInsert.setLastName(LOGGED_IN_USER.getLastName());
                                                toInsert.setDpUrl(LOGGED_IN_USER.getDpUrl().toString());
                                                toInsert.setGender(false);
                                                toInsert.setUserID(LOGGED_IN_USER.getUserID());
                                                toInsert.setUserName(LOGGED_IN_USER.getUserName());
                                                Log.d("what is this ", toInsert.getFirstName());
                                                DatabaseReference childRef = memref.push();
                                                toInsert.setUserID(childRef.getKey());
                                                childRef.setValue(toInsert);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    Intent intent = new Intent(Welcome.this, ChatsActivity.class);
                                    intent.putExtra("currenttrip", selectedUser.getTpId());
                                    startActivity(intent);
                                }

                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };membersref.addValueEventListener(pl);


    }

    @Override
    public void OnItemLongClick(final Trips selectedUser) {



    }

    @Override
    public void OnItemMessageIconClick(Trips selectedUser) {

    }

    @Override
    public void OnItemMessageIconClick(User selectedUser) {

    }

    @Override
    public void onTripsInteractionListener(Uri uri) {

    }

    @Override
    public void onOtherUserMessageCreateFragmentInteraction(User Recent_user) {
        
    }
}
