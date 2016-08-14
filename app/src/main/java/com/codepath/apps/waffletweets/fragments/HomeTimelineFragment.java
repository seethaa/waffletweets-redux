package com.codepath.apps.waffletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.waffletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.apps.waffletweets.models.User;
import com.codepath.apps.waffletweets.network.TwitterApplication;
import com.codepath.apps.waffletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 *
 */
public class HomeTimelineFragment extends TweetsListFragment{
    private TwitterClient mTwitterClient;
    private User mCurrentUser;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwitterClient = TwitterApplication.getRestClient();
        getCurrentUserInfo(); //get user account info and save as user object
        populateTimeline(null); //populate initial timeline

    }

    private void getCurrentUserInfo() {
        mTwitterClient.getUserInfo(new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());


                mCurrentUser = User.fromJSON(jsonResponse);

                // Log.d("DEBUG", mTweetsAdapter.toString());
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });


    }

    //send an API request to get the timeline JSON
    //fill in the listview by creating the tweet objects from the JSON
    private void populateTimeline(Long max_id) {
        mTwitterClient.getHomeTimeline(max_id, new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());
                //deserialize json
                //create models and add them to adapter
                //load model data into listview

                List<Tweet> tweets = Tweet.fromJSONArray(jsonResponse);

                    if (tweets!=null) {
                        addAll(tweets);
                        Log.d("DEBUG", "What am I printing?");
                    }

            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });


    }

    public List<Tweet> getTweetsList(){
        return super.mTweets;
    }
    public TweetsArrayAdapter getTweetsAdapter(){
        return super.mTweetsAdapter;
    }

    /**
     *  Send an API request to get the timeline JSON.
     *  Fill in the recyclerview by creating the tweet objects from the JSON
     */
    private void postTweet(final Tweet tweet) {
        mTwitterClient.postTweet(tweet, new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());

                mTweets.add(0, tweet);
                mTweetsAdapter.notifyItemInserted(0);

            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        });


    }

}
