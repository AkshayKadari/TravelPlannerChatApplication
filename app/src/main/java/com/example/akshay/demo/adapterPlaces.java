package com.example.akshay.demo;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by akshay on 4/27/2017.
 */

public class adapterPlaces extends RecyclerView.Adapter<adapterPlaces.myViewHolder>  {

    placepicker activity;
    private final LayoutInflater inflater;
    private Context mContext;
    List<PlaceDetails> mData;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


    public adapterPlaces(Context context, List<PlaceDetails> Data){
        inflater = LayoutInflater.from(context);
        mContext=context;
        mData=Data;
        activity = (placepicker)context;

    }
    public Context getContext()
    {
        return mContext;
    }

    @Override
    public adapterPlaces.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.places,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final adapterPlaces.myViewHolder holder, final int position) {

        final PlaceDetails pLL = mData.get(position);
        holder.title.setText(pLL.getPlacename());
        holder.edit.setPaintFlags(holder.edit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.remove.setPaintFlags(holder.remove.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.changethelocation(pLL.getPid());

            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference placeRef=dbRef.child("Trips").child(pLL.getTpid()).child("Places");
                placeRef.child(pLL.getTpid()).removeValue();
            }
        });


    }
    @Override
    public int getItemCount() {
        return mData.size();
    }




    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title,edit,remove;

        public myViewHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.tvPlaceName);
            edit=(TextView)itemView.findViewById(R.id.placeEdit);
            remove=(TextView)itemView.findViewById(R.id.tvRemove);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
        }
    }

    static public interface placepicker{

        public void changethelocation(String id);

    }

}