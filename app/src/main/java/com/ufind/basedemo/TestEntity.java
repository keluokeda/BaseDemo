package com.ufind.basedemo;


import android.os.Parcel;
import android.os.Parcelable;

public class TestEntity implements Parcelable {

    private int size;
    private String name;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.size);
        dest.writeString(this.name);
    }

    public TestEntity() {
    }

    protected TestEntity(Parcel in) {
        this.size = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<TestEntity> CREATOR = new Parcelable.Creator<TestEntity>() {
        @Override
        public TestEntity createFromParcel(Parcel source) {
            return new TestEntity(source);
        }

        @Override
        public TestEntity[] newArray(int size) {
            return new TestEntity[size];
        }
    };
}
