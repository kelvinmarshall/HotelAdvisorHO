package dev.marshall.hoteladvisorho;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisorho.Interface.ItemClickListener;
import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.model.*;
import dev.marshall.hoteladvisorho.viewholder.HotelViewHolder;
import dev.marshall.hoteladvisorho.viewholder.hotelwebViewHolder;
import info.hoang8f.widget.FButton;

/**
 * Created by Marshall on 04/03/2018.
 */

public class FragmentHome extends Fragment {

    RecyclerView recyclerView,recycler_review;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<MyHotels,hotelwebViewHolder> adapter;
    FirebaseRecyclerAdapter<MyHotels,HotelViewHolder> review_adapter;

    FButton myhotel,addhotel,reviews,website,sitevisitors,contact;


    //Firebase
    FirebaseDatabase database;
    DatabaseReference myhotels,userreviews,subscription;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database=FirebaseDatabase.getInstance();
        myhotels=database.getReference("Hotels");
        userreviews=database.getReference("Review");
        subscription=database.getReference("Subscriptions");

    }

    public static FragmentHome newInstance(){
        FragmentHome fragmentHome= new FragmentHome();
        return fragmentHome;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View  myFragment = inflater.inflate(R.layout.home, container, false);

        getActivity().setTitle("Home");
        addhotel= myFragment.findViewById(R.id.addHotel);
        reviews= myFragment.findViewById(R.id.reviews);
        website= myFragment.findViewById(R.id.website);
        sitevisitors= myFragment.findViewById(R.id.visitors);
        contact= myFragment.findViewById(R.id.contact);
        myhotel= myFragment.findViewById(R.id.myhotels);

        sitevisitors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sitevisitors=new Intent(getActivity(),Webvisitors.class);
                startActivity(sitevisitors);
            }
        });

        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               reviews();
            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecthotelweb();
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contact=new Intent(getActivity(), Contacts.class);
                startActivity(contact);
            }
        });


        addhotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               subscription.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       if (dataSnapshot.child(Common.currentHotelOwner.getPhone()).exists())
                       {
                           Intent addhotel=new Intent(getActivity(), AddHotel.class);
                           startActivity(addhotel);
                       }
                       else {
                           AlertDialog.Builder mdialog=new AlertDialog.Builder(getActivity());
                           mdialog.setMessage("You haven't subscribed to this service yet.Click Ok button to go to payment page ");
                           mdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {

                               }
                           });
                           mdialog.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   Intent pay=new Intent(getActivity(), Payment.class);
                                   startActivity(pay);
                               }
                           });
                           mdialog.show();
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
            }
        });
        myhotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myhotel=new Intent(getActivity(),Myhotel.class);
                startActivity(myhotel);
            }
        });


        return myFragment;

    }

    private void reviews() {
        AlertDialog.Builder alertdialog= new AlertDialog.Builder(getActivity());
        alertdialog.setTitle("Select to see Review");
        LayoutInflater inflater = this.getLayoutInflater();
        View review = inflater.inflate(R.layout.user_reviews,null);

        //Init view
        recycler_review= review.findViewById(R.id.recycler_reviews);
        recycler_review.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());
        recycler_review.setLayoutManager(layoutManager);

        alertdialog.setView(review);

        loadhotellist(Common.currentHotelOwner.getPhone());
        alertdialog.show();
    }

    private void loadhotellist(String phone) {
        review_adapter=new FirebaseRecyclerAdapter<MyHotels, HotelViewHolder>(
                MyHotels.class,
                R.layout.reviewedhotellist,
                HotelViewHolder.class,
                myhotels.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(HotelViewHolder viewHolder, MyHotels model, int position) {
                viewHolder.Hotelname.setText(model.getName());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent webview = new Intent(getActivity(),Reviews.class);
                        webview.putExtra("hotelId",review_adapter.getRef(position).getKey());
                        startActivity(webview);
                    }
                });
            }
        };

        recycler_review.setAdapter(review_adapter);
        review_adapter.notifyDataSetChanged();
    }

    private void selecthotelweb() {
        AlertDialog.Builder alertdialog= new AlertDialog.Builder(getActivity());
        alertdialog.setTitle("Select hotel to vist its website");
        LayoutInflater inflater = this.getLayoutInflater();
        View selectweb = inflater.inflate(R.layout.hotelweblist,null);

        //Init view
        recyclerView= selectweb.findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        alertdialog.setView(selectweb);

        loadweblist(Common.currentHotelOwner.getPhone());
        alertdialog.show();
    }

    private void loadweblist(String phone) {
        adapter=new FirebaseRecyclerAdapter<MyHotels, hotelwebViewHolder>(
                MyHotels.class,
                R.layout.hotelweblistlayout,
                hotelwebViewHolder.class,
                myhotels.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(hotelwebViewHolder viewHolder, MyHotels model, int position) {
                viewHolder.Hotelname.setText(model.getName());
                viewHolder.Hotelurl.setText(model.getUrl());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent webview = new Intent(getActivity(),Website.class);
                        webview.putExtra("hotelId",adapter.getRef(position).getKey());
                        startActivity(webview);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
