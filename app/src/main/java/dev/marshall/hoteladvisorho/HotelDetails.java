package dev.marshall.hoteladvisorho;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import dev.marshall.hoteladvisorho.model.MyHotels;

public class HotelDetails extends FragmentActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    ViewPager viewPager;
    TabLayout tablayout;


    public static String HotelId="";

    MyHotels  currentHotel;

    ImageView Imagehotel;


    FirebaseDatabase database;
    DatabaseReference hotels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);
        //firebase
        database= FirebaseDatabase.getInstance();
        hotels=database.getReference("Hotels");

        Imagehotel= findViewById(R.id.hotel_image);
        collapsingToolbarLayout= findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        viewPager= findViewById(R.id.viewpager);
        tablayout= findViewById(R.id.tablayout);
        //create and set ViewPager adapter
        setupViewPager(viewPager);
        tablayout.setupWithViewPager(viewPager);

        //get productId from Intent
        if(getIntent() !=null)
            HotelId=getIntent().getStringExtra("hotelId");
        if(!HotelId.isEmpty())
        {
            getDetailHotel(HotelId);
        }


        //change selected tab when viewpager changed page
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));

        //change viewpager page when tab selected
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:

                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentHotelInfo(
                ContextCompat.getColor(this, R.color.white)), "Information".toLowerCase());
        adapter.addFrag(new Fragment_Rooms(
                ContextCompat.getColor(this, R.color.white)), "Rooms".toLowerCase());
        viewPager.setAdapter(adapter);
    }


    private void getDetailHotel(String hotelId) {
        hotels.child(HotelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentHotel=dataSnapshot.getValue(MyHotels.class);
                collapsingToolbarLayout.setTitle(currentHotel.getName());

                //set image
                GlideApp.with(getBaseContext()).load(currentHotel.getImage())
                        .into(Imagehotel);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

