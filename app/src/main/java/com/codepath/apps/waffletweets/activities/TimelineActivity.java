package com.codepath.apps.waffletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.waffletweets.R;
import com.codepath.apps.waffletweets.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.waffletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.waffletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.waffletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.apps.waffletweets.models.User;
import com.codepath.apps.waffletweets.network.TwitterApplication;
import com.codepath.apps.waffletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity{
    private TweetsArrayAdapter mTweetsAdapter;
    private ArrayList<Tweet> mTweets;
    private LinearLayoutManager mLinearLayoutManager;
    private User mCurrentUser;
    private TwitterClient mTwitterClient;

    private SmartFragmentStatePagerAdapter adapterViewPager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ButterKnife.bind(this);


//        adapterViewPager = new SmartFragmentStatePagerAdapter(getSupportFragmentManager()) {
//            @Override
//            public int getCount() {
//                return 2;
//            }
//
//            @Override
//            public Fragment getItem(int position) {
//                if (position ==0){
//                    return new HomeTimelineFragment();
//                }
//                else if (position ==1){
//                    return new MentionsTimelineFragment();
//                }
//                else{
//                    return null;
//                }
//            }
//        };

        //get the viewpager
        //VP indicator is what displays which page you're on within viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);

        //set the viewpager adapter for pager
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        //find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        //attach the pagertabs to the viewpager
        tabStrip.setViewPager(vpPager);

        tabStrip.setForegroundGravity(TabLayout.GRAVITY_FILL);



        mTwitterClient = TwitterApplication.getRestClient();

        //set background color of actionbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.waffletweetslogofinal);




        //set pull to refresh
//        mMaterialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
//        mMaterialRefreshLayout.setIsOverLay(false);
//        mMaterialRefreshLayout.setWaveShow(false);
//
//        mMaterialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
//            @Override
//            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
//                refreshItems();
//            }
//
//            @Override
//            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
//                //load more refreshing...
//            }
//        });


    }

    public void onProfileView(MenuItem mi){
        //launch the profile view

        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);

    }



//    /**
//     *  Send an API request to get the timeline JSON.
//     *  Fill in the recyclerview by creating the tweet objects from the JSON
//     */
//    private void postTweet(final Tweet tweet) {
//        mTwitterClient.postTweet(tweet, new JsonHttpResponseHandler() {
//            //success
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
//                Log.d("DEBUG", jsonResponse.toString());
//
//                mTweets.add(0, tweet);
//                mTweetsAdapter.notifyItemInserted(0);
//
//            }
//
//            //failure
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("DEBUG", errorResponse.toString());
//
//            }
//        });
//
//
//    }


//    @Override
//    public void onFinishComposeTweetDialog(Tweet tweet) {
//        System.out.println("DEBUGGY: Got to finish");
//
//        postTweet(tweet);
//
//        refreshItems();
//    }


//    /**
//     * Checks if network is available and if device is online. If connected, it refreshes timeline items
//     */
//    private void refreshItems() {
//
//        if (isNetworkAvailable() && isOnline()) {
////            HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);
////            HomeTimelineFragment.mTweets.clear();
////            HomeTimelineFragment.mTweetsAdapter.notifyDataSetChanged();
//
////            TweetsListFragment fragmentDemo = (TweetsListFragment)
////                    getSupportFragmentManager().findFragmentById(R.id.tweet_list_frag);
////            fragmentDemo.refreshItems("some string");
//
////            populateTimeline(null);
//
//            // refresh complete
////            mMaterialRefreshLayout.finishRefresh();
//
//            // load more refresh complete
////            mMaterialRefreshLayout.finishRefreshLoadMore();
//        } else {
//            callNetworkDialog();
//
//        }
//    }




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








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miProfile:

                // User chose the "Settings" item, show the app settings UI...
                return true;

//            case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    //return order of fragments in view pager
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        //adapter gets the manager to insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //the order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {//return fragment for position
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            } else {
                return null;
            }

//            switch (position) {
//                case 0: // Fragment # 0 - This will show FirstFragment
//                    return FirstFragment.newInstance(0, "Page # 1");
//                case 1: // Fragment # 0 - This will show FirstFragment different title
//                    return FirstFragment.newInstance(1, "Page # 2");
//                case 2: // Fragment # 1 - This will show SecondFragment
//                    return SecondFragment.newInstance(2, "Page # 3");
//                default:
//                    return null;
//            }
        }

        //return tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        //returns how many fragments there are to swipe between
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

}
