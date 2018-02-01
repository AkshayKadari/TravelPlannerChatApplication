package com.example.akshay.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class ChatsActivity extends AppCompatActivity implements View.OnClickListener , CityWeatherAdapter.ItemClickCallBack {
    TextView username;
    ImageView logout;
    ArrayList<AddFriend> frndsList;
    ArrayList<DeletedMessages> userslist;
    ArrayList<User> memberslist;
    FirebaseUser user;
    ImageView btnSend,bntGal;
    DialogInterface dialogInterface;
    AlertDialog.Builder builder;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    User currentUser;
    String currentEmail;

    String tpid;
    EditText chatmsg;
    private RecyclerView recyclerView;
    ArrayList<User> usersList;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    static final int REQUEST_IMAGE_GET = 1;
    private Uri fullPhotoUri;
    private CityWeatherAdapter cityWeatherAdapter;
    StorageReference storageRef;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;

    ArrayList<chatobj> allChats = new ArrayList<chatobj>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            // imageView.setImageURI(fullPhotoUri);


            final chatobj toSend = new chatobj();
            toSend.setMessage("");
            toSend.setFulname(user.getDisplayName());
            toSend.setWhen(new Date());
            toSend.setComments("");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String key1 = mRoot.child("Trips").child(tpid).push().getKey();
            toSend.setKey(key1);
            toSend.setUserID(user.getUid());



            storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("chats/images/" + key1 + ".png");

            UploadTask uploadTask = riversRef.putFile(fullPhotoUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    toSend.setImageUrl(downloadUrl.toString());
                    mRoot.child("Trips").child("CompleteChat").child(tpid).child(key1).setValue(toSend);
                    //  Toast.makeText(Home.this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("Upload is paused");
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        username = (TextView)findViewById(R.id.textViewUser);
        logout = (ImageView)findViewById(R.id.imageViewLogout);
        logout.setOnClickListener(this);
          tpid = String.valueOf(getIntent().getExtras().get("currenttrip"));
        user = FirebaseAuth.getInstance().getCurrentUser();
        username.setText("Welcome, " + user.getDisplayName());
        recyclerView = (RecyclerView) findViewById(R.id.container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        cityWeatherAdapter = new CityWeatherAdapter(allChats,this,R.layout.chatlist,user.getUid());
        cityWeatherAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cityWeatherAdapter);

        btnSend =  (ImageView)findViewById(R.id.imageViewsend);
        bntGal =  (ImageView)findViewById(R.id.imageViewGal);
        chatmsg = (EditText)findViewById(R.id.editTextchat);

        DatabaseReference tripRef=mRoot.child("Trips").child(tpid).child("Status");
        ValueEventListener postList=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    statusTrip info = postSnapshot.getValue(statusTrip.class);
                    if(info!=null){
                        btnSend.setEnabled(false);
                        bntGal.setEnabled(false);
                        chatmsg.setEnabled(false);
                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tripRef.addValueEventListener(postList);
        bntGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatobj toSend = new chatobj();
                toSend.setMessage(chatmsg.getText().toString());
                toSend.setFulname(user.getDisplayName());
                toSend.setImageUrl("NA");
                toSend.setWhen(new Date());
                toSend.setComments("");
                // toSend.setCom(new comment());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String key1 = mRoot.child("Trips").child(tpid).push().getKey();
                toSend.setKey(key1);
                toSend.setUserID(user.getUid());
                //the below line also can be used
                // mRoot.child("expenses").child("/"+user.getUid()+"/").child("/"+key1+"/").setValue(newExpense);
                mRoot.child("Trips").child(tpid).child("CompleteChat").child(key1).setValue(toSend);

                chatmsg.setText("");



            }
        });


        mConditionRef = mRoot.child("Trips").child(tpid).child("CompleteChat");
        //showProgressDialog();
        mConditionRef.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatobj toadd = new chatobj();
                toadd.setCom(new ArrayList<comment>());
                toadd= dataSnapshot.getValue(chatobj.class);

                allChats.add(toadd);
                if(allChats.size()!= 0)
                {
                    // textViewmesg.setVisibility(View.INVISIBLE);
                }
                Log.d("XXXXXX",allChats.toString());
                cityWeatherAdapter.notifyDataSetChanged();



            }



            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // allUsers.re(toadd);


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*Expense toremove = dataSnapshot.getValue(Expense.class);
                MyList.remove(toremove);
                adapter.notifyDataSetChanged();*/
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.AddLocation:

                Intent Login = new Intent(ChatsActivity.this,MapsActivity.class);
                Login.putExtra("tpid",tpid);
                startActivity(Login);
                return true;



            case R.id.ShowPlace:

                Intent place = new Intent(ChatsActivity.this,ShowPlaces.class);
                place.putExtra("trpid",tpid);
               startActivity(place);
                return true;

            case R.id.RoundTrip:
                Intent rt = new Intent(ChatsActivity.this,MapsActivity2.class);
                rt.putExtra("tripid",tpid);
                startActivity(rt);
                return  true;

            case R.id.AddFriend:
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    currentEmail=user.getUid();

                }
                if(currentEmail!=null) {
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
                    final ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            currentUser = new User();
                            currentUser = null;
                            usersList=new ArrayList<User>();
                            usersList.clear();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                User info = postSnapshot.getValue(User.class);
                                if (currentEmail.equals(info.getUserID())) {
                                    currentUser = info;
                                }else{
                                    usersList.add(info);
                                }

                            }

                            final DatabaseReference frndRef = dbRef.child(currentUser.getUserID()).child("Friends");
                            ValueEventListener postListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    frndsList = new ArrayList<AddFriend>();
                                    frndsList.clear();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        AddFriend info = postSnapshot.getValue(AddFriend.class);
                                        frndsList.add(info);
                                        Log.d("friendslist", frndsList.toString());

                                    }
                                    final ArrayAdapter<String> arrayAdapater=new ArrayAdapter<String>(ChatsActivity.this,android.R.layout.select_dialog_singlechoice);
                                    for(int i=0;i<frndsList.size();i++){
                                        for(int j=0;j<usersList.size();j++){
                                            if(usersList.get(j).getUserID().equals(frndsList.get(i).getId())){
                                                arrayAdapater.add(usersList.get(j).getFirstName());
                                            }
                                        }

                                    }
                                    final DatabaseReference memref = FirebaseDatabase.getInstance().getReference().child("Trips").child(tpid).child("members");
                                    final DatabaseReference membersref = FirebaseDatabase.getInstance().getReference().child("Trips").child(tpid).child("members");

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
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatsActivity.this);
                                            builder.setTitle("Friends");
                                            builder.setAdapter(arrayAdapater, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, final int which) {
                                                    Log.d("demSelected", String.valueOf(which));

                                                    int Count=1;
                                                    for(int i=0;i<memberslist.size();i++){
                                                        if(memberslist.get(i).getFirstName().equals(arrayAdapater.getItem(which))){
                                                            Toast.makeText(ChatsActivity.this,"Already added to the trip",Toast.LENGTH_SHORT).show();
                                                            Count=2;
                                                        }
                                                    }

                                                    if(Count==1){
                                                        User selected=new User();
                                                        for(int i=0;i<usersList.size();i++){
                                                            if(usersList.get(i).getFirstName().equals(arrayAdapater.getItem(which))){
                                                                selected=usersList.get(i);
                                                                Log.d("Selected Friends to add",selected.getFirstName());
                                                            }
                                                        }
                                                        if(selected!=null){
                                                            User toInsert = new User();
                                                            toInsert.setFirstName(selected.getFirstName());
                                                            toInsert.setLastName(selected.getLastName());
                                                            toInsert.setDpUrl(selected.getDpUrl());
                                                            toInsert.setGender(false);
                                                            toInsert.setUserID(selected.getUserID());
                                                            toInsert.setUserName(selected.getUserName());
                                                            DatabaseReference childRef=memref.push();
                                                            toInsert.setUserID(childRef.getKey());
                                                            childRef.setValue(toInsert);
                                                        }


                                                    }


                                                    //  if(selected.getFirstName()!=m)


                                                }
                                            });
                                            builder.show();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    };
                                    membersref.addValueEventListener(pl);



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            frndRef.addValueEventListener(postListener1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    dbRef.addValueEventListener(postListener);
                }
                return true;
            case R.id.DeleteChat:
                DatabaseReference remRef=mRoot.child("Trips").child(tpid).child("Status");

                statusTrip uS=new statusTrip();
                DatabaseReference childR=remRef.push();
                uS.setId(childR.getKey());
                uS.setStatus("Removed");
                childR.setValue(uS);


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenu = getMenuInflater();
        myMenu.inflate(R.menu.addplaces,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        Intent loggOut = new Intent(ChatsActivity.this,MainActivity.class);
        loggOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loggOut);
    }

    @Override
    public void OnItemClick(final int p) {


        final chatobj toDelete = allChats.get(p);
        mConditionRef = mRoot.child("Trips").child(tpid).child("deleted messages");
        // mConditionRef.child(toDelete.getKey()).removeValue();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = new User();
                currentUser = null;
                usersList = new ArrayList<User>();
                usersList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User info = postSnapshot.getValue(User.class);
                    if (currentEmail.equals(info.getUserID())) {
                        currentUser = info;
                    } else {

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

                mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.hasChild(user.getUid())) {
                    DeletedMessages toInsert = new DeletedMessages();

                    toInsert.setMsgid(toDelete.getKey());
                    toInsert.setDeleteduserid(user.getUid());
                    DatabaseReference childref = mConditionRef.push();
                    toInsert.setMessagedeleteid(childref.getKey());
                    Log.d("demoset",toInsert.toString());
                    childref.setValue(toInsert);


                }
//
                DatabaseReference childRef=mRoot.child("Trips").child(tpid).child("deleted messages");
                ValueEventListener postList=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userslist = new ArrayList<DeletedMessages>();
                        userslist.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DeletedMessages toadd = snapshot.getValue(DeletedMessages.class);
                            Log.d("DemoToAdd", toadd.toString());
                            userslist.add(toadd);

                        }
                       // for(int i=0;i<userslist.size();i++) {


                            if (userslist.get(p).getDeleteduserid().equals(user.getUid())) {
                                allChats.remove(userslist.get(p));
                              Log.d("allchats",userslist.toString())  ;
                            } else {

                            }
                        //}
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };childRef.addValueEventListener(postList);
                    allChats.remove(toDelete);
                cityWeatherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d("demo", toDelete.getMessage());
        //if(currentUser==to)
        allChats.remove(toDelete);
        cityWeatherAdapter.notifyDataSetChanged();

        if (!toDelete.getImageUrl().toString().equals("NA")) {
            storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("chats/images/" + toDelete.getKey() + ".png");

            riversRef.delete().addOnSuccessListener(new OnSuccessListener() {

                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(ChatsActivity.this, " Deleted",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }



    }

            @Override
    public void OnItemCommentsClick(int p) {

        final  int pos = p;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("" +
                "Comments");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  m_Text = input.getText().toString();

                chatobj toupdate = allChats.get(pos);
                //mConditionRef = mRoot.child("CompleteChat");
                toupdate.setComments(toupdate.getComments()+  input.getText().toString());


                ArrayList <comment> coms =  toupdate.getCom();

                if(coms == null)
                {
                    coms = new  ArrayList <comment>();
                }
                coms.add(new  comment(input.getText().toString(),new Date()));
                toupdate.setCom(coms);
                mRoot.child("Trips").child("CompleteChat").child(toupdate.getKey()).setValue(toupdate);

                //mConditionRef.child(toupdate.getKey()).removeValue();
                //allChats.remove(toupdate);
                cityWeatherAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }


}

