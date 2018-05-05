package com.example.android.guardiannews;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {


    private ActionBarDrawerToggle mToggle;

    @BindView(R.id.empty_state_text)
    TextView noResultsView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.search_results)
    ListView newsSearchResultsListView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.drawer)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private ArrayList<News> newsArrayList = new ArrayList<News>();
    private NewsAdapter newsAdapter;
    private static LoaderManager loaderManager;
    private static final String API_INITIAL_QUERY = "https://content.guardianapis.com/search?";
    Parcelable state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawer.closeDrawers();
                        int itemId = menuItem.getItemId();

                        switch (itemId) {
                            case R.id.fresh_news:
                                swipeRefresh();
                                break;

                            case R.id.sport:
                                drawertouchHandle("sport");
                                break;

                            case R.id.football:
                                drawertouchHandle("football");
                                break;
                            case R.id.culture:
                                drawertouchHandle("culture");
                                break;
                            case R.id.business:
                                drawertouchHandle("business");
                                break;
                            case R.id.fashion:
                                drawertouchHandle("fashion");
                                break;

                            case R.id.technology:
                                drawertouchHandle("technology");
                                break;
                            case R.id.travel:
                                drawertouchHandle("travel");
                                break;
                            case R.id.money:
                                drawertouchHandle("money");
                                break;
                            case R.id.science:
                                drawertouchHandle("science");
                                break;

                            case R.id.environment:
                                drawertouchHandle("environment");
                                break;
                            case R.id.music:
                                drawertouchHandle("music");
                                break;
                            case R.id.politics:
                                drawertouchHandle("politics");
                                break;
                            case R.id.society:
                                drawertouchHandle("society");
                                break;
                        }
                        return true;
                    }
                });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh();
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            noResultsView.setText(getString(R.string.wait));
            initializeLoaderAndAdapter();
        } else {
            noResultsView.setText(getString(R.string.no_internet_connection_message));
        }

        newsSearchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News selectedArticle = newsAdapter.getItem(position);
                String ur = selectedArticle.getUrl();
                Uri newsUri = Uri.parse(ur);
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }

        });
    }


    private void drawertouchHandle(String section) {
        String sec = section;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(sec + " news");
        }
        handlequery(sec);
    }

    @Override
    protected void onPause() {
        // Save ListView state @ onPause
        state = newsSearchResultsListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        return new newsLoader(this, args);
    }


    public void handlequery(String sec) {
        Uri baseIri = Uri.parse(API_INITIAL_QUERY);
        Uri.Builder uriBuilder = baseIri.buildUpon();
        String section = sec;
        String orderBy = "newest";

        uriBuilder.appendQueryParameter("q", "");
        uriBuilder.appendQueryParameter("section", section);
        uriBuilder.appendQueryParameter("use-date", "published");
        uriBuilder.appendQueryParameter("page-size", "50");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,short-url");
        uriBuilder.appendQueryParameter("api-key", "test");
        Log.v("MainActivity", "Uri: " + uriBuilder);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            noResultsView.setText(getString(R.string.refresh));
            progressBar.setVisibility(View.VISIBLE);
            if (newsAdapter != null) {
                newsAdapter.clearAll();
            }
            if (loaderManager != null) {
                Bundle args = new Bundle();
                args.putString("uri", uriBuilder.toString());
                getLoaderManager().restartLoader(1, args, MainActivity.this);

                swipeRefreshLayout.setRefreshing(false);
            } else {
                initializeLoaderAndAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }

        } else {
            if (newsAdapter != null) {
                newsAdapter.clearAll();
            }
            noResultsView.setText(getString(R.string.no_internet_connection_message));
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    public void swipeRefresh() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Fresh News");
        }
        Uri baseIri = Uri.parse(API_INITIAL_QUERY);
        Uri.Builder uriBuilder = baseIri.buildUpon();
        String orderBy = "newest";

        uriBuilder.appendQueryParameter("q", "");
        uriBuilder.appendQueryParameter("use-date", "published");
        uriBuilder.appendQueryParameter("page-size", "50");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,short-url");
        uriBuilder.appendQueryParameter("api-key", "test");
        Log.v("MainActivity", "Uri: " + uriBuilder);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            noResultsView.setText(getString(R.string.refresh));
            progressBar.setVisibility(View.VISIBLE);
            if (newsAdapter != null) {
                newsAdapter.clearAll();
            }
            if (loaderManager != null) {
                Bundle args = new Bundle();
                args.putString("uri", uriBuilder.toString());
                getLoaderManager().restartLoader(1, args, MainActivity.this);

                swipeRefreshLayout.setRefreshing(false);
            } else {
                initializeLoaderAndAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }

        } else {
            if (newsAdapter != null) {
                newsAdapter.clearAll();
            }
            noResultsView.setText(getString(R.string.no_internet_connection_message));
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    public void handlequery(String search, String sec, String order) {

        Uri baseIri = Uri.parse(API_INITIAL_QUERY);
        Uri.Builder uriBuilder = baseIri.buildUpon();
        String searchQuery = search;
        String section = sec;
        String orderBy = order;

        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("section", section);
        uriBuilder.appendQueryParameter("use-date", "published");
        uriBuilder.appendQueryParameter("page-size", "50");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail,short-url");
        uriBuilder.appendQueryParameter("api-key", "test");
        Log.v("MainActivity", "Uri: " + uriBuilder);


        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            noResultsView.setText(getString(R.string.refresh));
            progressBar.setVisibility(View.VISIBLE);
            if (newsAdapter != null) {
                newsAdapter.clearAll();
            }
            if (loaderManager != null) {
                Bundle args = new Bundle();
                args.putString("uri", uriBuilder.toString());
                getLoaderManager().restartLoader(1, args, MainActivity.this);

                swipeRefreshLayout.setRefreshing(false);
            } else {
                initializeLoaderAndAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }

        } else {
            if (newsAdapter != null) {
                newsAdapter.clearAll();
            }
            noResultsView.setText(getString(R.string.no_internet_connection_message));
            swipeRefreshLayout.setRefreshing(false);
        }

    }


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsItems) {
        if (newsItems != null && !newsItems.isEmpty()) {
            newsAdapter.addAll(newsItems);
            progressBar.setVisibility(View.GONE);
            noResultsView.setText("");
        } else {
            noResultsView.setText(getString(R.string.no_article));
            progressBar.setVisibility(View.GONE);
        }
        Log.v("MainActivity", "Loader completed operation!");
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }

    public void initializeLoaderAndAdapter() {
        loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this);
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsSearchResultsListView.setAdapter(newsAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsAvtivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_refresh) {
            onPostResume();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
        if (state != null) {
            newsSearchResultsListView.onRestoreInstanceState(state);
        } else {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String searchQuery = sharedPreferences.getString(getString(R.string.settings_search_query_key), getString(R.string.settings_search_query_default));
            String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_list_key), getString(R.string.settings_order_by_list_default));
            String section = sharedPreferences.getString(getString(R.string.section_key), getString(R.string.settings_section_list_default));

            handlequery(searchQuery, section, orderBy);
        }
    }

}

