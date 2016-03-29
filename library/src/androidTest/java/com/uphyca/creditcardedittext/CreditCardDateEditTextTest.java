package com.uphyca.creditcardedittext;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static com.uphyca.creditcardedittext.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test for {@link CreditCardDateEditText}.
 */
@RunWith(AndroidJUnit4.class)
public class CreditCardDateEditTextTest {

    private CreditCardDateEditText underTest;
    private Context targetContext;

    @Mock
    private CreditCardDateListener mockCreditCardDateListener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        targetContext = InstrumentationRegistry.getTargetContext();
        underTest = new CreditCardDateEditText(targetContext);
        underTest.onFinishInflate();
    }

    @Test
    public void assertPreconditions() throws Exception {
        assertThat(underTest).isNotNull();
    }

    /**
     * 未入力
     */
    @Test
    public void unknownDate() throws Exception {
        assertThat(underTest).hasDate(new CreditCardDate("", ""));
    }

    /**
     * 月一桁のみ
     */
    @Test
    public void oneDigitMonth() throws Exception {
        underTest.setText("0");

        assertThat(underTest).hasTextString("0");
        assertThat(underTest).hasDate(new CreditCardDate("0", ""));
    }

    /**
     * 月二桁のみ
     */
    @Test
    public void twoDigitMonth() throws Exception {
        underTest.setText("01");

        assertThat(underTest).hasTextString("01");
        assertThat(underTest).hasDate(new CreditCardDate("01", ""));
    }

    /**
     * 年一桁
     */
    @Test
    public void oneDigitYear() throws Exception {
        underTest.setText("019");

        assertThat(underTest).hasTextString("01/9");
        assertThat(underTest).hasDate(new CreditCardDate("01", "9"));
    }

    /**
     * 年二桁
     */
    @Test
    public void twoDigitYear() throws Exception {
        underTest.setText("0199");

        assertThat(underTest).hasTextString("01/99");
        assertThat(underTest).hasDate(new CreditCardDate("01", "99"));
    }

    /**
     * 1月
     */
    @Test
    public void january() throws Exception {
        underTest.setText("0100");

        assertThat(underTest).hasTextString("01/00");
        assertThat(underTest).hasDate(new CreditCardDate("01", "00"));
    }

    /**
     * 9月
     */
    @Test
    public void september() throws Exception {
        underTest.setText("0900");

        assertThat(underTest).hasTextString("09/00");
        assertThat(underTest).hasDate(new CreditCardDate("09", "00"));
    }

    /**
     * 10月
     */
    @Test
    public void october() throws Exception {
        underTest.setText("1000");

        assertThat(underTest).hasTextString("10/00");
        assertThat(underTest).hasDate(new CreditCardDate("10", "00"));
    }

    /**
     * 12月
     */
    @Test
    public void december() throws Exception {
        underTest.setText("1200");

        assertThat(underTest).hasTextString("12/00");
        assertThat(underTest).hasDate(new CreditCardDate("12", "00"));
    }

    /**
     * 00月
     */
    @Test
    public void invalidMonth00() throws Exception {
        underTest.setText("00");

        assertThat(underTest).hasTextString("0");
        assertThat(underTest).hasDate(new CreditCardDate("0", ""));
    }

    /**
     * 13月
     */
    @Test
    public void invalidMonth13() throws Exception {
        underTest.setText("13");

        assertThat(underTest).hasTextString("1");
        assertThat(underTest).hasDate(new CreditCardDate("1", ""));
    }

    /**
     * 20月
     */
    @Test
    public void invalidMonth2() throws Exception {
        underTest.setText("2");

        assertThat(underTest).hasTextString("");
        assertThat(underTest).hasDate(new CreditCardDate("", ""));
    }

    /**
     * 有効期限のフォーマットでセパレーターが編集されなかった場合の変更を通知する
     */
    @Test
    public void sendDateChanged() throws Exception {
        underTest.addDateListener(mockCreditCardDateListener);
        underTest.setText("0");
        Mockito.verify(mockCreditCardDateListener, Mockito.times(1)).onChanged(new CreditCardDate("0", ""));
    }


    /**
     * 有効期限のフォーマットでセパレーターが編集された場合の変更を通知する
     */
    @Test
    public void sendDateChangedAfterFormatDate() throws Exception {
        underTest.addDateListener(mockCreditCardDateListener);
        underTest.setText("0123");
        Mockito.verify(mockCreditCardDateListener, Mockito.times(1)).onChanged(new CreditCardDate("01", "23"));
    }
}
