package dev.marshall.hoteladvisorho;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.marshall.hoteladvisorho.model.MyHotels;
import dev.marshall.hoteladvisorho.model.Review;
import dev.marshall.hoteladvisorho.viewholder.ReviewViewHolder;

public class Reviews extends AppCompatActivity {
    TextView availablereviews;

    String HotelId="";
    TextView hotelname_reviewed;

    RecyclerView recycler_review;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference reviews;

    FirebaseRecyclerAdapter<Review,ReviewViewHolder> review_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        availablereviews= findViewById(R.id.available);

        if (getIntent() !=null){
            HotelId=getIntent().getStringExtra("hotelId");
        }

        //load reviews
        recycler_review= findViewById(R.id.recycler_reviews);
        recycler_review.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(Reviews.this);
        recycler_review.setLayoutManager(layoutManager);
        hotelname_reviewed=(TextView)findViewById(R.id.Hotelname);
        //firebase
        database=FirebaseDatabase.getInstance();
        reviews=database.getReference("Reviews");
        reviews.child(HotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MyHotels hotels=dataSnapshot.getValue(MyHotels.class);

                hotelname_reviewed.setText(hotels.getName());
                availablereviews.setText(dataSnapshot.getChildrenCount()+" available");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        loadreviews(HotelId);
    }

    private void loadreviews(String hotelId) {
        review_adapter= new FirebaseRecyclerAdapter<Review, ReviewViewHolder>(
                Review.class,
                R.layout.user_review_layout,
                ReviewViewHolder.class
                ,reviews.orderByChild("hotelId").equalTo(hotelId)
        ) {
            @Override
            protected void populateViewHolder(ReviewViewHolder viewHolder, Review model, int position) {
                viewHolder.Reviewername.setText(model.getUsername());
                viewHolder.Review.setText(model.getReview());
                GlideApp.with(Reviews.this).load(model.getImage())
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(viewHolder.Reviewerimages);
                final Review reviewitem=model;
            }

        };
        recycler_review.setAdapter(review_adapter);
        review_adapter.notifyDataSetChanged();
    }
}
