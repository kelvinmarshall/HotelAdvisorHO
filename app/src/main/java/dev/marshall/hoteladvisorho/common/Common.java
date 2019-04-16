package dev.marshall.hoteladvisorho.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dev.marshall.hoteladvisorho.Myhotel;
import dev.marshall.hoteladvisorho.Remote.IGeoCoordinates;
import dev.marshall.hoteladvisorho.Remote.RetrofitClient;
import dev.marshall.hoteladvisorho.model.MyHotels;
import dev.marshall.hoteladvisorho.model.User;

/**
 * Created by Marshall on 27/02/2018.
 */

public class Common {
    public static User currentHotelOwner;
    public static MyHotels currentHotel;

    public static String currentlocation;

    public static String APPLICATION_ID="QZNK9LXV19";
    public static String API_KEY="4ac09b7d0adce89157de480429a3e747";
    public static String ADMIN_API_KEY="a3a6ffd0e0da9f2ef03789d9ef016384";

    public static Double currenthotellocation_lat;
    public static Double currenthotellocation_lng;

    public static final String UPDATE ="Update";
    public static final String DELETE ="Delete";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

    public static String convertcodetosubscription(String subscription )
    {
        if (subscription.equals("0"))
            return "3 Months-KES 1000";
        else  if (subscription.equals("1"))
            return "6 Months-KES 2000";
        else
            return "3 Months-KES 3000";
    }
    public static String convercodetomoney(String money )
    {
        if (money.equals("0"))
            return "1";
        else  if (money.equals("1"))
            return "2000";
        else
            return "3000";
    }
    public static String converttopaypal(String currency )
    {
        if (currency.equals("0"))
            return "10";
        else  if (currency.equals("1"))
            return "20";
        else
            return "30";
    }
    public  static String Roomcapacity(String capacity){
        if (capacity.equals("0"))
            return "1 Person";
        else  if (capacity.equals("1"))
            return "2 People";
        else
            return "3 People";
    }

    public  static String Roomtype(String type){
        if (type.equals("0"))
            return "Standard Room";
        else  if (type.equals("1"))
            return "Standard-Tripple Room";
        else  if (type.equals("2"))
            return "Conference Room";
        else
            return "VIP Room";
    }
    public  static String Hotelclass(String type){
        if (type.equals("0"))
            return "1 Star";
        else  if (type.equals("1"))
            return "2 Star";
        else  if (type.equals("2"))
            return "3 Star";
        else  if (type.equals("3"))
            return "4 Star";
        else  if (type.equals("4"))
            return "5 Star";
        else  if (type.equals("5"))
            return "6 Star";
        else
            return "7 Star";
    }

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager !=null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info !=null)
            {
                for (int i=0;i<info.length;i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
    public static String getTimestamp(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        return timeStamp;
    }
    public static  String sanitizePhoneNumber(String phone) {

        if(phone.equals("")){
            return "";
        }

        if (phone.length() < 11 & phone.startsWith("0")) {
            String p = phone.replaceFirst("^0", "254");
            return p;
        }
        if(phone.length() == 13 && phone.startsWith("+")){
            String p = phone.replaceFirst("^+", "");
            return p;
        }
        return phone;
    }
    public static String getPassword(String businessShortCode, String passkey, String timestamp){
        String str = businessShortCode + passkey + timestamp;
        //encode the password to Base64
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }

    public static final String baseURl = "https://maps.googleapis.com";


    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseURl).create(IGeoCoordinates.class);
    }
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleY = newWidth/(float)bitmap.getWidth();
        float scaleX = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }
}
