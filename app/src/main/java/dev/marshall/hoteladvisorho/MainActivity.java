package dev.marshall.hoteladvisorho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.model.User;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout welcom;
    RelativeLayout loading;

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        next= findViewById(R.id.btnnext);

        welcom= findViewById(R.id.welcome);
        loading= findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);

        showView(welcom);
        hideView(loading);

        //Init paper
        Paper.init(this);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
            }
        });

        //check remember
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user != null && pwd != null) {
            if (!user.isEmpty() && !pwd.isEmpty())
                login(user, pwd);

        }

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

    private void login(final String phone,final String pwd) {

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if (Common.isConnectedToInternet(getBaseContext())) {
           // showView(welcom);
            showView(loading);
            next.setVisibility(View.GONE);

            table_user.addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //check if user exist in database
                    if (dataSnapshot.child(phone).exists()){

                        //Get User information


                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);//set phone
                        if (user.getPassword().equals(pwd)) {
                            {
                                Intent homeIntent = new Intent(MainActivity.this,Home.class);
                                Common.currentHotelOwner = user;
                                startActivity(homeIntent);
                                finish();

                            }
                        } else {
                            new SweetAlertDialog(MainActivity.this)
                                    .setTitleText("Wrong password")
                                    .show();
                        }
                    } else {
                        hideView(loading);
                        next.setVisibility(View.VISIBLE);
                        new SweetAlertDialog(MainActivity.this)
                                .setTitleText("User does not exist in the database.Try creating an account")
                                .show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            new SweetAlertDialog(MainActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops!Error Connecting")
                    .setContentText("Please check your internet connection and try again")
                    .show();

        }
    }

}
