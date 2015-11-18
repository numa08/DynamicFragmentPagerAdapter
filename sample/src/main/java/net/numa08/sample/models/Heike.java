package net.numa08.sample.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

public class Heike implements Parcelable {

    private final String name;
    private final String description;

    public Heike(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
    }

    protected Heike(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
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
