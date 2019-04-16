package dev.marshall.hoteladvisorho;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.hoteladvisorho.api.ApiUtils;
import dev.marshall.hoteladvisorho.api.STKPush;
import dev.marshall.hoteladvisorho.api.StoreKey;
import dev.marshall.hoteladvisorho.api.services.STKPushService;
import dev.marshall.hoteladvisorho.common.Common;
import dev.marshall.hoteladvisorho.common.Config;
import dev.marshall.hoteladvisorho.model.Subscription;
import dev.marshall.hoteladvisorho.utils.NotificationUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Payment extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    STKPushService stkPushService;
    private String token = null;
    private String phone_number = "";
    private String regId;
    private LinearLayoutManager layoutManager;


    private static final int PAYPAL_REQUEST_CODE = 0;
    MaterialSpinner mpesa,paypalpay,creditcard;
    RadioButton Rmpesa,Rpaypal,Rcreditcard;


    FirebaseDatabase database;
    DatabaseReference subscriptions;

    //paypal payment
    static PayPalConfiguration configuration=new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIEN_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //toolbar
        Toolbar toolbar= findViewById(R.id.toolb);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home =new Intent(Payment.this,Home.class);
                startActivity(home);
            }
        });
        toolbar.setTitle("Subscription");

        getToken(Config.CONSUMER_KEY, Config.CONSUMER_SECRET);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    getFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    createNotification(message);
                    showResultdialog(message);
                }
            }
        };
        getFirebaseRegId();


        //Int paypal
        Intent paypal=new Intent(this, PayPalService.class);
        paypal.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configuration);
        startService(paypal);

        //firebase
        database= FirebaseDatabase.getInstance();
        subscriptions=database.getReference("Subscriptions");


        mpesa= findViewById(R.id.mpesa);
        mpesa.setItems("3 Months-KES 1000","6 Months-KES 2000","1 Year-KES 3000");

        paypalpay= findViewById(R.id.paypal);
        paypalpay.setItems("3 Months-KES 1000","6 Months-KES 2000","1 Year-KES 3000");

        creditcard= findViewById(R.id.creditcard);
        creditcard.setItems("3 Months-KES 1000","6 Months-KES 2000","1 Year-KES 3000");


        Rmpesa= findViewById(R.id.Rmpesa);
        Rpaypal= findViewById(R.id.Rpaypal);
        Rcreditcard= findViewById(R.id.Rcreditcard);


    }

    private void getFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);

        Toast.makeText(this, ""+regId, Toast.LENGTH_LONG).show();

        if (!TextUtils.isEmpty(regId)) {
            StoreKey storeKey = new StoreKey(Payment.this);
            storeKey.createKey(regId);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void showResultdialog(String message) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();

            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Payment Notification")
                    .setContentText("Payment made succesfully")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("firstTime", false);
                            editor.commit();
                        }
                    })
                    .show();
        }
    }

    private void createNotification(String message) {
        Notification noti = new Notification.Builder(this)
                .setContentTitle(message)
                .setContentText("Subject").setSmallIcon(R.mipmap.ic_launcher).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, noti);
    }

    private String getToken(String consumerKey, String consumerSecret) {
        try {
            String keys = consumerKey + ":" + consumerSecret;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Config.TOKEN_URL)
                    .get()
                    .addHeader("authorization", "Basic " + Base64.encodeToString(keys.getBytes(), Base64.NO_WRAP))
                    .addHeader("cache-control", "no-cache")
                    .build();

            client.newCall(request)
                    .enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Payment.this, "Fetching token failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        }


                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            String res = response.body().string();
                            token = res;

                            JsonParser jsonParser = new JsonParser();
                            JsonObject jo = jsonParser.parse(token).getAsJsonObject();
                            Assert.assertNotNull(jo);
                            //Log.e("Token", token + jo.get("access_token"));
                            token = jo.get("access_token").getAsString();
                            stkPushService = ApiUtils.getTasksService(token);
                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Payment.this, "Please add your app credentials", Toast.LENGTH_LONG).show();
        }
        return token;

    }

    public void selectItem(View view) {

        final String amt= Common.converttopaypal(String.valueOf(paypalpay.getSelectedIndex()));

        switch (view.getId()) {

            case R.id.Rmpesa:

                Rpaypal.setChecked(false);
                Rcreditcard.setChecked(false);
                getPhoneNumber();

                break;

            case R.id.Rpaypal:

                Rmpesa.setChecked(false);
                Rcreditcard.setChecked(false);
                PayPalPayment payment=new PayPalPayment(new BigDecimal(amt),
                        "USD",
                        "HotelAdvisor(HO) subscription",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent= new Intent(getApplicationContext(),PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configuration);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payment);
                startActivityForResult(intent,PAYPAL_REQUEST_CODE);

                break;

            case R.id.Rcreditcard:

                Rpaypal.setChecked(false);
                Rmpesa.setChecked(false);
                Intent credit=new Intent(this, dev.marshall.hoteladvisorho.creditcard.class);
                credit.putExtra("amtcredit","KES"+Common.convercodetomoney(String.valueOf(creditcard.getSelectedIndex())));
                startActivity(credit);

                break;
        }
    }

    private void getPhoneNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter Current Safaricom phone number (2547XXX) in this phone to pay Ksh " +Common.convercodetomoney(String.valueOf(mpesa.getSelectedIndex())));

        final MaterialEditText input = new MaterialEditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setHint("07xxxxxxxx");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                phone_number = input.getText().toString();
                try {
                    performSTKPush(phone_number);
                }catch (Exception e){
                    Toast.makeText(Payment.this,"Error fetching token", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new SweetAlertDialog(Payment.this)
                        .setTitleText("Payment cancel")
                        .show();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void performSTKPush(String phone_number) {
       final SweetAlertDialog dialog= new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        dialog.setContentText("Processing..");
        dialog.setTitle("Please Wait");
        dialog.setCancelable(false);
        dialog.show();
        String timestamp = Common.getTimestamp();
        STKPush stkPush = new STKPush(Config.BUSINESS_SHORT_CODE,
                Common.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, timestamp),
                timestamp,
                Config.TRANSACTION_TYPE,
                Common.convercodetomoney(String.valueOf(mpesa.getSelectedIndex())),
                Common.sanitizePhoneNumber(phone_number),
                Config.PARTYB,
                Common.sanitizePhoneNumber(phone_number),
                Config.CALLBACKURL + regId,
                "test", //The account reference
                "test"); //The transaction description

        Log.e("Party B", phone_number);

        Call<STKPush> call = stkPushService.sendPush(stkPush);
        call.enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(Call<STKPush> call, Response<STKPush> response) {
                dialog.dismiss();
                try {
                    //Log.e("Response SUccess", response.toString());
                    if (response.isSuccessful()) {
                        Log.d(TAG, "post submitted to API." + response.body().toString());

                        String paymentDetail=response.body().toString();
                        JSONObject jsonObject=new JSONObject(paymentDetail);
                        //Create new Subscription

                        Subscription subscription=new Subscription(
                                Common.currentHotelOwner.getPhone(),
                                Common.currentHotelOwner.getName(),
                                Common.convertcodetosubscription(String.valueOf(mpesa.getSelectedIndex())),
                                Common.convercodetomoney(String.valueOf(mpesa.getSelectedIndex())),
                                jsonObject.getJSONObject("response").getString("state")//state from JSON

                        );
                        //submit to firebase
                        //we will be using System.CurrentMilli to key
                        String subscribe_number= String.valueOf(System.currentTimeMillis());
                        subscriptions.child(Common.currentHotelOwner.getPhone()).child(subscribe_number)
                                .setValue(subscription);

                        finish();

                    } else {
                        Log.e("Response", response.errorBody().string());
                        new SweetAlertDialog(Payment.this)
                                .setTitleText("Payment Invalid")
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<STKPush> call, Throwable t) {
                dialog.dismiss();
                Log.e(TAG, "Unable to submit post to API." + t.getMessage());
                t.printStackTrace();
                Log.e("Error message", t.getLocalizedMessage());
            }
        });
        //Log.e("Method end", "method end");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==PAYPAL_REQUEST_CODE)
        {
            if (resultCode== Activity.RESULT_OK){
                PaymentConfirmation confirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation !=null){
                    try {
                        String paymentDetail=confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject=new JSONObject(paymentDetail);
                        //Create new Subscription

                        Subscription subscription=new Subscription(
                                Common.currentHotelOwner.getPhone(),
                                Common.currentHotelOwner.getName(),
                                Common.convertcodetosubscription(String.valueOf(paypalpay.getSelectedIndex())),
                                Common.converttopaypal(String.valueOf(paypalpay.getSelectedIndex())),
                                jsonObject.getJSONObject("response").getString("state")//state from JSON

                        );
                        //submit to firebase
                        //we will be using System.CurrentMilli to key
                        String subscribe_number= String.valueOf(System.currentTimeMillis());
                        subscriptions.child(Common.currentHotelOwner.getPhone()).child(subscribe_number)
                                .setValue(subscription);

                        new SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Successful")
                                .setContentText("Thank you,Order placed")
                                .show();

                        finish();

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else  if (resultCode==Activity.RESULT_CANCELED){
                new SweetAlertDialog(this)
                        .setTitleText("Payment cancel")
                        .show();
            }
            else if (resultCode==PaymentActivity.RESULT_EXTRAS_INVALID){

                new SweetAlertDialog(this)
                        .setTitleText("Invalid Payment")
                        .show();
            }
        }
    }

}
