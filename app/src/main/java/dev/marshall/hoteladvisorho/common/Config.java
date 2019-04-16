package dev.marshall.hoteladvisorho.common;

/**
 * Created by Marshall on 17/03/2018.
 */

public class Config {
    public static final String PAYPAL_CLIEN_ID="AWsyodYs00LKo9wyJx35PA789SgKn5z__OsWcicGTE-E4BitoOHzbeAEkiYpltHWsqCrPpvs4IDz-dvT";


    //Use credentials from your Lipa na MPESA Online(MPesa Express) App from the developer portal

    public static final String CONSUMER_KEY = "drK9PdAIrAKSZ1o6COURgmV4toDS17E9";
    public static final String CONSUMER_SECRET = "FYcUwXoMGFYOiiV3";
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    //STKPush Properties
    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    public static final String TRANSACTION_TYPE = "CustomerPayBillOnline";
    public static final String PARTYB = "174379";
    public static final String CALLBACKURL = "https://spurquoteapp.ga/pusher/pusher.php?title=stk_push&message=result&push_type=individual&regId=";
    //public static final String CALLBACKURL = "https://mpesa.bdhobare.com/mpesa/";

    public static final String TOKEN_URL = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
}
