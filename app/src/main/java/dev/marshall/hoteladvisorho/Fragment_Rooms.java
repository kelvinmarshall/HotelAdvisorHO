package dev.marshall.hoteladvisorho;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.marshall.hoteladvisorho.model.Review;
import dev.marshall.hoteladvisorho.model.Rooms;
import dev.marshall.hoteladvisorho.viewholder.ReviewViewHolder;
import dev.marshall.hoteladvisorho.viewholder.RoomViewHolder;

import static dev.marshall.hoteladvisorho.HotelDetails.HotelId;

public class Fragment_Rooms extends Fragment{

    int color;

    public Fragment_Rooms() {
    }
    @SuppressLint("ValidFragment")
    public Fragment_Rooms(int color) {
        this.color = color;
    }

    RecyclerView recyclerrooms;
    RecyclerView.LayoutManager layoutManager;

    Button addroom;


    FirebaseDatabase database;
    DatabaseReference rooms;

    FirebaseRecyclerAdapter<Rooms,RoomViewHolder> room_adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=FirebaseDatabase.getInstance();
        rooms=database.getReference("Rooms");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_fragment__rooms,container,false);

        recyclerrooms= view.findViewById(R.id.recycler_room);
        addroom= view.findViewById(R.id.add_room);
        addroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addroom=new Intent(getContext(),AddRoom.class);
                startActivity(addroom);
            }
        });
        recyclerrooms.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(getContext());
        recyclerrooms.setLayoutManager(layoutManager); 

       String HotelId= HotelDetails.HotelId;
        
        Loadrooms();
        return view;
    }

    private void Loadrooms() {
        room_adapter= new FirebaseRecyclerAdapter<Rooms, RoomViewHolder>(
                Rooms.class,
                R.layout.rooms,
                RoomViewHolder.class
                ,rooms.child(HotelId)
        ) {
            @Override
            protected void populateViewHolder(RoomViewHolder viewHolder, Rooms model, int position) {

                viewHolder.roomprice.setText("KES "+model.getRooprice());
                viewHolder.roomtype.setText(model.getRoomtype());
                viewHolder.capacity.setText(model.getCapacity());
                GlideApp.with(getActivity()).load(model.getImage())
                        .placeholder(R.drawable.ic_local_hotel_black_24dp)
                        .into(viewHolder.imageroom);
               if (!TextUtils.equals( model.getBreakfast(),"true")){
                   viewHolder.breakfast.setVisibility(View.GONE);
               }
               if (! model.getLunch().equals("true")) {
                   viewHolder.lunch.setVisibility(View.GONE);
               }
               if (! model.getDinner().equals("true")) {
                   viewHolder.dinner.setVisibility(View.GONE);
               }
               if (! model.getBooknow().equals("true")) {
                    viewHolder.book.setVisibility(View.GONE);
               }
               if (! model.getCancelation().equals("true")){
                    viewHolder.cancel.setVisibility(View.GONE);
               }
               if (! model.getHotshower().equals("true")) {
                    viewHolder.hot.setVisibility(View.GONE);
               }
               if (! model.getBathtub().equals("true")) {
                    viewHolder.bath.setVisibility(View.GONE);
               }
               if (! model.getTv().equals("true")) {
                    viewHolder.tv.setVisibility(View.GONE);
               }


                final Rooms roomitem=model;

            }
        };
        recyclerrooms.setAdapter(room_adapter);
        room_adapter.notifyDataSetChanged();
    }
}
