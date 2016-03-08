package pl.mareklangiewicz.myactivities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;

import pl.mareklangiewicz.mydrawables.MyLivingDrawable;
import pl.mareklangiewicz.mydrawables.MyMagicLinesDrawable;
import pl.mareklangiewicz.myviews.IMyNavigation;

import static android.animation.ObjectAnimator.ofInt;
import static android.animation.ObjectAnimator.ofPropertyValuesHolder;
import static android.animation.PropertyValuesHolder.ofFloat;
import static android.view.View.ALPHA;
import static android.view.View.TRANSLATION_Y;
import static pl.mareklangiewicz.myactivities.R.id.magic_underline_view;
import static pl.mareklangiewicz.myactivities.R.id.section_my_pie_tests;
import static pl.mareklangiewicz.myactivities.R.id.text_home_page;
import static pl.mareklangiewicz.myactivities.R.layout.ma_my_test_global_header;
import static pl.mareklangiewicz.myactivities.R.menu.ma_my_test_global;

/**
 * Test activity presenting most of the MyBlocks functionality
 */

public final class MyTestActivity extends pl.mareklangiewicz.myactivities.MyActivity {

    private final MyLivingDrawable mMyMagicLinesDrawable = new MyMagicLinesDrawable();
    private @Nullable ObjectAnimator mHomePageAnimator;
    private @Nullable ObjectAnimator mMagicLinesAnimator;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log.i("Hello world!");
        log.d("some boring debug message...");
        log.w("Warning!... just kidding...");

        //noinspection ConstantConditions
        getGlobalNavigation().inflateMenu(ma_my_test_global);
        getGlobalNavigation().inflateHeader(ma_my_test_global_header);

        //noinspection ConstantConditions
        View underline = getGlobalNavigation().getHeader().findViewById(magic_underline_view);
        mMyMagicLinesDrawable.setColor(0x30ffffff);
        mMyMagicLinesDrawable.setStrokeWidth(dp2px(4));
        underline.setBackground(mMyMagicLinesDrawable);

        View homepage = getGlobalNavigation().getHeader().findViewById(text_home_page);

        PropertyValuesHolder pvha = ofFloat(ALPHA, 0f, 0f, 1f);
        PropertyValuesHolder pvhy = ofFloat(TRANSLATION_Y, -50f, -50f, 0f);

        mHomePageAnimator = ofPropertyValuesHolder(homepage, pvha, pvhy);
        mHomePageAnimator.setInterpolator(new LinearInterpolator());

        mMagicLinesAnimator = ofInt(mMyMagicLinesDrawable, "level", 0, 10000);
        mMagicLinesAnimator.setDuration(1000).setInterpolator(new LinearInterpolator());

        if(savedInstanceState == null) {
            selectGlobalItem(section_my_pie_tests);
        }
    }

    @Override public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        if(drawerView != mGlobalNavigationView)
            return;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(mHomePageAnimator != null)
                mHomePageAnimator.setCurrentFraction(slideOffset);
        }
    }


    @Override public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        if(drawerView != mGlobalNavigationView)
            return;
        if(mMagicLinesAnimator != null)
            if(!mMagicLinesAnimator.isStarted())
                mMagicLinesAnimator.start();
    }

    @Override public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        if(drawerView != mGlobalNavigationView)
            return;
        if(mMagicLinesAnimator != null)
            mMagicLinesAnimator.cancel();
        mMyMagicLinesDrawable.setLevel(0);
    }

    @Override public boolean onItemSelected(IMyNavigation nav, MenuItem item) {

        boolean done = super.onItemSelected(nav, item);

        if(done)
            return true;

        @IdRes int id = item.getItemId();

        if(id == R.id.action_whats_up) {
            log.i("[SNACK][SHORT]What's up mate?");
            return true;
        }
        else if(id == R.id.action_settings) {
            log.w("[SNACK]TODO: some settings (or not)..");
            return true;
        }
        else if(id == R.id.action_destroy_something) {
            log.a("[SNACK]BUM!"); /* throw new InternalError("BUM!"); */
            return true;
        }

        return false;
    }

}
