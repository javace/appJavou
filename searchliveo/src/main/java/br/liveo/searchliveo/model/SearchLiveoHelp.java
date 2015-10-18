package br.liveo.searchliveo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudsonlive on 26/05/15.
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
