package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    // populate action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // handle all action buttons in single method (if adding more items)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle clicks on action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composeTweet();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // initialize twitter client
        client = TwitterApp.getRestClient(this);

        // find RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // initialize ArrayList (data source)
        tweets = new ArrayList<>();
        // construct adapter from data source
        tweetAdapter = new TweetAdapter(tweets);
        // RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        // set adapter
        rvTweets.setAdapter(tweetAdapter);
        populateTimeline();
    }

    private void populateTimeline() {
        // create anonymous class to handle response from network
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Log.d("TwitterClient", response.toString());
                // iterate through JSON array
                // for each entry, deserialize JSON object

                for(int i = 0; i < response.length(); i++) {
                    try {
                        // convert each object to Tweet model
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        // add Tweet model to data source
                        tweets.add(tweet);
                        // notify adapter that item was added
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });
    }

    // create request code for compose activity
    private final int COMPOSE_CODE = 1;
    // call compose activity using intents
    public void composeTweet() {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, COMPOSE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // new composed tweet
        if(resultCode == RESULT_OK && requestCode == COMPOSE_CODE) {
            Tweet newTweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
            tweets.add(0, newTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }
}