package com.example.akshay.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by akshay on 4/21/2017.
 */


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.myViewHolder>  {

    private final LayoutInflater inflater;
    private Context mContext;
    List<User> mData;
    AddFriend reqUserDb,senUserDb,deletedb,otherdb;
    String CurrentEmail;
    User curntUser;
    private DatabaseReference dbRef;
    private DatabaseReference frndRecRef,frndSenRef;
    private FirebaseAuth mAuth;


    public RequestAdapter(Context context, List<User> Data){
        inflater = LayoutInflater.from(context);
        mContext=context;
        mData=Data;

    }
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public RequestAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.itemunfriend,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RequestAdapter.myViewHolder holder, final int position)

    {

        mAuth = FirebaseAuth.getInstance();
        final User requestedUser=mData.get(position);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            CurrentEmail=user.getUid();

        }

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curntUser = new User();
                curntUser = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User FBCity = postSnapshot.getValue(User.class);
                    if (CurrentEmail.equals(FBCity.getUserID())) {
                        curntUser = FBCity;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbRef.addValueEventListener(postListener);
        holder.fullName.setText(requestedUser.getFullName());
        Picasso.with(mContext).load(requestedUser.CheckAndGetDpUrl()).placeholder(requestedUser.isGender() ? R.drawable.u_female :R.drawable.u_male)
                .transform(new CircleTransform()).into(holder.userIcon);
      // holder.unfriendicon.setVisibility(View.GONE);
        holder.msgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAsFriend(requestedUser);
               // delete(requestedUser);
                holder.msgIcon.setImageResource(R.drawable.added);
                holder.unfriendicon.setVisibility(View.VISIBLE);
            }
        });
        holder.unfriendicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext().getApplicationContext(),"clicked",Toast.LENGTH_LONG).show();
                //delete(requestedUser);
            }
        });

    }

//    public void delete(final User frnd){
//        Log.d("demodelete","came");
//        final DatabaseReference dref = dbRef.child(curntUser.getUserID()).child("Friends");
//        ValueEventListener deletelistenerref = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                deletedb = new AddFriend();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    AddFriend FBCity = postSnapshot.getValue(AddFriend.class);
//                    if(FBCity.getId().equals(frnd.getUserID())){
//                        deletedb= FBCity;
//
//                    }
//                }
//
//                ValueEventListener deletelistenref = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        otherdb = new AddFriend();
//                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                            AddFriend FBCity = postSnapshot.getValue(AddFriend.class);
//                            if(FBCity.getId().equals(frnd.getUserID())){
//                                otherdb= FBCity;
//
//                            }
//                        }
//                        Log.d("demoDeletedFriend",otherdb.toString());
//                        dbRef.child(curntUser.getUserID()).child("Friends").child(deletedb.getReqId()).removeValue();
//
//                        dbRef.child(otherdb.getId()).child("Friends").child(otherdb.getReqId()).removeValue();
//                        // dbRef.child(deletedb.getId()).child("Friends").child(curntUser.get()).removeValue();
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                };
//                dref.addValueEventListener(deletelistenref);
//                Log.d("demoDeletedFriend",deletedb.toString());
//               // dbRef.child(deletedb.getId()).child("Friends").child(curntUser.get()).removeValue();
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        dref.addValueEventListener(deletelistenerref);
//    }



    public void addAsFriend(final User reqU){

        frndRecRef = dbRef.child(curntUser.getUserID()).child("Request_Received");
        ValueEventListener postListenerRec = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reqUserDb = new AddFriend();
                reqUserDb = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AddFriend FBCity = postSnapshot.getValue(AddFriend.class);
                    if(FBCity.getId().equals(reqU.getUserID())){
                        reqUserDb=FBCity;
                    }
                }


                if(reqUserDb!=null) {
                    frndSenRef = dbRef.child(reqUserDb.getId()).child("Request_Send");
                    ValueEventListener postListenerSen = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            senUserDb = new AddFriend();
                            senUserDb = null;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                AddFriend FBCity = postSnapshot.getValue(AddFriend.class);
                                if (FBCity.getId().equals(curntUser.getUserID())) {
                                    senUserDb = FBCity;
                                }
                            }
                            if(senUserDb!=null) {
                                Log.d("demoFrndReq", reqUserDb.toString());
                                Log.d("demoFrndSender", senUserDb.toString());

                                DatabaseReference delRef = dbRef.child(reqUserDb.getId()).child("Request_Send");
                                delRef.child(senUserDb.getReqId()).removeValue();

                                AddFriend frndSen = new AddFriend();
                                frndSen.setId(senUserDb.getId());
                                frndSen.setStatus("Approved");
                                DatabaseReference new1Ref = dbRef.child(reqUserDb.getId()).child("Friends");
                                final DatabaseReference child1Ref = new1Ref.push();
                                frndSen.setReqId(child1Ref.getKey());
                                child1Ref.setValue(frndSen);

                                DatabaseReference del2Ref = dbRef.child(curntUser.getUserID()).child("Request_Received");
                                del2Ref.child(reqUserDb.getReqId()).removeValue();

                                AddFriend frndRec = new AddFriend();
                                frndRec.setId(reqUserDb.getId());
                                frndRec.setStatus("Approved");
                                DatabaseReference new2Ref = dbRef.child(curntUser.getUserID()).child("Friends");
                                final DatabaseReference child2Ref = new2Ref.push();
                                frndRec.setReqId(child2Ref.getKey());
                                child2Ref.setValue(frndRec);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    frndSenRef.addValueEventListener(postListenerSen);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        frndRecRef.addValueEventListener(postListenerRec);

    }
    @Override
    public int getItemCount() {
        return mData.size();
    }




    


    class myViewHolder extends RecyclerView.ViewHolder {
        private TextView fullName;
        private ImageView userIcon,msgIcon;
        private  ImageView unfriendicon;
        private View container;


        public myViewHolder(View itemView) {
            super(itemView);

            fullName = (TextView) itemView.findViewById(R.id.Name_TextView1);
            userIcon = (ImageView)itemView.findViewById(R.id.imageViewUserIcon1);
            msgIcon = (ImageView) itemView.findViewById(R.id.imageViewfriendIcon) ;
            unfriendicon = (ImageView)itemView.findViewById(R.id.unfriend1);
            msgIcon.setImageResource(R.drawable.friends);
            //container = itemView.findViewById(R.id.eachUserRoot);

            //container.setOnClickListener(this);



        }


    }

}
