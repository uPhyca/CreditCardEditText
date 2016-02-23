package com.uphyca.creditcardnumberedittext;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.Editable;
import android.widget.EditText;

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
    public void JCB() throws Exception {
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
     * セパレーターを挿入する
     */
    @Test
    public void insertSeparator() throws Exception {
        underTest.setText("4242");
        assertThat(underTest).hasTextString("4242");
        assertThat(underTest).hasNumber("4242");

        underTest.append("4");
        assertThat(underTest).hasTextString("4242 4");
        assertThat(underTest).hasNumber("42424");
    }

    /**
     * セパレーターを除去する
     */
    @Test
    public void removeSeparator() throws Exception {
        underTest.setText("42424");
        assertThat(underTest).hasTextString("4242 4");
        assertThat(underTest).hasNumber("42424");

        trim(underTest, 1);
        assertThat(underTest).hasTextString("4242");
        assertThat(underTest).hasNumber("4242");
    }

    /**
     * 末尾の文字を指定の文字数だけ削除する
     *
     * @param editText 文字を削除する対象
     * @param count    文字数
     */
    private static void trim(EditText editText, int count) {
        Editable editableText = editText.getEditableText();
        editableText.delete(editableText.length() - count, editableText.length());
    }
}