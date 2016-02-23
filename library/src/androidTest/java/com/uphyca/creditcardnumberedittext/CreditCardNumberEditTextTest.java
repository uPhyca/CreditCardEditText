package com.uphyca.creditcardnumberedittext;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.uphyca.creditcardnumberedittext.Assertions.assertThat;

/**
 * Test for {@link CreditCardNumberEditText}.
 */
@RunWith(AndroidJUnit4.class)
public class CreditCardNumberEditTextTest {

    private CreditCardNumberEditText underTest;
    private Context targetContext;

    @Before
    public void setUp() throws Exception {
        targetContext = InstrumentationRegistry.getTargetContext();
        underTest = new CreditCardNumberEditText(targetContext);
        underTest.onFinishInflate();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void assertPreconditions() throws Exception {
        assertThat(underTest).isNotNull();
    }

    /**
     * 空文字の場合、ブランドは不明
     */
    @Test
    public void emptyText() throws Exception {
        assertThat(underTest).hasBrand(CreditCardBrand.UNKNOWN);
    }
}