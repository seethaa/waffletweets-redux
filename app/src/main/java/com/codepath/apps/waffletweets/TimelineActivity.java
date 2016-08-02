package com.codepath.apps.waffletweets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.codepath.apps.waffletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    private TwitterClient client;//singleton client
    private TweetsArrayAdapter mTweetsAdapter;
    private ArrayList<Tweet> mTweets;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mListView = (ListView) findViewById(R.id.lvTweets);
        //create arraylist (data source)
        mTweets  = new ArrayList<>();
        //construct adapter from data source
        mTweetsAdapter = new TweetsArrayAdapter(this, mTweets);
        //connect adapter to list view
        mListView.setAdapter(mTweetsAdapter);
        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    //send an API request to get the timeline JSON
    //fill in the listview by creating the tweet objects from the JSON
    private void populateTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());
                //deserialize json
                //create models and add them to adapter
                //load model data into listview
                mTweetsAdapter.addAll(Tweet.fromJSONArray(jsonResponse));
                Log.d("DEBUG", mTweetsAdapter.toString());
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });


    }
}
