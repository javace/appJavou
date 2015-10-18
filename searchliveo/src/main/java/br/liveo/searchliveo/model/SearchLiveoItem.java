package br.liveo.searchliveo.model;


import android.os.Parcel;
import android.os.Parcelable;

public class SearchLiveoItem implements Parcelable {

    private int icon;
    private String title;

    public SearchLiveoItem(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.getIcon());
        dest.writeString(this.getTitle());
    }

    protected SearchLiveoItem(Parcel in) {
        this.setIcon(in.readInt());
        this.setTitle(in.readString());
    }

    public static final Parcelable.Creator<SearchLiveoItem> CREATOR = new Parcelable.Creator<SearchLiveoItem>() {
        public SearchLiveoItem createFromParcel(Parcel source) {
            return new SearchLiveoItem(source);
        }

        public SearchLiveoItem[] newArray(int size) {
            return new SearchLiveoItem[size];
        }
    };

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}