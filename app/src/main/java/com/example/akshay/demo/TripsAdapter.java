package com.example.akshay.demo;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by akshay on 4/18/2017.
 */



public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripsHolder>{

    private ArrayList<Trips> allUsers;
    private LayoutInflater inflater;
    private int recourseID;
    private Context mContext;
    private  String userid;


    private ItemClickCallBack itemClickCallBack;

    public interface ItemClickCallBack
    {
        void OnItemClick(Trips selectedUser);
        void OnItemLongClick(Trips selectedUser);
        void OnItemMessageIconClick(Trips selectedUser);
        //if next method.
    }


    public TripsAdapter(ArrayList<Trips> weathersData, Context context, int recourseId, String userID)
    {
        this.inflater = LayoutInflater.from(context);
        this.allUsers = weathersData;
        this.recourseID = recourseId;
        this.mContext = context;
        this.userid = userID;
        this.itemClickCallBack = (ItemClickCallBack) context;

    }

    @Override
    public TripsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(this.recourseID,parent,false);
        return new TripsHolder(view);
    }

    @Override
    public void onBindViewHolder(TripsHolder holder, int position) {
        Trips curUser = allUsers.get(position);



        PrettyTime p2 = new PrettyTime();



        holder.location.setText(curUser.getLocation());
        holder.fullName.setText(curUser.getTitle());
        Log.d("demophoto",curUser.getPhoto());
        Picasso.with(mContext).load(curUser.getPhoto()).transform(new CircleTransform()).into(holder.Tripsicon);



    }

    @Override
    public int getItemCount() {
        return allUsers.size();
    }





    class TripsHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView fullName,location;
        private ImageView Tripsicon,msgIcon;

        private View container;



        public TripsHolder(View itemView) {
            super(itemView);

            fullName = (TextView) itemView.findViewById(R.id.TripName_TextView);
            location = (TextView) itemView.findViewById(R.id.TripLocation_TextView);
            Tripsicon = (ImageView)itemView.findViewById(R.id.imageViewTripIcon);
           // msgIcon = (ImageView) itemView.findViewById(R.id.imageViewMsgIcon) ;

            container = itemView.findViewById(R.id.eachTripRoot);

            container.setOnClickListener(this);
           // msgIcon.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.eachTripRoot:

                    itemClickCallBack.OnItemClick(allUsers.get(getAdapterPosition()));
                    itemClickCallBack.OnItemLongClick(allUsers.get(getAdapterPosition()));
                    break;
               case R.id.imageViewTripIcon:

                    itemClickCallBack.OnItemMessageIconClick(allUsers.get(getAdapterPosition()));
                    break;
//                case R.id.commentIcon:
//                    itemClickCallBack.OnItemCommentsClick(getAdapterPosition());
//                    break;

            }
        }
    }

}
