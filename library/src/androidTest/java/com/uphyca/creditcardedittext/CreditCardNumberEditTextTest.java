package com.uphyca.creditcardedittext;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static com.uphyca.creditcardedittext.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test for {@link CreditCardNumberEditText}.
 */
@RunWith(AndroidJUnit4.class)
public class CreditCardNumberEditTextTest {

    private CreditCardNumberEditText underTest;
    private Context targetContext;

    @Mock
    private CreditCardNumberListener mockCreditCardNumberListener;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        targetContext = InstrumentationRegistry.getTargetContext();
        underTest = new CreditCardNumberEditText(targetContext);
        underTest.onFinishInflate();
    }

    @Test
    public void assertPreconditions() throws Exception {
        assertThat(underTest).isNotNull();
    }

    /**
     * 不明なブランド
     */
    @Test
    public void unknownBrand() throws Exception {
        assertThat(underTest).hasBrand(CreditCardBrand.UNKNOWN);
    }

    /**
     * VISA
     */
    @Test
    public void visa() throws Exception {
        underTest.setText("4242424242424242");

        assertThat(underTest).hasBrand(CreditCardBrand.VISA);
        assertThat(underTest).hasTextString("4242 4242 4242 4242");
        assertThat(underTest).hasNumber("4242424242424242");
    }

    /**
     * MasterCard
     */
    @Test
    public void masterCard() throws Exception {
        underTest.setText("5555555555554444");

        assertThat(underTest).hasBrand(CreditCardBrand.MASTER_CARD);
        assertThat(underTest).hasTextString("5555 5555 5555 4444");
        assertThat(underTest).hasNumber("5555555555554444");
    }

    /**
     * American Express
     */
    @Test
    public void americanExpress() throws Exception {
        underTest.setText("378282246310005");

        assertThat(underTest).hasBrand(CreditCardBrand.AMERICAN_EXPRESS);
        assertThat(underTest).hasTextString("3782 822463 10005");
        assertThat(underTest).hasNumber("378282246310005");
    }

    /**
     * JCB
     */
    @Test
    public void jcb() throws Exception {
        underTest.setText("3530111333300000");

        assertThat(underTest).hasBrand(CreditCardBrand.JCB);
        assertThat(underTest).hasTextString("3530 1113 3330 0000");
        assertThat(underTest).hasNumber("3530111333300000");
    }

    /**
     * Diners Club
     */
    @Test
    public void dinersClub() throws Exception {
        underTest.setText("30569309025904");

        assertThat(underTest).hasBrand(CreditCardBrand.DINERS_CLUB);
        assertThat(underTest).hasTextString("3056 930902 5904");
        assertThat(underTest).hasNumber("30569309025904");
    }

    /**
     * 不明（American Express, JCB, Diners Clubのいずれか）
     */
    @Test
    public void americanExpressOrJCBOrDinersClub() throws Exception {
        underTest.setText("3");

        assertThat(underTest).hasBrand(CreditCardBrand.UNKNOWN);
        assertThat(underTest).hasTextString("3");
        assertThat(underTest).hasNumber("3");
    }

    /**
     * カード番号のフォーマットでセパレーターが編集されなかった場合の変更を通知する
     */
    @Test
    public void sendNumberChanged() throws Exception {
        underTest.addNumberListener(mockCreditCardNumberListener);
        underTest.setText("4");
        Mockito.verify(mockCreditCardNumberListener, Mockito.times(1)).onChanged("4", CreditCardBrand.VISA);
    }


    /**
     * カード番号のフォーマットでセパレーターが編集された場合の変更を通知する
     */
    @Test
    public void sendNumberChangedAfterFormatNumber() throws Exception {
        underTest.addNumberListener(mockCreditCardNumberListener);
        underTest.setText("4242424242424242");
        Mockito.verify(mockCreditCardNumberListener, Mockito.times(1)).onChanged("4242424242424242", CreditCardBrand.VISA);
    }

}
