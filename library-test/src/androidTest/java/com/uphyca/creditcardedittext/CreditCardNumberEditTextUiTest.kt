package com.uphyca.creditcardedittext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.uphyca.creditcardedittext.ViewMatchers.withSelection
import com.uphyca.creditcardedittext.library_test.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Test for [CreditCardNumberEditText].
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class CreditCardNumberEditTextUiTest {

    @get:Rule
    val scenarioRule = ActivityScenarioRule(CreditCardNumberActivity::class.java)

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    /**
     * セクションの区切りにセパレーターを挿入する
     */
    @Test
    fun insertSeparator() {
        // 最初のセクション"4242"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("4242"), closeSoftKeyboard())
            .check(matches(withText("4242")))
            .check(matches(withSelection(4)))

        // 次のセクションの最初の1文字を入力するとセパレーターが挿入される
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_1), closeSoftKeyboard())
            .check(matches(withText("4242 1")))
            .check(matches(withSelection(6)))
    }

    /**
     * 複数のセクションの区切りにセパレーターを挿入する
     */
    @Test
    fun insertSeparators() {
        // 最初のセクション"4242 1110 2222"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("424211102222"), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2222")))
            .check(matches(withSelection(14)))

        // 次のセクションの最初の1文字を入力するとセパレーターが挿入される
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_3), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2222 3")))
            .check(matches(withSelection(16)))
    }

    /**
     * セクションの先頭の文字が削除されたら直前のセパレーターを除去する
     */
    @Test
    fun removeTrailingSeparator() {
        // セパレーターを含む番号"4242 1110 2"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("424211102"), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2")))
            .check(matches(withSelection(11)))

        // セパレーターの次の文字"2"を削除すると、隣接するセパレーターが削除される
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_DEL), closeSoftKeyboard())
            .check(matches(withText("4242 1110")))
            .check(matches(withSelection(9)))
    }

    /**
     * セパレーターが削除されたら直前の文字を削除する（セパレーターは表示上の空白として表現しているため）
     */
    @Test
    fun removePreviousCharacterWhenSeparatorDeleted() {
        // セパレーターを含む番号"4242 1110 2"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("424211102"), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2")))
            .check(matches(withSelection(11)))

        // カーソルをセパレーターの位置に移動する
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
            .check(matches(withSelection(10)))

        // セパレーターを削除すると直前の文字"0"が削除される
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_DEL), closeSoftKeyboard())
            .check(matches(withText("4242 1112")))
            .check(matches(withSelection(8)))
    }

    /**
     * セクションの区切りにセパレーターを挿入する
     */
    @Test
    fun insertCharBeforeSeparator() {
        // セパレーターを含む番号"4242 1110 2222"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("424211102222"), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2222")))
            .check(matches(withSelection(14)))

        // "4242 1110 2222" の "0" の直後（スペースの前）にカーソルを持ってくる。
        // カーソル位置の設定はプログラムでTextView#setSelection(..)を実行すれば可能だが、動きが変わる可能性があるのでできるだけ実際の操作をエミュレートする。
        for (i in 1..5) {
            onView(withId(R.id.credit_card_number))
                .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                .check(matches(withSelection(14 - i)))
        }

        // カーソル位置に"3"を入力すると、カーソルは入力した文字"3"の直後に移動する
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_3), closeSoftKeyboard())
            .check(matches(withText("4242 1110 3222 2")))
            .check(matches(withSelection(11)))
    }

    /**
     * クリップボード等から複数の文字を一度に貼り付ける
     */
    @Test
    @Throws(Exception::class)
    fun pasteNumberFromClipboard() {
        sendToClipboard("424211102222")
        pasteFromClipboard()

        // 末尾にカーソルがセットされる
        onView(withId(R.id.credit_card_number))
            .check(matches(withText("4242 1110 2222")))
            .check(matches(withSelection(14)))
    }

    /**
     * クリップボード等から最大文字長を超過する複数の文字を一度に貼り付ける
     */
    @Test
    @Throws(Exception::class)
    fun pasteOverflowNumberFromClipboard() {
        onView(withId(R.id.credit_card_number))
            .perform(typeText("4"), closeSoftKeyboard())
            .check(matches(withText("4")))
            .check(matches(withSelection(1)))
        sendToClipboard("2421110222255549")
        pasteFromClipboard()

        // 末尾にカーソルがセットされる
        onView(withId(R.id.credit_card_number))
            .check(matches(withText("4242 1110 2222 5554")))
            .check(matches(withSelection(19)))
    }

    /**
     * 先頭にスペースを入力する
     */
    @Test
    fun enterSpaceAtHead() {
        // スペースを入力しても無視される
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_SPACE), closeSoftKeyboard())
            .check(matches(withText("")))
            .check(matches(withSelection(0)))
    }

    /**
     * 末尾にスペースを入力する
     */
    @Test
    fun enterSpaceAtTail() {
        // セパレーターを含む番号"4242 1110 2222"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("424211102222"), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2222")))
            .check(matches(withSelection(14)))

        // スペースを入力しても無視される
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_SPACE), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2222")))
            .check(matches(withSelection(14)))
    }

    /**
     * セクションの区切りにスペースを入力する
     */
    @Test
    fun enterSpaceOnSeparator() {
        // セパレーターを含む番号"4242 1110 2"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("424211102"), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2")))
            .check(matches(withSelection(11)))

        // "4242 1110 2222" の "0" の直後（スペースの前）にカーソルを持ってくる。
        // カーソル位置の設定はプログラムでTextView#setSelection(..)を実行すれば可能だが、動きが変わる可能性があるのでできるだけ実際の操作をエミュレートする。
        for (i in 1..2) {
            onView(withId(R.id.credit_card_number))
                .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                .check(matches(withSelection(11 - i)))
        }

        // スペースを入力するとセパレーターを上書きしてカーソルが移動する
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_SPACE), closeSoftKeyboard())
            .check(matches(withText("4242 1110 2")))
            .check(matches(withSelection(10)))
    }

    /**
     * セクションの先頭にスペースを入力する
     */
    @Test
    fun enterSpaceOnSectionHead() {
        // セパレーターを含む番号"4242 1110 2"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("42421110"), closeSoftKeyboard())
            .check(matches(withText("4242 1110")))
            .check(matches(withSelection(9)))

        // "4242 1110" の "1110" の前にカーソルを持ってくる。
        for (i in 1..4) {
            onView(withId(R.id.credit_card_number))
                .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                .check(matches(withSelection(9 - i)))
        }

        // スペースを入力しても無視される
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_SPACE), closeSoftKeyboard())
            .check(matches(withText("4242 1110")))
            .check(matches(withSelection(5)))
    }

    /**
     * セクションの途中にスペースを入力する
     */
    @Test
    fun enterSpaceInSection() {
        // セパレーターを含む番号"4242 1110 2"を入力しておく
        onView(withId(R.id.credit_card_number))
            .perform(typeText("42421110"), closeSoftKeyboard())
            .check(matches(withText("4242 1110")))
            .check(matches(withSelection(9)))

        // "4242 1110" の "10" の前にカーソルを持ってくる。
        for (i in 1..2) {
            onView(withId(R.id.credit_card_number))
                .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                .check(matches(withSelection(9 - i)))
        }

        // スペースを入力しても無視される
        onView(withId(R.id.credit_card_number))
            .perform(pressKey(KeyEvent.KEYCODE_SPACE), closeSoftKeyboard())
            .check(matches(withText("4242 1110")))
            .check(matches(withSelection(7)))
    }

    /**
     * クリップボードから貼り付ける
     */
    private fun pasteFromClipboard() {
        device.pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON)
    }

    companion object {

        /**
         * 指定の文字列をクリップボードに転送する
         *
         * @param text クリップボードに転送する文字列
         */
        @Throws(InterruptedException::class)
        private fun sendToClipboard(text: String) {
            val label = CreditCardNumberEditTextUiTest::class.java.name
            val lock = CountDownLatch(1)
            //クリップボードはハンドラースレッドで操作しなければならない
            val handlerThread = HandlerThread(label).apply {
                start()
            }
            Handler(handlerThread.looper).post {
                val context = ApplicationProvider.getApplicationContext<Context>()
                val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                checkNotNull(clipboardManager)
                clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text))
                lock.countDown()
            }
            lock.await(5, TimeUnit.SECONDS)
            handlerThread.quit()
        }
    }
}
