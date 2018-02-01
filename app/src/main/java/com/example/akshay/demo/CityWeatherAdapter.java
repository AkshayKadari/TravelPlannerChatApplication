package com.example.akshay.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by akshay on 4/18/2017.
 */

public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.CityWeatherHolder>{

    private ArrayList<chatobj> dayWeathers;
    private LayoutInflater inflater;
    private int recourseID;
    private Context mContext;
    private  String userid;


    private ItemClickCallBack itemClickCallBack;

    public interface ItemClickCallBack
    {
        void OnItemClick(int p);
        void OnItemCommentsClick(int p);
        //if next method.
    }


    public CityWeatherAdapter(ArrayList<chatobj> weathersData, Context context, int recourseId,String userID)
    {
        this.inflater = LayoutInflater.from(context);
        this.dayWeathers = weathersData;
        this.recourseID = recourseId;
        this.mContext = context;
        this.userid = userID;
        this.itemClickCallBack = (ItemClickCallBack) context;

    }

    @Override
    public CityWeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(this.recourseID,parent,false);
        return new CityWeatherHolder(view);
    }

    @Override
    public void onBindViewHolder(CityWeatherHolder holder, final int position) {
        chatobj currchat = dayWeathers.get(position);
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

        PrettyTime p2 = new PrettyTime();
        System.out.println(p2.format(new Date()));

        // holder.comments.setText(currchat.getComments().toString()+ "\n");
        if(!currchat.getMessage().toString().equals("")) {
            holder.date.setText(p2.format(currchat.getWhen()));
            holder.message.setText(currchat.getMessage().toString());
            holder.usrrname.setText(currchat.getFulname().toString());
            holder.icon.setVisibility(View.INVISIBLE);
        }
        if(currchat.getMessage().toString().equals("")){
            Log.d("yo","its empty");        }
        holder.deleteicon.setVisibility(View.INVISIBLE);
        if(userid.equals( currchat.getUserID()))
        {
            holder.deleteicon.setVisibility(View.VISIBLE);
        }

        if(!currchat.getImageUrl().equals("NA")) {
            Picasso.with(mContext).load(currchat.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.icon);
            holder.date.setText(p2.format(currchat.getWhen()));
            holder.icon.setVisibility(View.VISIBLE);
            holder.usrrname.setText(currchat.getFulname().toString());
            holder.chatlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dayWeathers.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }


        if(currchat.getCom()!= null) {
            String todi ="";
            for (comment c : currchat.getCom()
                    ) {

                PrettyTime p = new PrettyTime();
                System.out.println(p.format(new Date()));
                //prints: “moments from now”

                System.out.println(p.format(new Date(c.getDate().toString() + 1000*60*10)));


                todi =  todi  +" \n" + c.getComment() + "       " + p.format(new Date(c.getDate().toString() ));
                holder.comments.setText(todi);
            }
        }
        else {

            holder.comments.setText("");

        }
        //  String url= BASE_IMAGE_URL + dayWeather.getAvgIcon() + ".png";
        //  Picasso.with(mContext).load(url).placeholder(R.drawable.loading).into(holder.icon);


    }

    @Override
    public int getItemCount() {
        return dayWeathers.size();
    }





    class CityWeatherHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView usrrname,message,date,comments;
        private ImageView icon,deleteicon,commentIcon;
        private LinearLayout chatlayout;




        public CityWeatherHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.dateTimeText);
            usrrname = (TextView) itemView.findViewById(R.id.userText);
            message = (TextView) itemView.findViewById(R.id.messageText);
            comments = (TextView) itemView.findViewById(R.id.commentViewText);
            icon = (ImageView)itemView.findViewById(R.id.msgIcon);
            deleteicon = (ImageView)itemView.findViewById(R.id.deleteIcon);
            commentIcon = (ImageView)itemView.findViewById(R.id.commentIcon);
            chatlayout = (LinearLayout)itemView.findViewById(R.id.chatlayout);
//            temperature = (TextView) itemView.findViewById(R.id.textViewTemperature);
//            icon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
//            container = itemView.findViewById(R.id.cityWeatherRoot);
            deleteicon.setOnClickListener(this);
            commentIcon.setOnClickListener(this);


            //  container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.deleteIcon:

                    itemClickCallBack.OnItemClick(getAdapterPosition());
                    break;

                case R.id.commentIcon:
                    itemClickCallBack.OnItemCommentsClick(getAdapterPosition());
                    break;


            }
        }
    }

}

