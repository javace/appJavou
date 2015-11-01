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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudsonlive on 18/10/15.
 */
public class SearchLiveoHelp {

    private SearchLiveoItem mSearchItem;
    private List<SearchLiveoItem> mSearchList;

    public SearchLiveoHelp(){
        this.mSearchList = new ArrayList<>();
    }

    private void newSearchHelpLiveo(){
        this.mSearchItem = new SearchLiveoItem();
    }

    public void add(int icon, String title){
        this.newSearchHelpLiveo();
        this.mSearchItem.setTitle(title);
        this.mSearchItem.setIcon(icon);
        this.mSearchList.add(mSearchItem);
    }

    public SearchLiveoItem get(int position){
        return this.mSearchList.get(position);
    }

    public List<SearchLiveoItem> getList(){
        return this.mSearchList;
    }

    public int getCount(){
        return this.mSearchList.size();
    }
}
