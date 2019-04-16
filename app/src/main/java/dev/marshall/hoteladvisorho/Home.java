package dev.marshall.hoteladvisorho;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import dev.marshall.hoteladvisorho.common.Common;
import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AHBottomNavigation.OnTabSelectedListener {

    TextView txtfullname;
    ImageView imageView;
    AHBottomNavigation bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Set name for user
        View headerView = navigationView.getHeaderView(0);
        imageView= headerView.findViewById(R.id.imageView);
        txtfullname = headerView.findViewById(R.id.txtfullName);

        GlideApp.with(this).load(Common.currentHotelOwner.getImage())
                .placeholder(R.drawable.ic_person_black_24dp)
                .into(imageView);
        txtfullname.setText(Common.currentHotelOwner.getName());

        //bottom bar
        bottomNavigation = findViewById(R.id.myBottomNavigation_ID);
        bottomNavigation.setOnTabSelectedListener(this);
        this.createNavItems();


    }

    private void createNavItems() {
        //CREATE ITEMS
        AHBottomNavigationItem homeItem = new AHBottomNavigationItem("Home", R.drawable.ic_home_black_24dp);
        AHBottomNavigationItem notificationsItem = new AHBottomNavigationItem("Notifications", R.drawable.ic_notifications_black_24dp);
        AHBottomNavigationItem tipstem = new AHBottomNavigationItem("Tips", R.drawable.ic_library_books_black_24dp);
        AHBottomNavigationItem profileitem = new AHBottomNavigationItem("Me", R.drawable.ic_person_black_24dp);

        //ADD THEM to bar
        bottomNavigation.addItem(homeItem);
        bottomNavigation.addItem(notificationsItem);
        bottomNavigation.addItem(tipstem);
        bottomNavigation.addItem(profileitem);
        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#00a680"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        //set properties
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#fefefe"));

        //set current item
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setTranslucentNavigationEnabled(true);
        // Customize notification (title, background, typeface)
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));
// Add or remove notification for each item
        bottomNavigation.setNotification("1", 3);
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        //show fragments
        if (position == 0) {
            FragmentHome fragmentHome = new FragmentHome();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragmentHome).commit();
        } else if (position == 1) {
            FragmentAlerts fragmentAlerts = new FragmentAlerts();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragmentAlerts).commit();
        } else if (position == 2) {
            FragmentTips fragmentTips = new FragmentTips();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragmentTips).commit();
        } else if (position == 3) {
            FragmentProfile fragmentProfile = new FragmentProfile();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragmentProfile).commit();
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_pay)
        {
            Intent pay=new Intent(Home.this,Payment.class);
            startActivity(pay);
        }
        if (id==R.id.action_logout){
            //Logout
            Intent signIn=new Intent(Home.this,SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_booking) {
            // Handle the camera action
        } else if (id == R.id.nav_suscribe) {
            Intent pay=new Intent(Home.this,Payment.class);
            startActivity(pay);

        }  else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_log_out) {

        }
        else if (id==R.id.nav_about){

        }
        else if (id==R.id.nav_help){

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
