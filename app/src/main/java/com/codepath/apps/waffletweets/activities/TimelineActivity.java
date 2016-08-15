package com.codepath.apps.waffletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.waffletweets.R;
import com.codepath.apps.waffletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.waffletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.waffletweets.fragments.TweetsListFragment;
import com.codepath.apps.waffletweets.models.Tweet;
import com.codepath.apps.waffletweets.models.User;
import com.codepath.apps.waffletweets.network.TwitterApplication;
import com.codepath.apps.waffletweets.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener{
    //    private TweetsArrayAdapter mTweetsAdapter;
//    private ArrayList<Tweet> mTweets;
    private LinearLayoutManager mLinearLayoutManager;
    private TweetsListFragment mTweetsListFragment;
    private User mCurrentUser;
    private TwitterClient mTwitterClient;
    private TweetsListFragment homeTimelineFragment;
    private TweetsListFragment mentionsTimelineFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

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
        getCurrentUserInfo();

        //set background color of actionbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.waffletweetslogofinal);

        ButterKnife.bind(this);


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
        i.putExtra("user", Parcels.wrap(mCurrentUser));
        startActivity(i);

    }

    @OnClick(R.id.fabCompose)
    public void composeTweet(FloatingActionButton fab) {
        showComposeTweetDialog();
    }

    /**
     * Checks if an active network is available
     *
     * @return true if network is available, false otherwise
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Checks if device is connected to the internet
     *
     * @return true if device is connected, false otherwise
     */
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Alert user to connect to network
     */
    protected void callNetworkDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(TimelineActivity.this);
        alert.setTitle("No Connection Available");
        alert.setMessage("Please check your network connection, and try again!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }


    /**
     * Calls Filter Dialog Fragment
     */
    private void showComposeTweetDialog() {
        FragmentManager fm = getSupportFragmentManager();

        //pass in current user
        Bundle args = new Bundle();
        args.putParcelable("currUser", Parcels.wrap(mCurrentUser));

        ComposeTweetDialogFragment composeTweetDialogFragment = newInstance();
        composeTweetDialogFragment.setArguments(args);
        composeTweetDialogFragment.show(fm, "compose_tweet");
    }

    /**
     * Used for creating ComposeTweetDialogFragment and binding arguments
     *
     * @return
     */
    static ComposeTweetDialogFragment newInstance() {
        ComposeTweetDialogFragment f = new ComposeTweetDialogFragment();
        return f;
    }



    private void getCurrentUserInfo() {
        mTwitterClient.getUserInfo(new JsonHttpResponseHandler() {
            //success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
                Log.d("DEBUG", jsonResponse.toString());


                mCurrentUser = User.fromJSON(jsonResponse);

                if (mCurrentUser == null){
                    System.out.println("DEBUGGY USER IS NULL");
                }

                // Log.d("DEBUG", mTweetsAdapter.toString());
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });


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

                homeTimelineFragment.addTweetAtTop(tweet);

            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        });


    }

    /**
     * Checks if network is available and if device is online. If connected, it refreshes timeline items
     */
    private void refreshItems2() {

        if (isNetworkAvailable() && isOnline()) {
//
//            mTweets.clear();
//            mTweetsAdapter.notifyDataSetChanged();
//            populateTimeline(null);

            // refresh complete
//            mMaterialRefreshLayout.finishRefresh();

            // load more refresh complete
//            mMaterialRefreshLayout.finishRefreshLoadMore();
        } else {
            callNetworkDialog();

        }
    }


    @Override
    public void onFinishComposeTweetDialog(Tweet tweet) {
        System.out.println("DEBUGGY TWEET: " + tweet.getUser().getName());

        postTweet(tweet);

        refreshItems();
    }

    private void refreshItems() {
        System.out.println("DEBUGGY in Timeline");
//        TweetsListFragment fragmentDemo = (TweetsListFragment)
//                getSupportFragmentManager().findFragmentById(R.id.).findFragmentByTag("HOME_FRAGMENT");
//        fragmentDemo.refreshItems();


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
    public class TweetsPagerAdapter extends FragmentPagerAdapter{
        private String tabTitles[] = {"Home", "Mentions"};

        //adapter gets the manager to insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        //the order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {//return fragment for position
            if (position ==0){
                homeTimelineFragment = HomeTimelineFragment.newInstance(mCurrentUser);
                return homeTimelineFragment;
            }
            else if (position ==1){
                mentionsTimelineFragment = MentionsTimelineFragment.newInstance(mCurrentUser);
                return mentionsTimelineFragment;
            }
            else{
                return null;
            }
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
