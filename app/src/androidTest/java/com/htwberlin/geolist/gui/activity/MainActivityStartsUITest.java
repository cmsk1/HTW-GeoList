package com.htwberlin.geolist.gui.activity;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.htwberlin.geolist.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityStartsUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_BACKGROUND_LOCATION",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void mainActivityStartsUITest() {
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.addListBtn), withContentDescription("Add new List"),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction viewGroup = onView(
                allOf(withId(R.id.action_bar),
                        withParent(allOf(withId(R.id.action_bar_container),
                                withParent(withId(R.id.decor_content_parent)))),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.tasklists),
                        withParent(withParent(withId(R.id.nav_host_fragment))),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));
    }
}
