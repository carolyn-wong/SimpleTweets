package com.codepath.apps.restclienttemplate.models;

import android.arch.paging.ItemKeyedDataSource;
import android.support.annotation.NonNull;

import com.codepath.apps.restclienttemplate.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TweetDataSource extends ItemKeyedDataSource<Long, Tweet> {
    // define key (Tweet post id) used to determine next page of data
    @NonNull
    @Override
    public Long getKey(@NonNull Tweet item) {
        return item.getUid();
    }

    // pass dependencies needed to make network call
    TwitterClient mClient;

    public TweetDataSource(TwitterClient client) {
        mClient = client;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Tweet> callback) {
        // fetch data synchronously (second parameter set to true)
        // load initial data set so paged list not empty
        JsonHttpResponseHandler jsonHttpResponseHandler = createTweetHandler(callback, true);

        // no max_id passed on initial load
        mClient.getHomeTimeline(1L, params.requestedLoadSize, jsonHttpResponseHandler);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Tweet> callback) {
        // network call can be asynchronous (second parameter false)
        JsonHttpResponseHandler jsonHttpResponseHandler = createTweetHandler(callback, false);

        // params.key lowest Twitter post ID retrieved (max_id for Twitter API)
        // max_id = params.key - 1
        mClient.getHomeTimeline(params.key - 1, params.requestedLoadSize, jsonHttpResponseHandler);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Tweet> callback) {

    }

    // parses JSON data and posts to RecyclerView adapter/PagedList handler
    // network calls done in background thread, so network call should run synchronously
    public JsonHttpResponseHandler createTweetHandler(final LoadCallback<Tweet> callback, boolean isAsync) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> tweets = new ArrayList<Tweet>();
                for(int i = 0; i < response.length(); i++) {
                    try {
                        // convert each object to Tweet model
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        // add Tweet model to data source
                        tweets.add(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // send to PagedList handler
                callback.onResult(tweets);
            }
        };

        // TODO FIGURE OUT WHAT THIS DOES. ALSO WHAT IS THE CALLBACK
        if (isAsync) {
            // fetch data synchronously
            handler.setUseSynchronousMode(true);
            handler.setUsePoolThread(true);
        }
        return handler;
    }
}
