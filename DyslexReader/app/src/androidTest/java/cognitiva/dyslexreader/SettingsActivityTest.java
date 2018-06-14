package cognitiva.dyslexreader;

import android.support.test.rule.ActivityTestRule;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;

public class SettingsActivityTest {
    @Rule
    public ActivityTestRule<SettingsActivity>
            mainActivityActivityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    @Test
    public void pasteFromClipBoard(){
        onView(withId(R.string.whiteNoiseKey)).perform(click());
        onView(withId(R.string.whiteNoiseKey)).check(matches(isEnabled()));
    }
}
