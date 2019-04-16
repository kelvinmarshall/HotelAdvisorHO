package dev.marshall.hoteladvisorho;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.model.User;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    Button sign_up,sign_in;
    EditText edtphone,edtpassword;
    TextView forgot;
    com.rey.material.widget.CheckBox chbRemember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sign_up= findViewById(R.id.btSigUp);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup=new Intent(SignIn.this,Phone_Auth.class);
                startActivity(signup);
            }
        });

        forgot= findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(SignIn.this, ResetPassword.class);
                startActivity(home);
            }
        });

        edtphone=(MaterialEditText)findViewById(R.id.Edphone);
        edtpassword=(MaterialEditText)findViewById(R.id.Edpassword);
        sign_in= findViewById(R.id.sign_in);
        chbRemember = findViewById(R.id.chbRemember);

        //init paper
        Paper.init(this);

        //Init Firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    //Save user and password
                    if (chbRemember.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,edtphone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtpassword.getText().toString());
                    }
                    //check if all fields are okay
                    String phone =edtphone.getText().toString();
                    String pwd=edtpassword.getText().toString();
                    if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(pwd) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(pwd) ) {
                        AlertDialog.Builder alertdialog=new AlertDialog.Builder(SignIn.this);
                        alertdialog.setMessage("Please fill all the required details");
                        alertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertdialog.show();
                    }

                    else {
                        final SweetAlertDialog sDialog = new SweetAlertDialog(SignIn.this,SweetAlertDialog.PROGRESS_TYPE);
                        sDialog.getProgressHelper().setBarColor(Color.parseColor("#d2e2c0"));
                        sDialog.setTitleText("Please wait");
                        sDialog.setCancelable(false);
                        sDialog.show();
                        table_user.addListenerForSingleValueEvent(new ValueEventListener() {


                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //check if user exist in database
                                if (dataSnapshot.child(edtphone.getText().toString()).exists()) {

                                    //Get User information
                                    sDialog.dismiss();

                                    User user = dataSnapshot.child(edtphone.getText().toString()).getValue(User.class);
                                    user.setPhone(edtphone.getText().toString()); //set phone
                                    if (Boolean.parseBoolean(user.getStaff()))//if staff is true
                                    {
                                        if (user.getPassword().equals(edtpassword.getText().toString()))
                                        {
                                            Intent homeIntent = new Intent(SignIn.this, Home.class);
                                            Common.currentHotelOwner = user;
                                            startActivity(homeIntent);
                                            finish();
                                            table_user.removeEventListener(this);

                                        }
                                        else
                                        {new SweetAlertDialog(SignIn.this)
                                                .setTitleText("Wrong password")
                                                .show();
                                        }
                                    }
                                    else{
                                        new SweetAlertDialog(SignIn.this)
                                                .setTitleText("The account you are using to login is not authorised.Please wait for verification Email")
                                                .show();

                                    }
                                } else {
                                    sDialog.dismiss();
                                    new SweetAlertDialog(SignIn.this)
                                            .setTitleText("User does not exist in the database.Try creating an account")
                                            .show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });
                    }

                }
                else
                {
                    new SweetAlertDialog(SignIn.this,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops!Error Connecting")
                            .setContentText("Please check your internet connection and try again")
                            .show();
                }

            }
        });



    }
}
