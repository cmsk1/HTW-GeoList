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
import com.htwberlin.geolist.data.interfaces.TaskListRepositoryImpl;
import com.htwberlin.geolist.data.models.Task;
import com.htwberlin.geolist.data.sqlite.DatabaseHelper;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
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
public class SaveAndEditDataInListE2ETest {

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
    public void saveAndEditDataInListE2ETest() {

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
        appCompatEditText.perform(replaceText("Testliste 1"), closeSoftKeyboard());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.btnSaveList), withContentDescription("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                3),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        assertEquals(1, repo.getAllLists().size());
        assertEquals("Testliste 1", repo.getAllLists().get(0).getDisplayName());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.tasklists),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.btnNewTask), withContentDescription("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                6),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        ViewInteraction editText = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.custom),
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0)),
                        0),
                        isDisplayed()));
        editText.perform(replaceText("Testaufgabe 1"), closeSoftKeyboard());



        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton2.perform(scrollTo(), click());

        assertEquals("Testaufgabe 1", repo.getAllLists().get(0).getTasks().iterator().next().getDescription());

        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.btnNewTask), withContentDescription("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                6),
                        isDisplayed()));
        floatingActionButton4.perform(click());

        ViewInteraction editText2 = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.custom),
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0)),
                        0),
                        isDisplayed()));
        editText2.perform(replaceText("Testaufgabe 2"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction floatingActionButton5 = onView(
                allOf(withId(R.id.btnNewTask), withContentDescription("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                6),
                        isDisplayed()));
        floatingActionButton5.perform(click());

        ViewInteraction editText3 = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.custom),
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0)),
                        0),
                        isDisplayed()));
        editText3.perform(replaceText("Testaufgabe 3"), closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction floatingActionButton6 = onView(
                allOf(withId(R.id.btnNewTask), withContentDescription("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                6),
                        isDisplayed()));
        floatingActionButton6.perform(click());

        ViewInteraction editText4 = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.custom),
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0)),
                        0),
                        isDisplayed()));
        editText4.perform(replaceText("Testaufgabe 4"), closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton5.perform(scrollTo(), click());

        ViewInteraction checkBox = onView(
                allOf(withId(R.id.checkBox), withContentDescription("checkBox_0"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                0),
                        isDisplayed()));
        checkBox.perform(click());

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.taskEntryEdit), withContentDescription("editTask_2"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        imageButton.perform(click());

        assertEquals(4, repo.getAllLists().get(0).getTasks().size());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button3), withText("Löschen!"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                0)));
        appCompatButton6.perform(scrollTo(), click());

        assertEquals(3, repo.getAllLists().get(0).getTasks().size());

        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.taskEntryEdit), withContentDescription("editTask_0"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.cardview.widget.CardView")),
                                        0),
                                2),
                        isDisplayed()));
        imageButton2.perform(click());

        ViewInteraction editText5 = onView(
                allOf(withText("Testaufgabe 1"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editText5.perform(replaceText("Testaufgabe 1 mit Änderung"));

        ViewInteraction editText6 = onView(
                allOf(withText("Testaufgabe 1 mit Änderung"),
                        childAtPosition(
                                allOf(withId(android.R.id.custom),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        editText6.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(android.R.id.button1), withText("Speichern"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton7.perform(scrollTo(), click());

        ArrayList<Task> newList = new ArrayList<Task>(repo.getAllLists().get(0).getTasks());

        assertEquals(3, newList.size());
        assertEquals("Testaufgabe 1 mit Änderung", newList.get(0).getDescription());
        assertEquals("Testaufgabe 2", newList.get(1).getDescription());
        assertEquals("Testaufgabe 4", newList.get(2).getDescription());

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
