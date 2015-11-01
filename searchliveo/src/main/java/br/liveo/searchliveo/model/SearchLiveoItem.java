package br.liveo.searchliveo.model;

/*
 * Copyright 2015 Rudson Lima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rudsonlive on 18/10/15.
 */
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