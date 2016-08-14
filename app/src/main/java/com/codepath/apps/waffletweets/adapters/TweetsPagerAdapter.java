//package com.codepath.apps.waffletweets.adapters;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//
//import com.codepath.apps.waffletweets.fragments.HomeTimelineFragment;
//import com.codepath.apps.waffletweets.fragments.MentionsTimelineFragment;
//
//
////return order of fragments in view pager
//public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter{
//    private String tabTitles[] = {"Home", "Mentions"};
//
//    //adapter gets the manager to insert or remove fragment from activity
//    public TweetsPagerAdapter(FragmentManager fm){
//        super(fm);
//    }
//
//    //the order and creation of fragments within the pager
//    @Override
//    public Fragment getItem(int position) {//return fragment for position
//        if (position ==0){
//            return new HomeTimelineFragment();
//        }
//        else if (position ==1){
//            return new MentionsTimelineFragment();
//        }
//        else{
//            return null;
//        }
//
////            switch (position) {
////                case 0: // Fragment # 0 - This will show FirstFragment
////                    return FirstFragment.newInstance(0, "Page # 1");
////                case 1: // Fragment # 0 - This will show FirstFragment different title
////                    return FirstFragment.newInstance(1, "Page # 2");
////                case 2: // Fragment # 1 - This will show SecondFragment
////                    return SecondFragment.newInstance(2, "Page # 3");
////                default:
////                    return null;
////            }
//    }
//
//    //return tab title
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabTitles[position];
//    }
//
//    //returns how many fragments there are to swipe between
//    @Override
//    public int getCount() {
//        return tabTitles.length;
//    }
//}