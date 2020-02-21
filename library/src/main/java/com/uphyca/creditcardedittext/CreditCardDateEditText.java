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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EditText for expiration date of credit card.
 * <p>
 * Show formatted date according to MM/yy format.
 * {@link #getDate()} returns the expiration date.
 * Use {@link CreditCardDateListener} to receive the change of expiration date.
 * <p>
 * If you register your own {@link TextWatcher} by {@link #addTextChangedListener}, you will
 * receive the change twice for user input and formatting.
 */
public class CreditCardDateEditText extends AppCompatEditText {

    private static final char SEPARATOR = '/';
    private static final String EMPTY = "";
    private static final int MAX_LENGTH = 4;
    private static final int SEPARATOR_COUNT = 1;
    private static final int SEPARATOR_POSITION = 2;
    private static final int[] FORMAT = {2, 2};

    private ArrayList<CreditCardDateListener> listeners;

    public CreditCardDateEditText(Context context) {
        super(context);
    }

    public CreditCardDateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreditCardDateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setRawInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] filters = {new CreditCardDateKeyListener()};
        setFilters(filters);

        addTextChangedListener(textWatcher);
    }

    /**
     * Register a listener to receive the change of expiration date
     */
    public void addDateListener(CreditCardDateListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    /**
     * Unregister listener that registered by {@link #addDateListener}
     */
    public void removeDateListener(CreditCardDateListener listener) {
        if (listeners != null) {
            int i = listeners.indexOf(listener);
            if (i >= 0) {
                listeners.remove(i);
            }
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {

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
            final int beforeRawLength = beforeText.length();

            final String afterCardDate = removeSeparators(s.toString());
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
                // セパレーター位置でスラッシュを入力後に連続したセパレーターの一つが削除された場合、
                // DELキーによる削除とみなすとセパレーターの前の文字が意図せず消えるのを抑止する。
                if (!equalsChatAt(beforeText, beforeSelectionStart, SEPARATOR)) {
                    afterRawText.deleteCharAt(getSelectionStart() - 1);
                }
            }

            //final CreditCardBrand brand = CreditCardBrand.getBrand(afterCardDate);
            // この後にseparatorを一括で挿入するので、すでに挿入済みのseparatorを削除しておく
            removeSeparators(afterRawText, 0);
            insertSeparator(afterRawText, 0);

            if (!TextUtils.equals(s, afterRawText)) {
                s.replace(0, s.length(), afterRawText.toString());
                // セパレーター挿入後にカーソル位置がずれるので調整する
                StringBuilder beforeRawText = new StringBuilder(beforeText);
                int selectionIndex = separatorDeleted ? beforeSelectionStart - 1 : beforeSelectionStart;
                selectionIndex = removeSeparators(beforeRawText, selectionIndex);
                selectionIndex = insertSeparator(beforeRawText, selectionIndex);
                selectionIndex = Math.min(selectionIndex, MAX_LENGTH + SEPARATOR_COUNT);
                setSelection(selectionIndex);
            } else {
                // 編集完了時にだけリスナーを呼ぶ
                sendDateChanged(parseDate(afterCardDate));
            }
        }

        private void sendDateChanged(@NonNull CreditCardDate date) {
            if (listeners != null) {
                final ArrayList<CreditCardDateListener> list = listeners;
                final int count = list.size();
                for (int i = 0; i < count; i++) {
                    list.get(i).onChanged(date);
                }
            }
        }
    };

    private static class CreditCardDateKeyListener extends NumberKeyListener {

        private final Pattern monthStartPattern = Pattern.compile("^[0-1].*");
        private final Pattern monthPattern = Pattern.compile("^(0[1-9]|1[0-2]).*");

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

            //書式のセパレーター位置以外に入力されたセパレーターを除去する
            StringBuilder sourceBuf = new StringBuilder(source.subSequence(start, end));
            for (int i = end - 1; i >= start; --i) {
                if (sourceBuf.charAt(i) == SEPARATOR) {
                    int index = i + dstart;
                    if (!isSeparatorPosition(index)) {
                        sourceBuf.deleteCharAt(i);
                    }
                }
            }
            if (!TextUtils.equals(source, sourceBuf)) {
                source = sourceBuf;
                start = 0;
                end = source.length();
            }

            //有効期限の月が0から1で始まっていない場合は除去する
            if (tempRawText.length() >= 1) {
                final Matcher matcher = monthStartPattern.matcher(tempRawText);
                if (!matcher.matches()) {
                    return EMPTY;
                }
            }

            //有効期限の月が01〜12ではない場合は、二文字目以降を除去する
            if (tempRawText.length() >= 2) {
                final Matcher matcher = monthPattern.matcher(tempRawText);
                if (!matcher.matches()) {
                    sourceBuf = new StringBuilder(source.subSequence(start, end));
                    for (int i = end - 1; i >= start; --i) {
                        if (dstart + i == 0) {
                            break;
                        }
                        sourceBuf.deleteCharAt(i);
                    }
                    if (!TextUtils.equals(source, sourceBuf)) {
                        source = sourceBuf;
                        start = 0;
                        end = source.length();
                    }
                }
            }

            // 入力文字数をチェック
            final int maxLength = MAX_LENGTH + SEPARATOR_COUNT;
            final CharSequence lengthOut = lengthFilter(maxLength, source, start, end, dest, dstart, dend);
            return lengthOut == null ? source : lengthOut;
        }

        private boolean isSeparatorPosition(int index) {
            return index == SEPARATOR_POSITION;
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
     * Return expiration date of credit card.
     */
    @NonNull
    public CreditCardDate getDate() {
        final Editable text = getText();
        return parseDate(text == null ? "" : text.toString());
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
     * 有効期限の書式に従ってセパレーターを挿入する
     *
     * @param sb             有効期限
     * @param selectionIndex カーソル位置
     * @return セパレーター挿入後のカーソル位置
     */
    private static int insertSeparator(StringBuilder sb, int selectionIndex) {
        int newSelectionIndex = selectionIndex;
        int i = 0;
        for (int number : FORMAT) {
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

    @NonNull
    private static String safeSubstring(String s, int start, int end) {
        if (s == null) {
            return EMPTY;
        }
        if (start >= s.length()) {
            return EMPTY;
        }
        if (end > s.length()) {
            end = s.length();
        }
        return s.substring(start, end);
    }

    @NonNull
    private static CreditCardDate parseDate(String s) {
        String date = removeSeparators(s);
        String month = safeSubstring(date, 0, 2);
        String year = safeSubstring(date, 2, 4);
        return new CreditCardDate(month, year);
    }
}
