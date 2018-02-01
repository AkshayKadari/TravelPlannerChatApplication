package com.example.akshay.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

/**
 * Created by akshay on 4/16/2017.
 */


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{

    private ArrayList<User> allUsers;
    private LayoutInflater inflater;
    private int recourseID;
    private Context mContext;
    private  String userid;

    User currentUser;
    ArrayList<AddFriend> frndsList;
    AddFriend deletedb,otherdb;
    ArrayList<AddFriend> reqList;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    String currentEmail;



    private ItemClickCallBack itemClickCallBack;

    public interface ItemClickCallBack
    {
        void OnItemClick(User selectedUser);
        void OnItemMessageIconClick(User selectedUser);
        //if next method.
    }


    public UserAdapter(ArrayList<User> weathersData, Context context, int recourseId, String userID)
    {
        this.inflater = LayoutInflater.from(context);
        this.allUsers = weathersData;
        this.recourseID = recourseId;
        this.mContext = context;
        this.userid = userID;
        this.itemClickCallBack = (ItemClickCallBack) context;

    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(this.recourseID,parent,false);
        UserHolder holder=new UserHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        final User curUser = allUsers.get(position);
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
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User info = postSnapshot.getValue(User.class);
                        if (currentEmail.equals(info.getUserID())) {
                            currentUser = info;
                        }

                    }
                    DatabaseReference frndRef=dbRef.child(currentUser.getUserID()).child("Friends");
                    ValueEventListener postListener1=new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            frndsList=new ArrayList<AddFriend>();
                            frndsList.clear();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                AddFriend info = postSnapshot.getValue(AddFriend.class);
                                frndsList.add(info);
                                Log.d("friendslist",frndsList.toString());

                            }
                            final DatabaseReference reqRef=dbRef.child(currentUser.getUserID()).child("Request_Send");
                            ValueEventListener postListener2=new ValueEventListener() {


                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    reqList=new ArrayList<AddFriend>();
                                    reqList.clear();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        AddFriend info = postSnapshot.getValue(AddFriend.class);
                                        reqList.add(info);

                                    }
                                    if(compareList(frndsList,curUser)){
                                        holder.msgIcon.setImageResource(R.drawable.added);
                                        holder.msgIcon.setEnabled(false);
                                        holder.msgIcon.setVisibility(View.INVISIBLE);
                                    }
                                    else if(compareList(reqList,curUser)){
                                        holder.msgIcon.setImageResource(R.drawable.addfriend);
                                        holder.msgIcon.setEnabled(false);
                                        holder.msgIcon.setVisibility(View.INVISIBLE);

                                    }
                                    else{
                                        holder.msgIcon.setImageResource(R.drawable.addfriend);
                                        holder.msgIcon.setEnabled(true);
                                        holder.msgIcon.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                sendRequest(curUser);

                                            }
                                        });

                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            reqRef.addValueEventListener(postListener2);



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
        holder.fullName.setText(curUser.getFullName());
        Picasso.with(mContext).load(curUser.CheckAndGetDpUrl()).placeholder(curUser.isGender() ? R.drawable.u_female :R.drawable.u_male)
                .transform(new CircleTransform()).into(holder.userIcon);
        holder.unfriendicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("demoRemove","came");

                delete(curUser);
            }
        });
    }



    public void delete(final User frnd) {
        Log.d("demoFriendDetail",frnd.toString());
        final DatabaseReference dref = dbRef.child(currentUser.getUserID()).child("Friends");
        ValueEventListener deletelistenerref = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                deletedb = new AddFriend();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    AddFriend FBCity = postSnapshot.getValue(AddFriend.class);
                    if (FBCity.getId().equals(frnd.getUserID())) {
                        deletedb = FBCity;

                    }
                }
               final DatabaseReference dInnerRef=dbRef.child(frnd.getUserID()).child("Friends");
                ValueEventListener deletelistenref = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        otherdb = new AddFriend();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            AddFriend FBCity = postSnapshot.getValue(AddFriend.class);
                            if (FBCity.getId().equals(currentUser.getUserID())) {
                                otherdb = FBCity;

                            }
                        }
                        if(deletedb.getReqId()!=null&&otherdb.getReqId()!=null) {


                            dref.child(deletedb.getReqId()).removeValue();
                            dInnerRef.child(otherdb.getReqId()).removeValue();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                dInnerRef.addValueEventListener(deletelistenref);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        dref.addValueEventListener(deletelistenerref);

    }

    public boolean compareList(ArrayList<AddFriend> Frnds,User user){
        for(int i=0;i<Frnds.size();i++)
        {
            if(Frnds.get(i).getId().equals(user.getUserID())){
                return true;
            }
        }

        return false;
    }
    public void sendRequest(User u){

        AddFriend uR=new AddFriend();
        uR.setStatus("Request sent");
        uR.setId(u.getUserID());
        DatabaseReference newRef=dbRef.child(currentUser.getUserID()).child("Request_Send");
        final DatabaseReference childRef=newRef.push();
        uR.setReqId(childRef.getKey());
        childRef.setValue(uR);

        AddFriend uR1=new AddFriend();
        uR1.setId(currentUser.getUserID());
        uR1.setStatus("Pending");
        DatabaseReference new1Ref = dbRef.child(u.getUserID()).child("Request_Received");
        final DatabaseReference childRef1=new1Ref.push();
        uR1.setReqId(childRef1.getKey());
        childRef1.setValue(uR1);

    }
    @Override
    public int getItemCount() {
        return allUsers.size();
    }





    class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
         TextView fullName;
         ImageView userIcon,msgIcon,unfriendicon;
         View container;



        public UserHolder(View itemView) {
            super(itemView);

            fullName = (TextView) itemView.findViewById(R.id.Name_TextView);
            userIcon = (ImageView)itemView.findViewById(R.id.imageViewUserIcon);
            msgIcon = (ImageView) itemView.findViewById(R.id.imageViewMsgIcon) ;
            unfriendicon = (ImageView) itemView.findViewById(R.id.imageView2) ;

            container = itemView.findViewById(R.id.eachUserRoot);

            container.setOnClickListener(this);
            msgIcon.setOnClickListener(this);
            unfriendicon.setOnClickListener(this);



        }


            @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.eachUserRoot:

                    itemClickCallBack.OnItemClick(allUsers.get(getAdapterPosition()));
                    break;
            /*    case R.id.imageViewMsgIcon:

                    itemClickCallBack.OnItemMessageIconClick(allUsers.get(getAdapterPosition()));
                    break;*/
//                case R.id.commentIcon:
//                    itemClickCallBack.OnItemCommentsClick(getAdapterPosition());
//                    break;

            }
        }
    }

}
