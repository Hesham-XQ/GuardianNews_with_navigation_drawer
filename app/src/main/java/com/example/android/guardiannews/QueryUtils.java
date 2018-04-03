package com.example.android.guardiannews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom class that holds static variables and methods required by other classes & activities of the app.
 */

public final class QueryUtils {

    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_SECTION = "sectionName";
    private static final String KEY_DATE = "webPublicationDate";
    private static final String KEY_TITLE = "webTitle";
    private static final String KEY_WEB_URL = "webUrl";
    private static final String KEY_FIELDS = "fields";
    private static final String KEY_THUMB = "thumbnail";
    private static final String KEY_TAGS = "tags";

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String badResponse = "Bad response from the server was received - response code: ";
    private static final String ioeException = "IOE exception was encountered when trying to connect to http ";
    private static final String weRecieve = "we recieve";
    private static final String urlException = "The app was not able to create a URL request from the query ";
    private static final String connectionSuccess = "Http connection was not successful ";
    private static final String parsingProblem = "Problem parsing the Guardian JSON results";


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name AppUtilities (and an object instance of AppUtilities is not needed).
     */
    private QueryUtils() {
    }

    ////
    /* Utility methods that are used to implement an http query and read information from it:
    ////
    /**
     * Executes calls to helper methods and returns a List of Book objects
     * @param stringWithHttpQuery string that contains URL query
     * @return List<Book> a List of Book objects
     */
    static List<News> getDataFromHttp(String stringWithHttpQuery) {
        String JSONString = "";
        List<News> newsList = new ArrayList<>();
        if (stringWithHttpQuery == null) {
            return newsList;
        } else {
            URL url = createUrl(stringWithHttpQuery);
            try {
                JSONString = performHttpConnection(url);
                newsList = extractFromJSONString(JSONString);
            } catch (IOException exc_03) {
                Log.e(LOG_TAG, connectionSuccess + exc_03);
            }
        }
        return newsList;
    }


    /**
     * Creates an URL object from an input String
     *
     * @param stringWithHttpQuery string that contains URL query
     * @return URL object
     */
    static private URL createUrl(String stringWithHttpQuery) {
        URL urlWithHttpQuery = null;
        try {
            urlWithHttpQuery = new URL(stringWithHttpQuery);
        } catch (MalformedURLException exc_01) {
            Log.e(LOG_TAG, urlException + exc_01);
        }
        return urlWithHttpQuery;
    }

    /**
     * Uses URL object to create and execute Http connection, obtains InputStream and calls a helper method to read it
     *
     * @param url URL query
     * @return received JSON response in a String format
     */
    static private String performHttpConnection(URL url) throws IOException {
        String JSONResponse = "";
        if (url == null) {
            return JSONResponse;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            // check whether the connection response code is appropriate (in this case == 200)
            if (httpURLConnection.getResponseCode() == 200) {
                Log.e(LOG_TAG, weRecieve + httpURLConnection.getResponseCode());
                inputStream = httpURLConnection.getInputStream();
                JSONResponse = readInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, badResponse + httpURLConnection.getResponseCode());
            }
        } catch (IOException exc_02) {
            Log.e(LOG_TAG, ioeException + exc_02);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JSONResponse;
    }

    /**
     * Reads InputStream and parses it into a String
     *
     * @param stream InputStream
     * @return String
     */
    static private String readInputStream(InputStream stream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (stream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Reads JSONString and extracts relevant data from it
     *
     * @param JSONString - result of the previous http query parsed into String format
     * @return List<Book> a list of Book objects
     */
    static private List<News> extractFromJSONString(String JSONString) {
        if (TextUtils.isEmpty(JSONString)) {
            return null;
        }

        List<News> news = new ArrayList<News>();

        try {
            JSONObject baseJSONObject = new JSONObject(JSONString);
            JSONObject responseJSONObject = baseJSONObject.getJSONObject(KEY_RESPONSE);
            JSONArray newsResults = responseJSONObject.getJSONArray(KEY_RESULTS);

            // Variables for JSON parsing
            String section;
            String publicationDate;
            String title;
            String webUrl;
            String contributor = null;
            String thumbnail = null;
            for (int i = 0; i < newsResults.length(); i++) {
                JSONObject newsArticle = newsResults.getJSONObject(i);

                if (newsArticle.has(KEY_SECTION)) {
                    section = newsArticle.getString(KEY_SECTION);
                } else section = null;

                if (newsArticle.has(KEY_DATE)) {
                    publicationDate = newsArticle.getString(KEY_DATE);
                } else publicationDate = null;

                if (newsArticle.has(KEY_TITLE)) {
                    title = newsArticle.getString(KEY_TITLE);
                } else title = null;

                if (newsArticle.has(KEY_WEB_URL)) {
                    webUrl = newsArticle.getString(KEY_WEB_URL);
                } else webUrl = null;

                JSONObject fields = newsArticle.getJSONObject(KEY_FIELDS);
                if (fields.has(KEY_THUMB)) {
                    thumbnail = fields.getString(KEY_THUMB);
                } else thumbnail = "";

                JSONArray tagsArray = newsArticle.getJSONArray(KEY_TAGS);
                for (int j = 0; j < tagsArray.length(); j++) {
                    JSONObject tag = tagsArray.getJSONObject(j);
                    if (tag.has(KEY_TITLE)) {
                        contributor = tag.getString(KEY_TITLE);
                    } else {
                        contributor = "";
                    }
                }
                if (tagsArray.length() == 0) {
                    contributor = "";
                }
                News newsList = new News(title, publicationDate, section, webUrl, thumbnail, contributor);
                news.add(newsList);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, parsingProblem, e);
        }
        return news;
    }


}