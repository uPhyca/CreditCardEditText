package com.uphyca.creditcardedittext;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.uphyca.creditcardedittext.subject.CreditCardDateEditTextSubject.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link CreditCardDateEditText}.
 */
@RunWith(AndroidJUnit4.class)
public class CreditCardDateEditTextTest {

    private CreditCardDateEditText underTest;
    private CreditCardDateListener mockCreditCardDateListener;

    @Before
    public void setUp() {
        mockCreditCardDateListener = mock(CreditCardDateListener.class);
        underTest = new CreditCardDateEditText(ApplicationProvider.getApplicationContext());
        underTest.onFinishInflate();
    }

    @Test
    public void assertPreconditions() {
        assertThat(underTest).isNotNull();
    }

    /**
     * 未入力
     */
    @Test
    public void unknownDate() {
        assertThat(underTest).hasDate(new CreditCardDate("", ""));
    }

    /**
     * 月一桁のみ
     */
    @Test
    public void oneDigitMonth() {
        underTest.setText("0");

        assertThat(underTest).hasTextString("0");
        assertThat(underTest).hasDate(new CreditCardDate("0", ""));
    }

    /**
     * 月二桁のみ
     */
    @Test
    public void twoDigitMonth() {
        underTest.setText("01");

        assertThat(underTest).hasTextString("01");
        assertThat(underTest).hasDate(new CreditCardDate("01", ""));
    }

    /**
     * 年一桁
     */
    @Test
    public void oneDigitYear() {
        underTest.setText("019");

        assertThat(underTest).hasTextString("01/9");
        assertThat(underTest).hasDate(new CreditCardDate("01", "9"));
    }

    /**
     * 年二桁
     */
    @Test
    public void twoDigitYear() {
        underTest.setText("0199");

        assertThat(underTest).hasTextString("01/99");
        assertThat(underTest).hasDate(new CreditCardDate("01", "99"));
    }

    /**
     * 1月
     */
    @Test
    public void january() {
        underTest.setText("0100");

        assertThat(underTest).hasTextString("01/00");
        assertThat(underTest).hasDate(new CreditCardDate("01", "00"));
    }

    /**
     * 9月
     */
    @Test
    public void september() {
        underTest.setText("0900");

        assertThat(underTest).hasTextString("09/00");
        assertThat(underTest).hasDate(new CreditCardDate("09", "00"));
    }

    /**
     * 10月
     */
    @Test
    public void october() {
        underTest.setText("1000");

        assertThat(underTest).hasTextString("10/00");
        assertThat(underTest).hasDate(new CreditCardDate("10", "00"));
    }

    /**
     * 12月
     */
    @Test
    public void december() {
        underTest.setText("1200");

        assertThat(underTest).hasTextString("12/00");
        assertThat(underTest).hasDate(new CreditCardDate("12", "00"));
    }

    /**
     * 00月
     */
    @Test
    public void invalidMonth00() {
        underTest.setText("00");

        assertThat(underTest).hasTextString("0");
        assertThat(underTest).hasDate(new CreditCardDate("0", ""));
    }

    /**
     * 13月
     */
    @Test
    public void invalidMonth13() {
        underTest.setText("13");

        assertThat(underTest).hasTextString("1");
        assertThat(underTest).hasDate(new CreditCardDate("1", ""));
    }

    /**
     * 20月
     */
    @Test
    public void invalidMonth2() {
        underTest.setText("2");

        assertThat(underTest).hasTextString("");
        assertThat(underTest).hasDate(new CreditCardDate("", ""));
    }

    /**
     * 有効期限のフォーマットでセパレーターが編集されなかった場合の変更を通知する
     */
    @Test
    public void sendDateChanged() {
        underTest.addDateListener(mockCreditCardDateListener);
        underTest.setText("0");
        verify(mockCreditCardDateListener, times(1))
                .onChanged(new CreditCardDate("0", ""));
    }


    /**
     * 有効期限のフォーマットでセパレーターが編集された場合の変更を通知する
     */
    @Test
    public void sendDateChangedAfterFormatDate() {
        underTest.addDateListener(mockCreditCardDateListener);
        underTest.setText("0123");
        verify(mockCreditCardDateListener, times(1))
                .onChanged(new CreditCardDate("01", "23"));
    }
}
