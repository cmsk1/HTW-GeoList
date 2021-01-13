package com.htwberlin.geolist.gui.activity;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.data.interfaces.DataStorageImpl;
import com.htwberlin.geolist.data.interfaces.TaskListRepository;
import com.htwberlin.geolist.data.interfaces.TaskListRepositoryImpl;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(AndroidJUnit4.class)
// WARNING: Espresso Test Recorder was paused during recording.
// The generated test may be missing actions which might lead to unexpected behavior.
public class SaveListAndSetMarkerToMapE2ETest {

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
    public void saveListeAndSetMarkerToMapE2ETest() {

        Context context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
        DatabaseHelper db = new DatabaseHelper(context);
        TaskListRepositoryImpl repo = (TaskListRepositoryImpl) new DataStorageImpl(context).getTaskRepo();
        db.clearAllTables();

        assertEquals(0, repo.getAllLists().size());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.addListBtn), withContentDescription("Add new List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                1),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.titleEdit),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Testliste"), closeSoftKeyboard());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.btnSaveList), withContentDescription("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.tasklists),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.btnNotify), withContentDescription("Add new List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                5),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnSaveMarkerForList), withText("Speichern"),
                        childAtPosition(
                                allOf(withId(R.id.layoutBtnGroup),
                                        childAtPosition(
                                                withId(R.id.mapSelectFrame),
                                                1)),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open Menu"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(withId(R.id.mapItem),
                        childAtPosition(
                                allOf(withId(R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.navView),
                                                0)),
                                1),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        assertEquals(1, repo.getAllLists().size());
        assertEquals(52.5162968364215, repo.getAllLists().get(0).getRememberByLocation().getLatitude(),0.1);
        assertEquals(13.3777271617543, repo.getAllLists().get(0).getRememberByLocation().getLongitude(),0.1);

        db.clearAllTables();
        db.close();
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
