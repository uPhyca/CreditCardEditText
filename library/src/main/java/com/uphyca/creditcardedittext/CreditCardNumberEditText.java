/*
 * Copyright 2016 uPhyca, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uphyca.creditcardedittext;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;

/**
 * EditText for number of credit card.
 * <p>
 * Show formatted number according to the formats of issuing networks.
 * {@link #getBrand()} returns the issuing network related to card number.
 * Use {@link CreditCardNumberListener} to receive the change of card number.
 * <p>
 * If you register your own {@link TextWatcher} by {@link #addTextChangedListener}, you will
 * receive the change twice for user input and formatting.
 */
public class CreditCardNumberEditText extends AppCompatEditText {

    private static final char SEPARATOR = ' ';
    private static final String EMPTY = "";
    private ArrayList<CreditCardNumberListener> listeners;

    public CreditCardNumberEditText(Context context) {
        super(context);
    }

    public CreditCardNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreditCardNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setRawInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] filters = {new CreditCardNumberKeyListener()};
        setFilters(filters);

        addTextChangedListener(textWatcher);
    }

    /**
     * Register a listener to receive the change of number
     */
    public void addNumberListener(CreditCardNumberListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    /**
     * Unregister listener that registered by {@link #addNumberListener}
     */
    public void removeNumberListener(CreditCardNumberListener listener) {
        if (listeners != null) {
            int i = listeners.indexOf(listener);
            if (i >= 0) {
                listeners.remove(i);
            }
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {

        // カーソル位置について
        //
        // セパレーターを挿入したぶん、カーソル位置を調整しなければならない。
        // 以下にプログラムでカーソル位置を調整しない場合の挙動を参考として記す。
        //
        //    未入力状態から“424211102222”をペーストしたとき
        //    Precondition: <>
        //    Actual: 4242 1110 2222<>
        //    Expected: 4242 1110 2222<>
        //    Log:
        //    beforeTextChanged(,0,0,12)
        //    selectionStart=0, selectionEnd=0
        //    onTextChanged(424211102222,0,0,12)
        //    selectionStart=12, selectionEnd=12
        //    afterTextChanged(424211102222)
        //    selectionStart=12, selectionEnd=12
        //    beforeTextChanged(424211102222,0,12,14)
        //    selectionStart=12, selectionEnd=12
        //    onTextChanged(4242 1110 2222,0,12,14)
        //    selectionStart=14, selectionEnd=14
        //    afterTextChanged(4242 1110 2222)
        //    selectionStart=14, selectionEnd=14
        //
        //
        //    “4242 1110 2222”が入力された状態でカーソルが末尾にあり、”3”を入力したとき
        //    Precondition: 4242 1110 2222<>
        //    Actual: 4242 1110 2222 3<>
        //    Expected: 4242 1110 2222 3<>
        //    Log:
        //    beforeTextChanged(4242 1110 2222,14,0,1)
        //    selectionStart=14, selectionEnd=14
        //    onTextChanged(4242 1110 22223,14,0,1)
        //    selectionStart=15, selectionEnd=15
        //    afterTextChanged(4242 1110 22223)
        //    selectionStart=15, selectionEnd=15
        //    beforeTextChanged(4242 1110 22223,0,15,16)
        //    selectionStart=15, selectionEnd=15
        //    onTextChanged(4242 1110 2222 3,0,15,16)
        //    selectionStart=16, selectionEnd=16
        //    afterTextChanged(4242 1110 2222 3)
        //    selectionStart=16, selectionEnd=16
        //
        //
        //    “4242 1110 2222”が入力された状態でカーソルが9文字目(1110の0とセパレーターの間、”..1110<ここ> 2222…”)にあり、”3”を入力したとき
        //    Precondition: 4242 1110<> 2222
        //    Actual: 4242 1110 <>3222 2
        //    Expected: 4242 1110 3<>222 2
        //    Log:
        //    beforeTextChanged(4242 1110 2222,9,0,1)
        //    selectionStart=9, selectionEnd=9
        //    onTextChanged(4242 11103 2222,9,0,1)
        //    selectionStart=10, selectionEnd=10
        //    afterTextChanged(4242 11103 2222)
        //    selectionStart=10, selectionEnd=10
        //    beforeTextChanged(4242 11103 2222,0,15,16)
        //    selectionStart=10, selectionEnd=10
        //    onTextChanged(4242 1110 3222 2,0,15,16)
        //    selectionStart=10, selectionEnd=10
        //    afterTextChanged(4242 1110 3222 2)
        //    selectionStart=10, selectionEnd=10
        //
        //
        //    “4242 1110 2222”が入力された状態でカーソルが9文字目(1110の0とセパレーターの間、”..1110<ここ> 2222…”)にあり、”33”をペーストしたとき
        //    Precondition: 4242 1110<> 2222
        //    Actual: 4242 1110 3<>322 22
        //    Expected: 4242 1110 33<>22 22
        //    Log:
        //    beforeTextChanged(4242 1110 2222,9,0,2)
        //    selectionStart=9, selectionEnd=9
        //    onTextChanged(4242 111033 2222,9,0,2)
        //    selectionStart=11, selectionEnd=11
        //    afterTextChanged(4242 111033 2222)
        //    selectionStart=11, selectionEnd=11
        //    beforeTextChanged(4242 111033 2222,0,16,17)
        //    selectionStart=11, selectionEnd=11
        //    onTextChanged(4242 1110 3322 22,0,16,17)
        //    selectionStart=11, selectionEnd=11
        //    afterTextChanged(4242 1110 3322 22)
        //    selectionStart=11, selectionEnd=11
        //
        //
        //    “0”が入力された状態でカーソルが0文字目にあり、”424211102222321”をペーストしたとき
        //    Precondition: <>0
        //    Actual: 4242 1110 2222 32<>10
        //    Expected: 4242 1110 2222 321<>0
        //    Log:
        //    beforeTextChanged(0,0,0,15)
        //    selectionStart=0, selectionEnd=0
        //    onTextChanged(4242111022223210,0,0,15)
        //    selectionStart=15, selectionEnd=15
        //    afterTextChanged(4242111022223210)
        //    selectionStart=15, selectionEnd=15
        //    beforeTextChanged(4242111022223210,0,16,19)
        //    selectionStart=15, selectionEnd=15
        //    onTextChanged(4242 1110 2222 3210,0,16,19)
        //    selectionStart=17, selectionEnd=17
        //    afterTextChanged(4242 1110 2222 3210)
        //    selectionStart=17, selectionEnd=17

        private String beforeText;
        private int beforeSelectionStart;
        private int beforeSelectionEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            beforeText = s.toString();
            beforeSelectionStart = getSelectionStart();
            beforeSelectionEnd = getSelectionEnd();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // noop
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String beforeCardNumber = removeSeparators(beforeText);
            final int beforeRawLength = beforeText.length();

            final String afterCardNumber = removeSeparators(s.toString());
            final StringBuilder afterRawText = new StringBuilder(s);

            boolean noSelection = beforeSelectionStart == beforeSelectionEnd;
            // DELキーでの文字削除か否か
            boolean deleteKeyEntered = noSelection // 範囲選択状態ではない
                    && beforeSelectionStart > 1 // カーソルが1文字目より後（DELキーで削除でいる状態）だったか否か
                    && getSelectionStart() == beforeSelectionStart - 1 // カーソルが一文字分前に来たか否か
                    && getSelectionStart() == getSelectionEnd(); // 範囲選択状態ではない
            // DELキーで削除されたのがセパレーターか否か
            boolean separatorDeleted = deleteKeyEntered
                    && beforeSelectionStart < beforeRawLength
                    && equalsChatAt(beforeText, beforeSelectionStart - 1, SEPARATOR); // 削除されたのがセパレーターか否か
            // セパレーターが削除されたら直前の文字を削除する
            if (separatorDeleted) {
                // セパレーター位置でスペースを入力後に連続したセパレーターの一つが削除された場合、
                // DELキーによる削除とみなすとセパレーターの前の文字が意図せず消えるのを抑止する。
                // Precondition:
                //   "4242 1110<> 2"
                // スペースを入力
                //   "4242 1110 <> 2"
                // 編集でスペースが整形される。
                // この状態は"4242 1110 <> 2"からDELキーで削除した状態と変わらないので、セパレーターが連続しているかどうかで判断する
                //   "4242 1110<> 2"
                if (!equalsChatAt(beforeText, beforeSelectionStart, SEPARATOR)) {
                    afterRawText.deleteCharAt(getSelectionStart() - 1);
                }
            }

            final CreditCardBrand brand = CreditCardBrand.getBrand(afterCardNumber);
            // この後にseparatorを一括で挿入するので、すでに挿入済みのseparatorを削除しておく
            removeSeparators(afterRawText, 0);
            insertSeparator(afterRawText, brand, 0);

            if (!TextUtils.equals(s, afterRawText)) {
                s.replace(0, s.length(), afterRawText.toString());
                // セパレーター挿入後にカーソル位置がずれるので調整する
                StringBuilder beforeRawText = new StringBuilder(beforeText);
                int selectionIndex = separatorDeleted ? beforeSelectionStart - 1 : beforeSelectionStart;
                selectionIndex = removeSeparators(beforeRawText, selectionIndex);
                selectionIndex = insertSeparator(beforeRawText, brand, selectionIndex);
                selectionIndex = Math.min(selectionIndex, brand.getMaxLength() + brand.getSeparatorCount());
                setSelection(selectionIndex);
            } else {
                // 編集完了時にだけリスナーを呼ぶ
                sendNumberChanged(afterCardNumber, brand);
            }
        }

        private void sendNumberChanged(String number, CreditCardBrand brand) {
            if (listeners != null) {
                final ArrayList<CreditCardNumberListener> list = listeners;
                final int count = list.size();
                for (int i = 0; i < count; i++) {
                    list.get(i).onChanged(number, brand);
                }
            }
        }
    };

    private static class CreditCardNumberKeyListener extends NumberKeyListener {
        private final char[] accepted = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', SEPARATOR};

        @NonNull
        @Override
        protected char[] getAcceptedChars() {
            return accepted;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            // 入力文字（数字かどうか）をチェック
            final CharSequence out = super.filter(source, start, end, dest, dstart, dend);

            if (TextUtils.equals(out, EMPTY)) {
                return EMPTY;
            }

            if (out != null) {
                source = out;
                start = 0;
                end = source.length();
            }

            //destにsourceをマージした文字列
            final String tempRawText = new StringBuilder(dest)
                    .replace(dstart, dend, source.subSequence(start, end).toString())
                    .toString();
            final String tempCardNumber = removeSeparators(tempRawText);
            final CreditCardBrand tempBrand = CreditCardBrand.getBrand(tempCardNumber);

            //ブランドごとの書式のセパレーター位置以外に入力されたセパレーターを除去する
            final StringBuilder sourceBuf = new StringBuilder(source.subSequence(start, end));
            for (int i = end - 1; i >= start; --i) {
                if (sourceBuf.charAt(i) == SEPARATOR) {
                    int index = i + dstart;
                    if (!tempBrand.isSeparatorPosition(index)) {
                        sourceBuf.deleteCharAt(i);
                    }
                }
            }
            if (!TextUtils.equals(source, sourceBuf)) {
                source = sourceBuf;
                start = 0;
                end = source.length();
            }

            // 入力文字数をチェック
            final int maxLength = tempBrand.getMaxLength() + tempBrand.getSeparatorCount();
            final CharSequence lengthOut = lengthFilter(maxLength, source, start, end, dest, dstart, dend);
            return lengthOut == null ? source : lengthOut;
        }

        // Taken from android.text.InputFilter.LengthFilter
        private static CharSequence lengthFilter(int maxLength, CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int keep = maxLength - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                return EMPTY;
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                keep += start;
                if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                    --keep;
                    if (keep == start) {
                        return EMPTY;
                    }
                }
                return source.subSequence(start, keep);
            }
        }

        @Override
        public int getInputType() {
            return InputType.TYPE_CLASS_NUMBER;
        }
    }

    /**
     * Return the brand of credit card.
     */
    @NonNull
    public CreditCardBrand getBrand() {
        return CreditCardBrand.getBrand(getNumber());
    }

    /**
     * Return the number of credit card (separator not included).
     */
    @NonNull
    public String getNumber() {
        final Editable text = getText();
        return removeSeparators(text == null ? "" : text.toString());
    }

    /**
     * 文字列から全てのセパレーターを除去する
     *
     * @param s 文字列
     * @return セパレーター除去後の文字列
     */
    @NonNull
    private static String removeSeparators(String s) {
        return s.replace(String.valueOf(SEPARATOR), EMPTY);
    }

    /**
     * 文字列から全てのセパレーターを除去する
     *
     * @param sb             文字列
     * @param selectionIndex セパレーター除去前のカーソル位置
     * @return セパレーター除去後のカーソル位置
     */
    private static int removeSeparators(StringBuilder sb, int selectionIndex) {
        int newSelection = selectionIndex;
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (sb.charAt(i) == SEPARATOR) {
                sb.deleteCharAt(i);
                if (i < selectionIndex) {
                    --newSelection;
                }
            }
        }
        return newSelection;
    }

    /**
     * カード番号情報に対応する書式に従ってセパレーターを挿入する
     *
     * @param sb             カード番号
     * @param brand          カード番号情報
     * @param selectionIndex カーソル位置
     * @return セパレーター挿入後のカーソル位置
     */
    private static int insertSeparator(StringBuilder sb, CreditCardBrand brand, int selectionIndex) {
        int newSelectionIndex = selectionIndex;
        final int[] format = brand.getFormat();
        int i = 0;
        for (int number : format) {
            i += number;
            if (sb.length() > i) {
                sb.insert(i, SEPARATOR);
                if (i <= newSelectionIndex) {
                    ++newSelectionIndex;
                }
            } else {
                break;
            }
            i++;
        }
        return newSelectionIndex;
    }

    /**
     * 文字列中の指定位置の文字と、指定の文字を比較する。
     * 指定位置が有効ではない場合はfalseを返す。
     *
     * @param s     文字列
     * @param index 対象文字列中で比較する文字の位置
     * @param c     比較文字
     * @return 文字が同じ場合はtrue、それ以外はfalse
     */
    private static boolean equalsChatAt(CharSequence s, int index, char c) {
        return index < s.length() && s.charAt(index) == c;
    }
}
