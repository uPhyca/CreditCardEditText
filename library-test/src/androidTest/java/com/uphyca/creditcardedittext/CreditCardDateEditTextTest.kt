package com.uphyca.creditcardedittext

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uphyca.creditcardedittext.subject.CreditCardDateEditTextSubject.Companion.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

/**
 * Test for [CreditCardDateEditText].
 */
@RunWith(AndroidJUnit4::class)
class CreditCardDateEditTextTest {

    private lateinit var underTest: CreditCardDateEditText
    private lateinit var mockCreditCardDateListener: CreditCardDateListener

    @Before
    fun setUp() {
        mockCreditCardDateListener = mock(CreditCardDateListener::class.java)
        underTest = CreditCardDateEditText(ApplicationProvider.getApplicationContext())
        underTest.onFinishInflate()
    }

    @Test
    fun assertPreconditions() {
        assertThat(underTest).isNotNull()
    }

    /**
     * 未入力
     */
    @Test
    fun unknownDate() {
        assertThat(underTest).hasDate(CreditCardDate("", ""))
    }

    /**
     * 月一桁のみ
     */
    @Test
    fun oneDigitMonth() {
        underTest.setText("0")
        assertThat(underTest).hasTextString("0")
        assertThat(underTest).hasDate(CreditCardDate("0", ""))
    }

    /**
     * 月二桁のみ
     */
    @Test
    fun twoDigitMonth() {
        underTest.setText("01")
        assertThat(underTest).hasTextString("01")
        assertThat(underTest).hasDate(CreditCardDate("01", ""))
    }

    /**
     * 年一桁
     */
    @Test
    fun oneDigitYear() {
        underTest.setText("019")
        assertThat(underTest).hasTextString("01/9")
        assertThat(underTest).hasDate(CreditCardDate("01", "9"))
    }

    /**
     * 年二桁
     */
    @Test
    fun twoDigitYear() {
        underTest.setText("0199")
        assertThat(underTest).hasTextString("01/99")
        assertThat(underTest).hasDate(CreditCardDate("01", "99"))
    }

    /**
     * 1月
     */
    @Test
    fun january() {
        underTest.setText("0100")
        assertThat(underTest).hasTextString("01/00")
        assertThat(underTest).hasDate(CreditCardDate("01", "00"))
    }

    /**
     * 9月
     */
    @Test
    fun september() {
        underTest.setText("0900")
        assertThat(underTest).hasTextString("09/00")
        assertThat(underTest).hasDate(CreditCardDate("09", "00"))
    }

    /**
     * 10月
     */
    @Test
    fun october() {
        underTest.setText("1000")
        assertThat(underTest).hasTextString("10/00")
        assertThat(underTest).hasDate(CreditCardDate("10", "00"))
    }

    /**
     * 12月
     */
    @Test
    fun december() {
        underTest.setText("1200")
        assertThat(underTest).hasTextString("12/00")
        assertThat(underTest).hasDate(CreditCardDate("12", "00"))
    }

    /**
     * 00月
     */
    @Test
    fun invalidMonth00() {
        underTest.setText("00")
        assertThat(underTest).hasTextString("0")
        assertThat(underTest).hasDate(CreditCardDate("0", ""))
    }

    /**
     * 13月
     */
    @Test
    fun invalidMonth13() {
        underTest.setText("13")
        assertThat(underTest).hasTextString("1")
        assertThat(underTest).hasDate(CreditCardDate("1", ""))
    }

    /**
     * 20月
     */
    @Test
    fun invalidMonth2() {
        underTest.setText("2")
        assertThat(underTest).hasTextString("")
        assertThat(underTest).hasDate(CreditCardDate("", ""))
    }

    /**
     * 有効期限のフォーマットでセパレーターが編集されなかった場合の変更を通知する
     */
    @Test
    fun sendDateChanged() {
        underTest.addDateListener(mockCreditCardDateListener)
        underTest.setText("0")
        verify(mockCreditCardDateListener, times(1))
            .onChanged(CreditCardDate("0", ""))
    }

    /**
     * リスナーが削除された場合、変更は通知されない
     */
    @Test
    fun notSendDateChangedAfterRemoveListener() {
        underTest.addDateListener(mockCreditCardDateListener)
        underTest.removeDateListener(mockCreditCardDateListener)
        underTest.setText("0")
        verify(mockCreditCardDateListener, never())
            .onChanged(CreditCardDate("0", ""))
    }

    /**
     * 有効期限のフォーマットでセパレーターが編集された場合の変更を通知する
     */
    @Test
    fun sendDateChangedAfterFormatDate() {
        underTest.addDateListener(mockCreditCardDateListener)
        underTest.setText("0123")
        verify(mockCreditCardDateListener, times(1))
            .onChanged(CreditCardDate("01", "23"))
    }
}
