package com.digits.sdk.android;

import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

public class DigitsAndroidTestCase extends AndroidTestCase {

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    setContext(RuntimeEnvironment.application);
  }

  @Test
  public void test() {

  }
}
