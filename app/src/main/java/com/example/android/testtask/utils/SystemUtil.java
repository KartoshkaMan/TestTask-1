package com.example.android.testtask.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class SystemUtil {

    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

}
