package com.example.akshay.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private FirebaseAuth mAuth;

    private static final String TAG = "LoginPage";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private Button logout;
    FirebaseUser user;

    private EditText mEmailField;
    private EditText mPasswordField;
    ProgressDialog mProgress;
    Button mLogin,signUp;


    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private void SetTheAppIcon()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // getSupportActionBar().setLogo(R.drawable.my_con);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


    private void googleLogin()
    {

        SignInButton googleSignIn = (SignInButton) findViewById(R.id.Google_signin);
        googleSignIn.setOnClickListener(MainActivity.this);

        setGooglePlusButtonText(googleSignIn,"Log in with Google");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    protected void setGooglePlusButtonText(SignInButton signInButton,
                                           String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setPadding(0,0,0,0);
                tv.setText(buttonText);
                return;
            }
        }
    }

    private void StoreUserInDB()
    {

        user = FirebaseAuth.getInstance().getCurrentUser();
        mConditionRef = mRoot.child("Users");



        mConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(user.getUid())) {
                    User toInsert = new User();
                    String[] name = user.getDisplayName().split(" ");
                    toInsert.setFirstName(name[0]);
                    toInsert.setLastName(name[1]);
                    toInsert.setDpUrl(user.getPhotoUrl().toString());
                    toInsert.setGender(false);
                    toInsert.setUserID(user.getUid());
                    toInsert.setUserName(user.getEmail());
                    mConditionRef.child(user.getUid()).setValue(toInsert);

                    Intent welcome = new Intent(MainActivity.this,Welcome.class);
                    startActivity(welcome);
                }
                else
                {
                    DataSnapshot toCheck = snapshot.child(user.getUid());
                    User currUser = toCheck.getValue(User.class);
                    Log.d("User Is This",currUser.getFirstName());
                    Intent welcome = new Intent(MainActivity.this,Welcome.class);
                    startActivity(welcome);
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetTheAppIcon();
        //FirebaseAuth.getInstance().signOut();

        mEmailField = (EditText)findViewById(R.id.editTextEmail);
        mPasswordField = (EditText)findViewById(R.id.editTextPassword);
        mLogin = (Button)findViewById(R.id.buttonLogin);
        signUp = (Button)findViewById(R.id.buttonSignUp);

        // logout =(Button)findViewById(R.id.buttonLogOut);

//        logout.setOnClickListener(this);
        signUp.setOnClickListener(this);
        mLogin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {


                    Intent Welcome = new Intent(MainActivity.this,Welcome.class);

                    startActivity(Welcome);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {

                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        googleLogin();


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            StoreUserInDB();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

            }
        }


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }


    private void hideProgressDialog() {
        mProgress.hide();
    }

    private void showProgressDialog() {
        mProgress = new ProgressDialog(MainActivity.this);
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setCancelable(false);
        mProgress.setMessage("Please Wait...");
        mProgress.show();
    }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "You are not authorized",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent Expense = new Intent(MainActivity.this,Welcome.class);
                            startActivity(Expense);
                        }
                        hideProgressDialog();
                    }
                });

    }


    private void signOut() {
        // Firebase sign out
        mAuth.signOut();


        //Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // updateUI(null);
                    }
                });



    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.Google_signin) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            // updateUI(null);
                        }
                    });
            showProgressDialog();
            signInWithGoogle();

        }

        else if(i == R.id.buttonLogin)
        {
            signIn(mEmailField.getText().toString(),mPasswordField.getText().toString());
        }
        else if(i == R.id.buttonSignUp)
        {
            Intent signUp = new Intent(MainActivity.this,SignUp.class);
            startActivity(signUp);
        }
    }
}