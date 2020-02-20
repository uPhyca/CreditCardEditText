package com.uphyca.creditcardedittext;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.KeyEvent;

import com.uphyca.creditcardedittext.library_test.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.uphyca.creditcardedittext.ViewMatchers.withSelection;

/**
 * Test for {@link CreditCardDateEditText}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreditCardDateEditTextTest {

    @Rule
    public ActivityTestRule<CreditCardDateActivity> mActivityRule = new ActivityTestRule<>(
            CreditCardDateActivity.class);

    UiDevice device;

    @Before
    public void setUp() throws Exception {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    /**
     * セクションの区切りにセパレーターを挿入する
     */
    @Test
    public void insertSeparator() throws Exception {
        // 最初のセクション"4242"を入力しておく
        onView(withId(R.id.credit_card_date))
                .perform(typeText("01"), closeSoftKeyboard())
                .check(matches(withText("01")))
                .check(matches(withSelection(2)));

        // 次のセクションの最初の1文字を入力するとセパレーターが挿入される
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_1), closeSoftKeyboard())
                .check(matches(withText("01/1")))
                .check(matches(withSelection(4)));
    }

    /**
     * セクションの先頭の文字が削除されたら直前のセパレーターを除去する
     */
    @Test
    public void removeTrailingSeparator() throws Exception {
        // セパレーターを含む番号"01/2"を入力しておく
        onView(withId(R.id.credit_card_date))
                .perform(typeText("012"), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(4)));

        // セパレーターの次の文字"2"を削除すると、隣接するセパレーターが削除される
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_DEL), closeSoftKeyboard())
                .check(matches(withText("01")))
                .check(matches(withSelection(2)));
    }

    /**
     * セパレーターが削除されたら直前の文字を削除する（セパレーターは表示上の空白として表現しているため）
     */
    @Test
    public void removePreviousCharacterWhenSeparatorDeleted() throws Exception {
        // セパレーターを含む番号"01/2"を入力しておく
        onView(withId(R.id.credit_card_date))
                .perform(typeText("012"), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(4)));

        // カーソルをセパレーターの位置に移動する
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                .check(matches(withSelection(3)));

        // セパレーターを削除すると直前の文字"1"が削除される
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_DEL), closeSoftKeyboard())
                .check(matches(withText("02")))
                .check(matches(withSelection(1)));
    }

    /**
     * セクションの区切りにセパレーターを挿入する
     */
    @Test
    public void insertCharBeforeSeparator() throws Exception {
        // セパレーターを含む"012"を入力しておく
        onView(withId(R.id.credit_card_date))
                .perform(typeText("012"), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(4)));

        // "01/23" の "1" の直後（/の前）にカーソルを持ってくる。
        // カーソル位置の設定はプログラムでTextView#setSelection(..)を実行すれば可能だが、動きが変わる可能性があるのでできるだけ実際の操作をエミュレートする。
        for (int i = 1; i <= 2; i++) {
            onView(withId(R.id.credit_card_date))
                    .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                    .check(matches(withSelection(4 - i)));
        }

        // カーソル位置に"3"を入力すると、カーソルは入力した文字"3"の直後に移動する
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_3), closeSoftKeyboard())
                .check(matches(withText("01/32")))
                .check(matches(withSelection(4)));
    }

    /**
     * クリップボード等から複数の文字を一度に貼り付ける
     */
    @Test
    public void pasteDateFromClipboard() throws Exception {
        sendToClipboard("0123");
        pasteFromClipboard();

        // 末尾にカーソルがセットされる
        onView(withId(R.id.credit_card_date))
                .check(matches(withText("01/23")))
                .check(matches(withSelection(5)));
    }

    /**
     * クリップボード等から最大文字長を超過する複数の文字を一度に貼り付ける
     */
    @Test
    public void pasteOverflowNumberFromClipboard() throws Exception {
        onView(withId(R.id.credit_card_date))
                .perform(typeText("0"), closeSoftKeyboard())
                .check(matches(withText("0")))
                .check(matches(withSelection(1)));

        sendToClipboard("1234");
        pasteFromClipboard();

        // 末尾にカーソルがセットされる
        onView(withId(R.id.credit_card_date))
                .check(matches(withText("01/23")))
                .check(matches(withSelection(5)));
    }

    /**
     * 先頭にセパレーターを入力する
     */
    @Test
    public void enterSeparatorAtHead() throws Exception {
        // セパレーターを入力しても無視される
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_SLASH), closeSoftKeyboard())
                .check(matches(withText("")))
                .check(matches(withSelection(0)));
    }

    /**
     * 末尾にスペースを入力する
     */
    @Test
    public void enterSeparatorAtTail() throws Exception {
        // セパレーターを含む有効期限"01/23"を入力しておく
        onView(withId(R.id.credit_card_date))
                .perform(typeText("0123"), closeSoftKeyboard())
                .check(matches(withText("01/23")))
                .check(matches(withSelection(5)));

        // スペースを入力しても無視される
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_SLASH), closeSoftKeyboard())
                .check(matches(withText("01/23")))
                .check(matches(withSelection(5)));
    }

    /**
     * セクションの区切りにセパレーターを入力する
     */
    @Test
    public void enterSeparatorOnSeparator() throws Exception {
        // セパレーターを含む有効期限"01/2"を入力しておく
        onView(withId(R.id.credit_card_date))
                .perform(typeText("012"), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(4)));

        // "01/2" の "1" の直後（セパレーターの前）にカーソルを持ってくる。
        // カーソル位置の設定はプログラムでTextView#setSelection(..)を実行すれば可能だが、動きが変わる可能性があるのでできるだけ実際の操作をエミュレートする。
        for (int i = 1; i <= 2; i++) {
            onView(withId(R.id.credit_card_date))
                    .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                    .check(matches(withSelection(4 - i)));
        }

        // セパレーターを入力するとセパレーターを上書きしてカーソルが移動する
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_SLASH), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(3)));
    }

    /**
     * セクションの先頭にセパレーターを入力する
     */
    @Test
    public void enterSeparatorOnSectionHead() throws Exception {
        // セパレーターを含む番号"01/2"を入力しておく
        onView(withId(R.id.credit_card_date))
                .perform(typeText("012"), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(4)));

        // "4242 1110" の "1110" の前にカーソルを持ってくる。
        for (int i = 1; i <= 1; i++) {
            onView(withId(R.id.credit_card_date))
                    .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                    .check(matches(withSelection(4 - i)));
        }

        // セパレーターを入力しても無視される
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_SLASH), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(3)));
    }

    /**
     * セクションの途中にセパレーターを入力する
     */
    @Test
    public void enterSeparatorInSection() throws Exception {
        // セパレーターを含む有効期限"01/2"を入力しておく
        onView(withId(R.id.credit_card_date))
                .perform(typeText("012"), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(4)));

        // "01" の "1" の前にカーソルを持ってくる。
        for (int i = 1; i <= 3; i++) {
            onView(withId(R.id.credit_card_date))
                    .perform(pressKey(KeyEvent.KEYCODE_DPAD_LEFT), closeSoftKeyboard())
                    .check(matches(withSelection(4 - i)));
        }

        // セパレーターを入力しても無視される
        onView(withId(R.id.credit_card_date))
                .perform(pressKey(KeyEvent.KEYCODE_SLASH), closeSoftKeyboard())
                .check(matches(withText("01/2")))
                .check(matches(withSelection(1)));
    }


    /**
     * クリップボードから貼り付ける
     */
    private void pasteFromClipboard() {
        device.pressKeyCode(KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_ON);
    }

    /**
     * 指定の文字列をクリップボードに転送する
     *
     * @param text クリップボードに転送する文字列
     */
    private static void sendToClipboard(final String text) throws InterruptedException {
        final CountDownLatch lock = new CountDownLatch(1);
        //クリップボードはハンドラースレッドで操作しなければならない
        HandlerThread ht = new HandlerThread(CreditCardDateEditTextTest.class.getName());
        ht.start();
        Handler h = new Handler(ht.getLooper());
        h.post(new Runnable() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void run() {
                Context targetContext = InstrumentationRegistry.getTargetContext();
                ClipboardManager clipboardManager = (ClipboardManager) targetContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(CreditCardDateEditTextTest.class.getName(), text));
                lock.countDown();
            }
        });
        lock.await(5, TimeUnit.SECONDS);
        ht.quit();
    }
}
