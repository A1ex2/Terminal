package com.algoritm.terminal.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Sector implements Parcelable {
    private String ID;
//    private String guid;
    private String name;

    public static final String TABLE_NAME = "Sectors";
    public static final String COLUM_ID = "_id";
    public static final String COLUM_GUID = "Guid";
    public static final String COLUM_NAME = "Name";

    public Sector() {
    }
//
//    public String getGuid() {
//        return guid;
//    }
//
//    public void setGuid(String guid) {
//        this.guid = guid;
//    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "" + name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ID);
//        dest.writeString(this.guid);
        dest.writeString(this.name);
    }

    protected Sector(Parcel in) {
        this.ID = in.readString();
//        this.guid = in.readString();
        this.name = in.readString();
    }

    public static final Creator<Sector> CREATOR = new Creator<Sector>() {
        @Override
        public Sector createFromParcel(Parcel source) {
            return new Sector(source);
        }

        @Override
        public Sector[] newArray(int size) {
            return new Sector[size];
        }
    };
}
