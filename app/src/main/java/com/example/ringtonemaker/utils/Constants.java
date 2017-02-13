package com.example.ringtonemaker.utils;

import android.os.Environment;

/**
 * Created by Mishtiii on 08-02-2017.
 */

public class Constants
{
    public static int size = 0;
    public static String SECURE_RINGTONE_MAKER_FOLDER_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/ringtone_maker/";
}
