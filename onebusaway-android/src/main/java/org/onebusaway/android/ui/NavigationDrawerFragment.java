/*
 * Copyright 2014 Google Inc., University of South Florida (sjbarbeau@gmail.com) All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Portions of code taken from the Google I/0 2014 (https://github.com/google/iosched)
 * and a generated NavigationDrawer app from Android Studio, modified for OneBusAway by USF
 */
package org.onebusaway.android.ui;

import org.onebusaway.android.R;
import org.onebusaway.android.util.UIUtils;
import org.onebusaway.android.view.ScrimInsetsScrollView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    public static final String TAG = "NavDrawerFragment";

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    // symbols for navdrawer items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    protected static final int NAVDRAWER_ITEM_NEARBY = 0;

    protected static final int NAVDRAWER_ITEM_STARRED_STOPS = 1;

    protected static final int NAVDRAWER_ITEM_MY_REMINDERS = 2;

    protected static final int NAVDRAWER_ITEM_SETTINGS = 3;

    protected static final int NAVDRAWER_ITEM_HELP = 4;

    protected static final int NAVDRAWER_ITEM_SEND_FEEDBACK = 5;

    protected static final int NAVDRAWER_ITEM_INVALID = -1;

    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;

    protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

    // Currently selected navigation drawer item (must be value of one of the constants above)
    private int mCurrentSelectedPosition = 0;

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_nearby,
            R.string.navdrawer_item_starred_stops,
            R.string.navdrawer_item_my_reminders,
            R.string.navdrawer_item_settings,
            R.string.navdrawer_item_help,
            R.string.navdrawer_item_send_feedback
    };

    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_drawer_maps_place,  // Nearby
            R.drawable.ic_drawer_star, // Starred Stops
            R.drawable.ic_drawer_alarm, // My reminders
            0, // Settings
            0, // Help
            0, // Send feedback
    };

    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;

    private ViewGroup mDrawerItemsListContainer;

    private View mFragmentContainerView;

    private boolean mFromSavedInstanceState;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
            Log.d(TAG, "Using position from savedInstanceState = " + mCurrentSelectedPosition);
        } else {
            // Try to get the saved position from preferences
            mCurrentSelectedPosition = sp.getInt(STATE_SELECTED_POSITION, NAVDRAWER_ITEM_NEARBY);
            Log.d(TAG, "Using position from preferences = " + mCurrentSelectedPosition);
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mDrawerItemsListContainer = (ViewGroup) inflater
                .inflate(R.layout.navdrawer_list, container, false);

        return mDrawerItemsListContainer;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        int selfItem = mCurrentSelectedPosition;
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        if (mDrawerLayout == null) {
            return;
        }

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        ScrimInsetsScrollView navDrawer = (ScrimInsetsScrollView)
                mDrawerLayout.findViewById(R.id.navdrawer);
        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            mDrawerLayout = null;
            return;
        }

        // populate the nav drawer with the correct items
        populateNavDrawer();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * Sets the currently selected navigation drawer item, based on the provided position
     * parameter,
     * which must be one of the NAVDRAWER_ITEM_* contants in this class.
     *
     * @param position the item to select in the navigation drawer - must be one of the
     *                 NAVDRAWER_ITEM_* contants in this class
     */
    public void selectItem(int position) {
        setSelectedNavDrawerItem(position);
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (!isNewActivityItem(itemId)) {
            // We only change the selected item if it doesn't launch a new activity
            mCurrentSelectedPosition = itemId;

            // Save the selected position as a preference
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            sp.edit().putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition).apply();
        }
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving position = " + mCurrentSelectedPosition);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

//        if (item.getItemId() == R.id.action_example) {
//            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {

        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    /** Populates the navigation drawer with the appropriate items. */
    private void populateNavDrawer() {
        mNavDrawerItems.clear();

        mNavDrawerItems.add(NAVDRAWER_ITEM_NEARBY);
        mNavDrawerItems.add(NAVDRAWER_ITEM_STARRED_STOPS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_MY_REMINDERS);

        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);

        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_HELP);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEND_FEEDBACK);

        createNavDrawerItems();
    }

    private void createNavDrawerItems() {
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = mCurrentSelectedPosition == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else if (itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }
        View view = getActivity().getLayoutInflater().inflate(layoutToInflate, container, false);

        if (isSeparator(itemId)) {
            // we are done
            UIUtils.setAccessibilityIgnore(view);
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));

        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(itemId);
            }
        });

        return view;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // Don't do any formatting
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        /**
         * Configure its appearance according to whether or not it's selected.  Certain items
         * (e.g., Settings) don't get formatted upon selection, since they open a new activity.
         */
        if (selected) {
            if (isNewActivityItem(itemId)) {
                // Don't change any formatting, since this is a category that launches a new activity
                return;
            } else {
                // Show the category as highlighted by changing background, text, and icon color
                view.setSelected(true);
                titleView.setTextColor(
                        getResources().getColor(R.color.navdrawer_text_color_selected));
                iconView.setColorFilter(
                        getResources().getColor(R.color.navdrawer_icon_tint_selected));
            }
        } else {
            // Show the category as not highlighted, if its not currently selected
            if (itemId != mCurrentSelectedPosition) {
                view.setSelected(false);
                titleView.setTextColor(getResources().getColor(R.color.navdrawer_text_color));
                iconView.setColorFilter(getResources().getColor(R.color.navdrawer_icon_tint));
            }
        }
    }

    private boolean isSeparator(int itemId) {
        return itemId == NAVDRAWER_ITEM_SEPARATOR || itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
    }

    /**
     * Returns true if this is an item that should not allow selection (e.g., Settings),
     * because they launch a new Activity and aren't part of this screen, false if its selectable
     * and changes the current UI via a new fragment
     *
     * @return true if this is an item that should not allow selection (e.g., Settings),
     * because they launch a new Activity and aren't part of this screen, false if its selectable
     * and changes the current UI via a new fragment
     */
    private boolean isNewActivityItem(int itemId) {
        return itemId == NAVDRAWER_ITEM_SETTINGS ||
                itemId == NAVDRAWER_ITEM_HELP ||
                itemId == NAVDRAWER_ITEM_SEND_FEEDBACK;
    }
}
