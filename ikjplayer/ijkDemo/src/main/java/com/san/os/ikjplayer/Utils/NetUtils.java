package com.san.os.ikjplayer.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author luluc@yiche.com
 * @Description
 * @date 2017-01-23 11:01
 */

public class NetUtils {
    /**
     * 1:2G 2:3G 3:Wifi 4: CellNetwork
     *
     * @return
     */
    public static int getNetWorkType(Context context) {
        ConnectivityManager connectMgr =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectMgr.getActiveNetworkInfo();

        if (null == info) return 4;

        if (info.getType() == ConnectivityManager.TYPE_WIFI) return 3;

        int subtype = info.getSubtype();
        // 2g
        if (subtype == TelephonyManager.NETWORK_TYPE_GPRS
                || subtype == TelephonyManager.NETWORK_TYPE_EDGE
                || subtype == TelephonyManager.NETWORK_TYPE_EVDO_0
                || subtype == TelephonyManager.NETWORK_TYPE_EVDO_A
                || subtype == TelephonyManager.NETWORK_TYPE_EVDO_B) return 1;
        // 3g
        if (subtype == TelephonyManager.NETWORK_TYPE_UMTS
                || subtype == TelephonyManager.NETWORK_TYPE_HSDPA
                || subtype == TelephonyManager.NETWORK_TYPE_CDMA) return 2;

        return 4;
    }
}
