package com.fram.codingassignment.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by thaile on 6/4/17.
 */

public class StringUtils {

    private static final String TAG = StringUtils.class.toString();

    public static String readJson(Context context, String path){
        String json = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e(TAG, "e = " + ex.toString());
        }
        return json;
    }

}
