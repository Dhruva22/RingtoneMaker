package com.example.ringtonemaker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mishtiii on 08-02-2017.
 */

public class Ringtones implements Parcelable {
    int id;
    String ringtone_name,ringtone_source;

    public Ringtones()
    {
    }

    public Ringtones(int id, String ringtone_name, String ringtone_source)
    {
        this.id = id;
        this.ringtone_name = ringtone_name;
        this.ringtone_source = ringtone_source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRingtone_name() {
        return ringtone_name;
    }

    public void setRingtone_name(String ringtone_name) {
        this.ringtone_name = ringtone_name;
    }

    public String getRingtone_source() {
        return ringtone_source;
    }

    public void setRingtone_source(String ringtone_source) {
        this.ringtone_source = ringtone_source;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.ringtone_name);
        dest.writeString(this.ringtone_source);
    }

    protected Ringtones(Parcel in) {
        this.id = in.readInt();
        this.ringtone_name = in.readString();
        this.ringtone_source = in.readString();
    }

    public static final Parcelable.Creator<Ringtones> CREATOR = new Parcelable.Creator<Ringtones>() {
        @Override
        public Ringtones createFromParcel(Parcel source) {
            return new Ringtones(source);
        }

        @Override
        public Ringtones[] newArray(int size) {
            return new Ringtones[size];
        }
    };
}
