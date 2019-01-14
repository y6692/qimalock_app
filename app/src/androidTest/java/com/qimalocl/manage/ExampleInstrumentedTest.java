package com.qimalocl.manage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.qimalocl.manage", appContext.getPackageName());


        String str1="2015-02-08 20:20:20";
        String str2="2015-01-08 10:10:10";
        int res=str1.compareTo(str2);
        if(res>0)
            System.out.println("str1>str2");
        else if(res==0)
            System.out.println("str1=str2");
        else
            System.out.println("str1<str2");

    }
}
