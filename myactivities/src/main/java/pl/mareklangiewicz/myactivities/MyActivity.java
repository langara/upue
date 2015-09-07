package pl.mareklangiewicz.myactivities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.noveogroup.android.log.MyLogger;

import pl.mareklangiewicz.myviews.IMyCommander;
import pl.mareklangiewicz.myviews.IMyNavigation;
import pl.mareklangiewicz.myviews.MyNavigationView;
import static pl.mareklangiewicz.myutils.MyText.*;
// TODO: Hide left menu icon and block left drawer if global menu is empty
// TODO: Hide right menu icon and block right drawer if local menu is empty

public class MyActivity extends AppCompatActivity implements IMyCommander {

    static final boolean VERBOSE = true;
    //TODO later: implement it as a build time switch for user

    /**
     * Default logger for use in UI thread
     */
    protected MyLogger log = MyLogger.sMyDefaultUILogger;

    static public final String PREFIX_FRAGMENT = "fragment:";

    static public final String TAG_LOCAL_FRAGMENT = "tag_local_fragment";

    protected DrawerLayout mGlobalDrawerLayout;
    protected CoordinatorLayout mCoordinatorLayout;
    protected AppBarLayout mAppBarLayout;
    protected Toolbar mToolbar;
    protected DrawerLayout mLocalDrawerLayout;
    protected FrameLayout mLocalFrameLayout;
    protected MyNavigationView mLocalNavigationView;
    protected MyNavigationView mGlobalNavigationView;
    protected MyNavigation mGlobalNavigation;
    protected MyNavigation mLocalNavigation;

    protected FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(VERBOSE) log.v("%s.%s(%s)", this.getClass().getSimpleName(), "onCreate", toStr(savedInstanceState));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);

        mGlobalDrawerLayout = (DrawerLayout) findViewById(R.id.ma_global_drawer_layout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.ma_global_coordinator_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.ma_app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.ma_toolbar);
        mLocalDrawerLayout = (DrawerLayout) findViewById(R.id.ma_local_drawer_layout);
        mLocalFrameLayout = (FrameLayout) findViewById(R.id.ma_local_frame_layout);
        mGlobalNavigationView = (MyNavigationView) findViewById(R.id.ma_global_navigation_view);
        mLocalNavigationView = (MyNavigationView) findViewById(R.id.ma_local_navigation_view);

        mGlobalNavigation = new MyNavigation(mGlobalNavigationView);
        mLocalNavigation = new MyNavigation(mLocalNavigationView);

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp); //FIXME later: better animated icon (and for local navigation too..)
        mToolbar.setNavigationContentDescription(R.string.ma_global_navigation_description);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGlobalDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //TODO: implement icon for right menu (local menu)

        mFAB = (FloatingActionButton) findViewById(R.id.ma_fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.w("[SNACK]FAB Clicked!");
            }
        });

        log.setSnackView(mCoordinatorLayout);
    }

    @Override
    protected void onDestroy() {
        if(VERBOSE) log.v("%s.%s()", this.getClass().getSimpleName(), "onDestroy");
        log.setSnackView(null);
        super.onDestroy();
    }

    public class MyNavigation implements IMyNavigation {

        private @NonNull MyNavigationView mMyNavigationView;

        MyNavigation(@NonNull MyNavigationView myNavigationView) {
            mMyNavigationView = myNavigationView;
            mMyNavigationView.setNavigationItemSelectedListener(this);
        }

        @Override public @Nullable Menu getMenu() { return mMyNavigationView.getMenu(); }
        @Override public @Nullable View getHeader() { return mMyNavigationView.getHeader(); }

        @Override public void clearMenu() { mMyNavigationView.clearMenu(); }
        @Override public void clearHeader() { mMyNavigationView.clearHeader(); }

        @Override public void inflateMenu(@MenuRes int id) { mMyNavigationView.inflateMenu(id); }
        @Override public void inflateHeader(@LayoutRes int id) { mMyNavigationView.inflateHeader(id); }

        @Override public boolean selectMenuItem(@IdRes int id) { return mMyNavigationView.selectMenuItem(id); }

        /**
         * Override if you want to do something different that clearing local navigation menu and header
         * before new local fragment creation, or if you want to cancel it (by returning false)
         * @param name Class name of new fragment to instantiate
         * @return  False will cancel new fragment creation.
         */
        protected boolean onNewLocalFragment(String name) {
            mLocalNavigation.clearHeader();
            mLocalNavigation.clearMenu();
            return true;
        }

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            if(mGlobalDrawerLayout != null)
                mGlobalDrawerLayout.closeDrawers();
            if(mLocalDrawerLayout != null)
                mLocalDrawerLayout.closeDrawers();
            String ctitle = item.getTitleCondensed().toString();
            FragmentManager fm = getFragmentManager();
            Fragment f;
            if(ctitle.startsWith(PREFIX_FRAGMENT)) {
                ctitle = ctitle.substring(PREFIX_FRAGMENT.length());
                boolean ok = onNewLocalFragment(ctitle);
                if(!ok)
                    return false;
                f = Fragment.instantiate(MyActivity.this, ctitle);
                fm.beginTransaction().replace(R.id.ma_local_frame_layout, f, TAG_LOCAL_FRAGMENT).commit(); //TODO later: some animation?
                return true;
            }
            //TODO later: support for other prefixes (like starting activities/services) - but first lets make MyIntent work with new MyBlocks
            //TODO later: if we want to have an engine for starting activities/services here - we should put almost all logic from MyIntent to MyBlocks...
            //TODO later: and MyIntent would be only a thin wrapper.. and.. that's a great idea!
            //TODO later: menu api already has some api for launching intents (MenuItem.setIntent) - but our MyIntent engine is better!

            f = fm.findFragmentByTag(TAG_LOCAL_FRAGMENT);
            if(f instanceof IMyNavigation) {
                IMyNavigation imn = (IMyNavigation) f;
                if(imn.onNavigationItemSelected(item))
                    return true;
            }
            return false;
        }
    }
    @Override
    public IMyNavigation getGlobalNavigation() {
        return mGlobalNavigation;
    }

    @Override
    public IMyNavigation getLocalNavigation() {
        return mLocalNavigation;
    }


}
