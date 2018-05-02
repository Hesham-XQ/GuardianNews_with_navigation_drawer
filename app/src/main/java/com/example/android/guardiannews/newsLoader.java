package com.example.android.guardiannews;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import java.util.List;

/**
 * Created by SAMO on 3/23/2018.
 */

public class newsLoader extends AsyncTaskLoader<List<News>> {
    private List<News> listInCacheMemory;
    private final Bundle pBundle;

    public newsLoader(Context context, Bundle bundle) {
        super(context);
        pBundle = bundle;
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
        List<News> newsList = null;
        // If bundle has data get articles
        if (pBundle != null) {
            newsList = QueryUtils.getDataFromHttp(pBundle.getString("uri"));
        }
        return newsList;

    }

    @Override
    public void deliverResult(List<News> data) {
        listInCacheMemory = data;
        super.deliverResult(data);
    }
}
