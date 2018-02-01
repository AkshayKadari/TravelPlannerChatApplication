package com.example.akshay.demo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.akshay.demo.EditProfile.REQUEST_IMAGE_GET;

public class TripsActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT = 1;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;
    private User currentuser;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private EditText title;
    private EditText location;
    private User SELECTED_USER;
    private Button createtrip,cncl;
    Uri  url,downloadurl;
    StorageReference memeref;

    String CurrentEmail,photoUrl;
    ImageView iv;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        title=(EditText)findViewById(R.id.editText);
        location=(EditText)findViewById(R.id.editText2);
        createtrip = (Button)findViewById(R.id.button2);
        cncl = (Button)findViewById(R.id.button3);

        memeref = FirebaseStorage.getInstance().getReference();
        cncl.setOnClickListener(new View.OnClickListener() {
          @Override
           public void onClick(View v) {
            Finish();
           }
            });
        iv=(ImageView)findViewById(R.id.imageView);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);

            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();

        createtrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreUserInDB();
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT && resultCode == RESULT_OK){

            // code to retrive the image url and set it to picasso
            Uri uri = data.getData();
            uploadingImageToStorage(uri);

        }


    }
    private void StoreUserInDB()
    {

        user = FirebaseAuth.getInstance().getCurrentUser();
        mConditionRef = mRoot.child("Users");


         if(user!=null) {


             mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot snapshot) {
                     DataSnapshot toCheck = snapshot.child(user.getUid());
                     User currUser = toCheck.getValue(User.class);

                     if (snapshot.hasChild(currUser.getUserID())) {
                         Trips toInsert = new Trips();
                         toInsert.setTitle(title.getText().toString());
                         toInsert.setLocation(location.getText().toString());
                         toInsert.setAdminId(currUser.getUserID());
                         SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                         photoUrl = mSettings.getString("urlvalue", "missing");
                         if (photoUrl.equals("missing")) {

                             photoUrl = resourceToUri(TripsActivity.this, R.drawable.missingpic).toString();

                         }

                         toInsert.setPhoto(photoUrl.toString());
                         //  toInsert.setPhoto(user.getPhotoUrl().toString());
                         Log.d("demoKey", currUser.getUserID());
                         DatabaseReference newRef = mRoot.child("Trips");
                         DatabaseReference childref = newRef.push();
                         toInsert.setTpId(childref.getKey());
                         childref.setValue(toInsert);

                         finish();

                         //  mConditionRef.child(user.getUid()).setValue(toInsert);


                     } else {
                         // DataSnapshot toCheck = snapshot.child(user.getUid());
                         //User currUser = toCheck.getValue(User.class);
                         //Log.d("User Is This",currUser.getFirstName());

                     }
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });
         }
        else
         {
             finish();
         }




    }
    public static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }

    public  void Finish(){
        Intent w = new Intent(TripsActivity.this,Welcome.class);
        startActivity(w);
    }

    public void uploadingImageToStorage(Uri uri){

        Log.d("demo","emtered 2");
        StorageReference filepath = memeref.child("changedphotos").child(uri.getLastPathSegment());

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadurl = taskSnapshot.getDownloadUrl();



                SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("urlvalue",downloadurl.toString());
                editor.apply();

                Picasso.with(TripsActivity.this).load(downloadurl).into(iv);





            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                Toast.makeText(TripsActivity.this,"image upload failed", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
