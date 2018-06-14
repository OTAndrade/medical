package com.ineedserv.medical;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by andrade on 18-09-17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = "NOTICIAS";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token:" + token);
    }


}
