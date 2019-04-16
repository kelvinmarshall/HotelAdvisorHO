package dev.marshall.hoteladvisorho;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.model.MyHotels;

public class Website extends AppCompatActivity {

    String HotelId="";
    WebView booksite;

    FirebaseDatabase db;
    DatabaseReference databaseReference;
    LinearLayout webvieLayout;
    LinearLayout loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        webvieLayout= findViewById(R.id.webview);
        loadingProgress= findViewById(R.id.redirect);
        loadingProgress.setVisibility(View.INVISIBLE);

        db=FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Hotels");

        booksite= findViewById(R.id.webviewbook);


        //get hotelId from Intent
        if(getIntent() !=null)
            HotelId=getIntent().getStringExtra("hotelId");
        //if(!HotelId.isEmpty())
        booksite.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                hideView(webvieLayout);//hide web
                showView(loadingProgress);//show progress loading layout
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Page loading finished
                showView(webvieLayout);//hide web
                hideView(loadingProgress);//show progress loading layout
            }
        });

        WebSettings webSettings=booksite.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        booksite.getSettings().getLoadsImagesAutomatically();
        databaseReference.child(HotelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MyHotels hotels=dataSnapshot.getValue(MyHotels.class);

                String url=hotels.getUrl();

                booksite.loadUrl(url);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void showView(View... views) {
        for (View v: views){
            v.setVisibility(View.VISIBLE);
        }
    }
    private void hideView(View... views) {
        for (View v: views){
            v.setVisibility(View.INVISIBLE);
        }
    }


}
