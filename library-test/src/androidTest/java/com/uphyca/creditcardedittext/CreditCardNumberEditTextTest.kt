package com.uphyca.creditcardedittext

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uphyca.creditcardedittext.subject.CreditCardNumberEditTextSubject.Companion.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

/**
 * Test for [CreditCardNumberEditText].
 */
@RunWith(AndroidJUnit4::class)
class CreditCardNumberEditTextTest {

    private lateinit var underTest: CreditCardNumberEditText
    private lateinit var mockCreditCardNumberListener: CreditCardNumberListener

    @Before
    fun setUp() {
        mockCreditCardNumberListener = mock(CreditCardNumberListener::class.java)
        underTest = CreditCardNumberEditText(ApplicationProvider.getApplicationContext())
        underTest.onFinishInflate()
    }

    @Test
    fun assertPreconditions() {
        assertThat(underTest).isNotNull()
    }

    /**
     * 不明なブランド
     */
    @Test
    fun unknownBrand() {
        assertThat(underTest).hasBrand(CreditCardBrand.UNKNOWN)
    }

    /**
     * VISA
     */
    @Test
    fun visa() {
        underTest.setText("4242424242424242")
        assertThat(underTest).hasBrand(CreditCardBrand.VISA)
        assertThat(underTest).hasTextString("4242 4242 4242 4242")
        assertThat(underTest).hasNumber("4242424242424242")
    }

    /**
     * MasterCard
     */
    @Test
    fun masterCard() {
        underTest.setText("5555555555554444")
        assertThat(underTest).hasBrand(CreditCardBrand.MASTER_CARD)
        assertThat(underTest).hasTextString("5555 5555 5555 4444")
        assertThat(underTest).hasNumber("5555555555554444")
    }

    /**
     * American Express
     */
    @Test
    fun americanExpress() {
        underTest.setText("378282246310005")
        assertThat(underTest).hasBrand(CreditCardBrand.AMERICAN_EXPRESS)
        assertThat(underTest).hasTextString("3782 822463 10005")
        assertThat(underTest).hasNumber("378282246310005")
    }

    /**
     * JCB
     */
    @Test
    fun jcb() {
        underTest.setText("3530111333300000")
        assertThat(underTest).hasBrand(CreditCardBrand.JCB)
        assertThat(underTest).hasTextString("3530 1113 3330 0000")
        assertThat(underTest).hasNumber("3530111333300000")
    }

    /**
     * Diners Club
     */
    @Test
    fun dinersClub() {
        underTest.setText("30569309025904")
        assertThat(underTest).hasBrand(CreditCardBrand.DINERS_CLUB)
        assertThat(underTest).hasTextString("3056 930902 5904")
        assertThat(underTest).hasNumber("30569309025904")
    }

    /**
     * 不明（American Express, JCB, Diners Clubのいずれか）
     */
    @Test
    fun americanExpressOrJCBOrDinersClub() {
        underTest.setText("3")
        assertThat(underTest).hasBrand(CreditCardBrand.UNKNOWN)
        assertThat(underTest).hasTextString("3")
        assertThat(underTest).hasNumber("3")
    }

    /**
     * カード番号のフォーマットでセパレーターが編集されなかった場合の変更を通知する
     */
    @Test
    fun sendNumberChanged() {
        underTest.addNumberListener(mockCreditCardNumberListener)
        underTest.setText("4")
        verify(mockCreditCardNumberListener, times(1))
            .onChanged("4", CreditCardBrand.VISA)
    }

    /**
     * カード番号のフォーマットでセパレーターが編集された場合の変更を通知する
     */
    @Test
    fun sendNumberChangedAfterFormatNumber() {
        underTest.addNumberListener(mockCreditCardNumberListener)
        underTest.setText("4242424242424242")
        verify(mockCreditCardNumberListener, times(1))
            .onChanged("4242424242424242", CreditCardBrand.VISA)
    }
}
