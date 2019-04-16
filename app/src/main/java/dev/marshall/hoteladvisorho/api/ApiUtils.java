package dev.marshall.hoteladvisorho.api;

import dev.marshall.hoteladvisorho.api.services.STKPushService;

/**
 * Created by Marshall on 18/03/2018.
 */

public class ApiUtils {
    //endpoint of my Api
    public static final String BASE_URL = "https://sandbox.safaricom.co.ke/";

    public static STKPushService getTasksService(String token) {
        return RetrofitClient.getClient(BASE_URL, token).create(STKPushService.class);
    }
}
