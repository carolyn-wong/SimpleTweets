package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;
    EditText etCompose;
    Button btCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // initialize twitter client
        client = TwitterApp.getRestClient(this);

        // assign views
        btCompose = (Button) findViewById(R.id.btCompose);
        etCompose = (EditText) findViewById(R.id.etCompose);

        btCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String composeText = etCompose.getText().toString();
                client.sendTweet(composeText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            // convert to Tweet model
                            Tweet newTweet = Tweet.fromJSON(response);
                            // create intent to send data back to TimelineActivity
                            Intent returnTweet = new Intent();
                            // pass tweet as extra serialized via Parcels.wrap(), using short name as key
                            returnTweet.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(newTweet));
                            setResult(RESULT_OK, returnTweet);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });
    }
}
