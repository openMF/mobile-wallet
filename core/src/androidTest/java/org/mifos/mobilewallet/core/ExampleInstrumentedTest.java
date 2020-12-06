package org.mifos.mobilewallet.core;
import static org.junit.Assert.assertEquals;
import android.content.Context;
import android.support.test.*;
import org.junit.*;
/**
 * Instrumentation test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("org.mifos.mobilewallet.core.test", appContext.getPackageName());
    }
}
