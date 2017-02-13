package com.example.ringtonemaker.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by Mishtiii on 08-02-2017.
 */

public class Util
{
    public static File getStoragePath(Context context)
    {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File path = new File(root + "/Ringtone_Maker/");
        if(!path.exists())
        {
            path.mkdirs();
        }
        return path;
    }

    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }
}
