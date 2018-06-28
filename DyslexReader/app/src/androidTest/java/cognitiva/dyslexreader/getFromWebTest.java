package cognitiva.dyslexreader;

import android.support.test.espresso.Root;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/***
 * Testa se é possível obter texto válido de um site, acessar a Reader Activity e a Analysis Activity
 *
 * OBS: É necessário que tenha algo na Clipboard do celular antes de rodar esse teste
 */
@RunWith(JUnit4.class)
public class getFromWebTest {
    String siteUrl = "https://www.jlist.com/blog/news/15-year-old-boy-commits-suicide-playing-doki-doki-literature-club/";

    @Rule
    public ActivityTestRule<MainActivity>
        mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void getFromWeb(){

        onView(withId(R.id.btnHttpParser)).perform(click());

        onView(ViewMatchers.withId(R.id.btnPastePopUpHttp))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(ViewMatchers.withId(R.id.btnGetText))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.tvPreview)).check(matches(not(withText(""))));
        onView(withId(R.id.btnReader)).perform(click());
        onView(withId(R.id.tvMainText)).check(matches(not(withText(""))));
        onView(withId(R.id.btnAnalyzeWord)).perform(click());
        onView(withId(R.id.tvSyllable)).check(matches(not(withText(""))));
    }
}
