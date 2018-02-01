package com.example.akshay.demo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tripslist.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tripslist#newInstance} factory method to
 * create an instance of this fragment.
 */

public class Tripslist extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private TripsAdapter tripsAdapter;
    private ArrayList<Trips> allTrips = new ArrayList<Trips>();
    private ArrayList<Trips> completetrips = new ArrayList<>();
    private  ArrayList<AddFriend> friends = new ArrayList<>();
    RecyclerView recyclerReq;
    private FirebaseUser user;
    DatabaseReference mRoot  = FirebaseDatabase.getInstance().getReference();
    DatabaseReference  mConditionRef ;
    DatabaseReference mFriendRef;


    LinearLayout noTripsDiv ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    public Tripslist() {

    }


    public static Users newInstance(String param1, String param2) {
        Users fragment = new Users();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        allTrips = new ArrayList<Trips>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.TripsView);


        noTripsDiv = (LinearLayout)getActivity().findViewById(R.id.NoTripsDiv);
        User currUser = new User();
       String currentuserid =user.getUid();
        mConditionRef = mRoot.child("Trips");


        mConditionRef.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Trips toadd = dataSnapshot.getValue(Trips.class);
                 Log.d("toadd",toadd.toString());
                if(!toadd.getTpId().equals(user.getUid()) ) {
                    allTrips.add(toadd);
                }
                Log.d("XXXXXX",allTrips.toString());
                mFriendRef = mRoot.child("Users").child(user.getUid()).child("Friends");
                mFriendRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        AddFriend addfrnd = dataSnapshot.getValue(AddFriend.class);
                        if(!addfrnd.getId().equals(user.getUid())){
                            friends.add(addfrnd);
                        }

                        if(!allTrips.isEmpty()&& !friends.isEmpty()) {

                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                            tripsAdapter = new TripsAdapter(filterFrndsTrips(allTrips,friends),getActivity(),R.layout.item_trip,user.getUid());
                            Log.d("filtered",filterFrndsTrips(allTrips,friends).toString());
                            tripsAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(tripsAdapter);
                            Log.d("1","1");

                        }
                        else
                        {
                            noTripsDiv.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

               if(allTrips.size()!=0)
                {
                    noTripsDiv.setVisibility(View.GONE);
                }
                else {
                    noTripsDiv.setVisibility(View.VISIBLE);

                }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tripslist, container, false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public ArrayList<Trips> filterFrndsTrips(ArrayList<Trips> T,ArrayList<AddFriend> F) {

        if(!T.isEmpty() && !F.isEmpty()) {
            ArrayList<Trips> result = new ArrayList<Trips>();
            for (int i = 0; i < T.size(); i++) {
                for (int j = 0; j < F.size(); j++) {
                    if (T.get(i).getAdminId().equals(F.get(j).getId())) {
                        if(!result.contains(T.get(i)))
                        result.add(T.get(i));
                    }
                }

            }

            return result;
        }
        return null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTripsInteractionListener(Uri uri);
    }
}
