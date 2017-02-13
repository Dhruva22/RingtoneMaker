package com.example.ringtonemaker.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.ringtonemaker.service.CallerNameService;

/**
 * Created by Mishtiii on 11-02-2017.
 */

public class PhoneReceiver extends BroadcastReceiver
{
    Context context = null;
    private static final String TAG = "Phone call";
    String caller_name="";

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        this.context = context;
        boolean callerFlag;
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context , RingtoneManager.TYPE_RINGTONE);

        callerFlag = context.getSharedPreferences("RINGTONE_MAKER",0).getBoolean("callerFlag",false);
        Log.e("callerFlag",callerFlag+"");
        if(callerFlag)
        {
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state))
            {
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                caller_name = getContactDisplayNameByNumber(incomingNumber);
                Log.i("name", caller_name);

                if(caller_name.equals(""))
                {
                    caller_name = "unknown";
                }

                Intent speechIntent = new Intent();
                speechIntent.setClass(context, CallerNameService.class);
                speechIntent.putExtra("ringtone_uri",defaultRingtoneUri + "");
                speechIntent.putExtra("caller_name",caller_name);
                speechIntent.putExtra("state",state+"");
                speechIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startService(speechIntent);
            }
            else
            {
                context.stopService(new Intent(context,CallerNameService.class));
            }
        }
        else
        {

        }
    }

    public String getContactDisplayNameByNumber(String number)
    {
        String callerName = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, null, null, null, null);

        try
        {
            if (contactLookup != null && contactLookup.getCount() > 0)
            {
                contactLookup.moveToNext();
                callerName = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        }
        finally
        {
            if (contactLookup != null)
            {
                contactLookup.close();
            }
        }

        return callerName;
    }
}
