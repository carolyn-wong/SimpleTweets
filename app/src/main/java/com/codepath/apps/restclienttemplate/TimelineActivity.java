package com.codepath.apps.restclienttemplate;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDataSourceFactory;

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
//                composeTweet();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    TwitterClient client;
    TweetAdapter tweetAdapter;
    LiveData<PagedList<Tweet>> tweets;
    RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // only call setContentView once at the top
        setContentView(R.layout.activity_timeline);

        // initialize twitter client
        client = TwitterApp.getRestClient(this);
        // find RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // construct adapter from data source
        tweetAdapter = new TweetAdapter();
        // RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        // set adapter
        rvTweets.setAdapter(tweetAdapter);
//        populateTimeline();

        // Initial page size to fetch configured here
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).build();

        // pass in dependency
        TweetDataSourceFactory factory = new TweetDataSourceFactory(client);
        tweets = new LivePagedListBuilder(factory, config).build();

        tweets.observe(this, new Observer<PagedList<Tweet>>() {
            @Override
            public void onChanged(@Nullable PagedList<Tweet> tweets) {
                tweetAdapter.submitList(tweets);
            }
        });
    }
}

//                // look up swipe container view
//                swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
//        // set up refresh listener that triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                populateTimeline();
//            }
//        });
//        // configure refreshing colors
//        swipeContainer.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
//                getResources().getColor(android.R.color.holo_green_light),
//                getResources().getColor(android.R.color.holo_orange_light),
//                getResources().getColor(android.R.color.holo_red_light));
//    }

//    private void populateTimeline() {
//        // create anonymous class to handle response from network
//        client.getHomeTimeline(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                // clear out old items before appending in new ones
//                tweetAdapter.clear();
//                // iterate through JSON array response
//                // for each entry, deserialize JSON object
//                for(int i = 0; i < response.length(); i++) {
//                    try {
//                        // convert each object to Tweet model
//                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
//                        // add Tweet model to data source
//                        tweets.add(tweet);
//                        // notify adapter that item was added
//                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
//                    } catch(JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                // on successful reload, signal that refresh has completed
//                swipeContainer.setRefreshing(false);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.d("TwitterClient", responseString);
//                throwable.printStackTrace();
//            }
//        });
//    }

//    // create request code for compose activity
//    private final int COMPOSE_CODE = 1;
//    // call compose activity using intents
//    public void composeTweet() {
//        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
//        startActivityForResult(i, COMPOSE_CODE);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // new composed tweet
//        if(resultCode == RESULT_OK && requestCode == COMPOSE_CODE) {
//            Tweet newTweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
//            tweets.add(0, newTweet);
//            tweetAdapter.notifyItemInserted(0);
//            rvTweets.scrollToPosition(0);
//        }
//    }
//}
