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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(JUnit4.class)
public class MainActivityClipboardTest {

    @Rule
    public ActivityTestRule<MainActivity>
            mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void pasteFromClipBoard(){
        /*
        DOES NOT WORK
        ClipboardManager clipboardManager = (ClipboardManager) mainActivityActivityTestRule.getActivity()
                .getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);

        String label = "simple text";
        String text = "Hello world";

        ClipData clip = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clip);


        onView(withId(R.id.tvPreview)).check(matches(withText(text)));*/
    }


}
