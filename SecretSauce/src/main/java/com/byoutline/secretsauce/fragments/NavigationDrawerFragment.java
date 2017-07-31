package com.byoutline.secretsauce.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.byoutline.secretsauce.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 * Based on implementation of NavigationDrawerFragment from
 * https://github.com/GoogleChrome/chromium-webview-samples
 * licensed on Apache 2.0 license.
 */
public abstract class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    public static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks itemSelectListener;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    protected ActionBarDrawerToggle mDrawerToggle;

    protected DrawerLayout mDrawerLayout;
    protected ListView mDrawerListView;
    protected View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    protected boolean showingLearnDrawerEnabled = true;
    private View mDrawerView;

    protected Toolbar toolbar;
    protected ArrayAdapter<MenuOption> mAdapter;


    @LayoutRes
    protected abstract int getNavigationDrawerFragmentLayoutId();

    @IdRes
    protected abstract int getNavigationDrawerListId();

    protected abstract ArrayAdapter<MenuOption> getListAdapter(Activity activity);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerView = inflater.inflate(getNavigationDrawerFragmentLayoutId(), container, false);
        mDrawerListView = (ListView) mDrawerView.findViewById(getNavigationDrawerListId());

        setUpListeners();
        setUpAdapters();

        return mDrawerView;
    }

    private void setUpAdapters() {
        mAdapter = getListAdapter(getActivity());
        mDrawerListView.setAdapter(mAdapter);
    }

    private void setUpListeners() {
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
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
    public void setUp(final ActionBarDrawerToggle.DelegateProvider delegateProvider, @IdRes int fragmentId,
                      DrawerLayout drawerLayout,
                      Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        this.toolbar = toolbar;


        // set a custom shadow that overlays the add_tracker_menu content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.ss_drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,
                R.string.ss_navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.ss_navigation_drawer_close  /* "close drawer" description for accessibility */
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

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        Drawable themeUpIndicator = delegateProvider.getDrawerToggleDelegate().getThemeUpIndicator();
        mDrawerToggle.setHomeAsUpIndicator(themeUpIndicator);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (showLearnDrawerNeeded()) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });//
    }

    private boolean showLearnDrawerNeeded() {
        return !mUserLearnedDrawer && !mFromSavedInstanceState && showingLearnDrawerEnabled;
    }

    public void setShowingLearnDrawerEnabled(boolean showingLearnDrawerEnabled) {
        this.showingLearnDrawerEnabled = showingLearnDrawerEnabled;
    }

    public void setDrawerIndicatorEnabled(boolean show) {
        mDrawerToggle.setDrawerIndicatorEnabled(show);
    }

    public void selectItem(int position) {
        if (mAdapter == null) {
            return;
        }
        MenuOption menuOption = mAdapter.getItem(position);

        if (menuOption != null) {
            mCurrentSelectedPosition = position;
            if (mDrawerListView != null) {
                mDrawerListView.setItemChecked(position, true);
            }
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }

            itemSelectListener.onNavigationDrawerItemSelected(menuOption);
        } else {

            mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        }
    }

    public int getCurrentSelectedPosition() {
        return mCurrentSelectedPosition;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationDrawerCallbacks) {
            itemSelectListener = (NavigationDrawerCallbacks) context;
        } else {
            itemSelectListener = new NavigationDrawerCallbacksStub();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemSelectListener = new NavigationDrawerCallbacksStub();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerToggle.isDrawerIndicatorEnabled()) {
                return mDrawerToggle.onOptionsItemSelected(item);
            } else {
                getActivity().onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void closeDrawers() {
        mDrawerLayout.closeDrawers();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         *
         * @param position
         */
        Class<? extends Fragment> onNavigationDrawerItemSelected(MenuOption position);
    }

    public class NavigationDrawerCallbacksStub implements NavigationDrawerCallbacks {

        @Override
        public Class<? extends Fragment> onNavigationDrawerItemSelected(MenuOption position) {
            return null;
        }
    }
}
