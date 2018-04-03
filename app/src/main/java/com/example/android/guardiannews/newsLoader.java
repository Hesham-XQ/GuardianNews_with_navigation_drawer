package com.example.android.guardiannews;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by SAMO on 3/23/2018.
 */

public class newsLoader extends AsyncTaskLoader<List<News>> {
    private String urlString;
    private List<News> listInCacheMemory;

    public newsLoader(Context context, String mUrlString) {
        super(context);
        urlString = mUrlString;
    }

    @Override
    protected void onStartLoading() {
        if (listInCacheMemory == null) {
            forceLoad();
        } else {
            deliverResult(listInCacheMemory);
        }
    }

    @Override
    public List<News> loadInBackground() {
        if (urlString == null) {
            return null;
        }
        List<News> newsList = QueryUtils.getDataFromHttp(urlString);
        return newsList;
    }

    @Override
    public void deliverResult(List<News> data) {
        listInCacheMemory = data;
        super.deliverResult(data);
    }
}
