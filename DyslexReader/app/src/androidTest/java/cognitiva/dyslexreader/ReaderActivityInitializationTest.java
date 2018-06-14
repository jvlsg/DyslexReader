package cognitiva.dyslexreader;

import static android.support.test.espresso.intent.Intents.intending;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class ReaderActivityInitializationTest {
    @Rule public ActivityTestRule<ReaderActivity>
            readerActivityActivityTestRule = new ActivityTestRule<>(ReaderActivity.class);

    @Before
    /**
     * Garante que todas as intents externas sejam bloqueadas
     */
    public void stubAllExternalIntents(){
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void initializeTextView(){
        /*intending(hasComponent(hasShortClassName(".MainActivity")))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, ));*/
    }
}
