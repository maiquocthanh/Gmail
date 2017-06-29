package com.funix.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hung on 6/12/2017.
 */

public class Utils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo mobileNwInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNwInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return ((mobileNwInfo == null ? false : mobileNwInfo.isConnected()) || (wifiNwInfo == null ? false : wifiNwInfo.isConnected()));
    }

    public static boolean validateEmail(String email) {
        boolean isValid = false;

        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

}
