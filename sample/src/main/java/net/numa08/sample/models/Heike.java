package net.numa08.sample.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

public class Heike implements Parcelable {

    private final String name;
    @DrawableRes
    private final int imageRes;

    public Heike(String name, int imageRes) {
        this.name = name;
        this.imageRes = imageRes;
    }

    @DrawableRes
    public int getImageRes() {
        return imageRes;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.imageRes);
    }

    protected Heike(Parcel in) {
        this.name = in.readString();
        this.imageRes = in.readInt();
    }

    public static final Creator<Heike> CREATOR = new Creator<Heike>() {
        public Heike createFromParcel(Parcel source) {
            return new Heike(source);
        }

        public Heike[] newArray(int size) {
            return new Heike[size];
        }
    };
}
