package cognitiva.dyslexreader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(JUnit4.class)
/***
 * Testa se é possível colar algo diferente de nulo e acessar a Reader Activity
 *
 * OBS: É necessário que tenha algo na Clipboard do celular antes de rodar esse teste
 */
public class MainActivityClipboardTest {

    @Rule
    public ActivityTestRule<MainActivity>
            mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void pasteFromClipBoard(){

        onView(withId(R.id.btnPaste)).perform(click());
        onView(withId(R.id.tvPreview)).check(matches(not(withText(""))));
        onView(withId(R.id.btnReader)).perform(click());
        onView(withId(R.id.tvMainText)).check(matches(not(withText(""))));
    }


}
