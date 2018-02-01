package com.example.akshay.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

/**
 * Created by akshay on 5/1/2017.
 */




public class MyListsAdapter extends RecyclerView.Adapter<MyListsAdapter.TripsHolder>{

    private ArrayList<Trips> allUsers;
    private LayoutInflater inflater;
    private int recourseID;
    private Context mContext;
    private  String userid;


    private ItemClickCallBack1 itemClickCallBack1;

    public interface ItemClickCallBack1
    {
        void OnItemClick(Trips selectedUser);
       // void OnItemMessageIconClick(Trips selectedUser);
        //if next method.
    }


    public MyListsAdapter(ArrayList<Trips> weathersData, Context context, int recourseId, String userID)
    {
        this.inflater = LayoutInflater.from(context);
        this.allUsers = weathersData;
        this.recourseID = recourseId;
        this.mContext = context;
        this.userid = userID;
        this.itemClickCallBack1 = (ItemClickCallBack1) context;

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

            fullName = (TextView) itemView.findViewById(R.id.TripName_TextView1);
            location = (TextView) itemView.findViewById(R.id.TripLocation_TextView1);
            Tripsicon = (ImageView)itemView.findViewById(R.id.imageViewTripIcon1);
            // msgIcon = (ImageView) itemView.findViewById(R.id.imageViewMsgIcon) ;

            container = itemView.findViewById(R.id.eachTripRoot1);

            container.setOnClickListener(this);
            // msgIcon.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.eachTripRoot1:

                    itemClickCallBack1.OnItemClick(allUsers.get(getAdapterPosition()));
                    break;

//                case R.id.commentIcon:
//                    itemClickCallBack.OnItemCommentsClick(getAdapterPosition());
//                    break;

            }
        }
    }

}
