package com.codepath.apps.waffletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.waffletweets.R;
import com.codepath.apps.waffletweets.fragments.UserTimelineFragment;
import com.codepath.apps.waffletweets.models.User;
import com.codepath.apps.waffletweets.network.TwitterApplication;
import com.codepath.apps.waffletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient mTwitterClient;
    User mUser;

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvTagline)
    TextView tvTagline;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;
    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        mTwitterClient = TwitterApplication.getRestClient();
        //get account info.
        mTwitterClient.getUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mUser = User.fromJSON(response);
                //my current user account's information
                getSupportActionBar().setTitle("@" + mUser.getScreenName());
                populateProfileHeader(mUser);
            }
        });


        //get the screenname from the activity that launches this activity (TimelineActivity)

        String screenName = getIntent().getStringExtra("screen_name");
        if (savedInstanceState == null) {
            //create user timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            //display user fragment within this activity (dynamically)
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //transaction is a statement that you want to change what fragment is on the screen
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
    }

    private void populateProfileHeader(User mUser) {
        tvName.setText(mUser.getName());
        tvTagline.setText(mUser.getTagline());
        tvFollowers.setText(mUser.getFollowersCount() + " Followers");
        tvFollowing.setText(mUser.getFriendsCount() + " Following");

        Glide.with(getApplicationContext()).load(mUser.getProfileImageURL()).centerCrop().placeholder(R.drawable.ic_launcher)
                .bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 5, 5))
                .into(ivProfileImage);
    }


}
