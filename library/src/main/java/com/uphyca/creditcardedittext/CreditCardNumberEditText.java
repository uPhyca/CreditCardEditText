package com.uphyca.creditcardedittext;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;

/**
 * クレジットカード番号入力用の EditText
 * カード会社ごとに見せ方を変える
 * カード会社の判定もできる
 */
public class CreditCardNumberEditText extends AppCompatEditText {

    private static final char SEPARATOR = ' ';
    private static final String EMPTY = "";

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
        //    D/CreditCardNumber: beforeTextChanged(,0,0,12)
        //    D/CreditCardNumber: selectionStart=0, selectionEnd=0
        //    D/CreditCardNumber: onTextChanged(424211102222,0,0,12)
        //    D/CreditCardNumber: selectionStart=12, selectionEnd=12
        //    D/CreditCardNumber: afterTextChanged(424211102222)
        //    D/CreditCardNumber: selectionStart=12, selectionEnd=12
        //    D/CreditCardNumber: beforeTextChanged(424211102222,0,12,14)
        //    D/CreditCardNumber: selectionStart=12, selectionEnd=12
        //    D/CreditCardNumber: onTextChanged(4242 1110 2222,0,12,14)
        //    D/CreditCardNumber: selectionStart=14, selectionEnd=14
        //    D/CreditCardNumber: afterTextChanged(4242 1110 2222)
        //    D/CreditCardNumber: selectionStart=14, selectionEnd=14
        //
        //
        //    “4242 1110 2222”が入力された状態でカーソルが末尾にあり、”3”を入力したとき
        //    Precondition: 4242 1110 2222<>
        //    Actual: 4242 1110 2222 3<>
        //    Expected: 4242 1110 2222 3<>
        //    Log:
        //    D/CreditCardNumber: beforeTextChanged(4242 1110 2222,14,0,1)
        //    D/CreditCardNumber: selectionStart=14, selectionEnd=14
        //    D/CreditCardNumber: onTextChanged(4242 1110 22223,14,0,1)
        //    D/CreditCardNumber: selectionStart=15, selectionEnd=15
        //    D/CreditCardNumber: afterTextChanged(4242 1110 22223)
        //    D/CreditCardNumber: selectionStart=15, selectionEnd=15
        //    D/CreditCardNumber: beforeTextChanged(4242 1110 22223,0,15,16)
        //    D/CreditCardNumber: selectionStart=15, selectionEnd=15
        //    D/CreditCardNumber: onTextChanged(4242 1110 2222 3,0,15,16)
        //    D/CreditCardNumber: selectionStart=16, selectionEnd=16
        //    D/CreditCardNumber: afterTextChanged(4242 1110 2222 3)
        //    D/CreditCardNumber: selectionStart=16, selectionEnd=16
        //
        //
        //    “4242 1110 2222”が入力された状態でカーソルが9文字目(1110の0とセパレーターの間、”..1110<ここ> 2222…”)にあり、”3”を入力したとき
        //    Precondition: 4242 1110<> 2222
        //    Actual: 4242 1110 <>3222 2
        //    Expected: 4242 1110 3<>222 2
        //    Log:
        //    D/CreditCardNumber: beforeTextChanged(4242 1110 2222,9,0,1)
        //    D/CreditCardNumber: selectionStart=9, selectionEnd=9
        //    D/CreditCardNumber: onTextChanged(4242 11103 2222,9,0,1)
        //    D/CreditCardNumber: selectionStart=10, selectionEnd=10
        //    D/CreditCardNumber: afterTextChanged(4242 11103 2222)
        //    D/CreditCardNumber: selectionStart=10, selectionEnd=10
        //    D/CreditCardNumber: beforeTextChanged(4242 11103 2222,0,15,16)
        //    D/CreditCardNumber: selectionStart=10, selectionEnd=10
        //    D/CreditCardNumber: onTextChanged(4242 1110 3222 2,0,15,16)
        //    D/CreditCardNumber: selectionStart=10, selectionEnd=10
        //    D/CreditCardNumber: afterTextChanged(4242 1110 3222 2)
        //    D/CreditCardNumber: selectionStart=10, selectionEnd=10
        //
        //
        //    “4242 1110 2222”が入力された状態でカーソルが9文字目(1110の0とセパレーターの間、”..1110<ここ> 2222…”)にあり、”33”をペーストしたとき
        //    Precondition: 4242 1110<> 2222
        //    Actual: 4242 1110 3<>322 22
        //    Expected: // 4242 1110 33<>22 22
        //    Log:
        //    D/CreditCardNumber: beforeTextChanged(4242 1110 2222,9,0,2)
        //    D/CreditCardNumber: selectionStart=9, selectionEnd=9
        //    D/CreditCardNumber: onTextChanged(4242 111033 2222,9,0,2)
        //    D/CreditCardNumber: selectionStart=11, selectionEnd=11
        //    D/CreditCardNumber: afterTextChanged(4242 111033 2222)
        //    D/CreditCardNumber: selectionStart=11, selectionEnd=11
        //    D/CreditCardNumber: beforeTextChanged(4242 111033 2222,0,16,17)
        //    D/CreditCardNumber: selectionStart=11, selectionEnd=11
        //    D/CreditCardNumber: onTextChanged(4242 1110 3322 22,0,16,17)
        //    D/CreditCardNumber: selectionStart=11, selectionEnd=11
        //    D/CreditCardNumber: afterTextChanged(4242 1110 3322 22)
        //    D/CreditCardNumber: selectionStart=11, selectionEnd=11
        //
        //
        //    “0”が入力された状態でカーソルが0文字目にあり、”424211102222321”をペーストしたとき
        //    Precondition: <>0
        //    Actual: 4242 1110 2222 32<>10
        //    Expected: 4242 1110 2222 321<>0
        //    Log:
        //    D/CreditCardNumber: beforeTextChanged(0,0,0,15)
        //    D/CreditCardNumber: selectionStart=0, selectionEnd=0
        //    D/CreditCardNumber: onTextChanged(4242111022223210,0,0,15)
        //    D/CreditCardNumber: selectionStart=15, selectionEnd=15
        //    D/CreditCardNumber: afterTextChanged(4242111022223210)
        //    D/CreditCardNumber: selectionStart=15, selectionEnd=15
        //    D/CreditCardNumber: beforeTextChanged(4242111022223210,0,16,19)
        //    D/CreditCardNumber: selectionStart=15, selectionEnd=15
        //    D/CreditCardNumber: onTextChanged(4242 1110 2222 3210,0,16,19)
        //    D/CreditCardNumber: selectionStart=17, selectionEnd=17
        //    D/CreditCardNumber: afterTextChanged(4242 1110 2222 3210)
        //    D/CreditCardNumber: selectionStart=17, selectionEnd=17


        private String beforeText;
        private int beforeIndex;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d("CreditCardNumber", String.format("beforeTextChanged(%s,%d,%d,%d)", s, start, count, after));
            Log.d("CreditCardNumber", String.format("selectionStart=%d, selectionEnd=%d", getSelectionStart(), getSelectionEnd()));
            beforeText = s.toString();
            beforeIndex = getSelectionStart();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("CreditCardNumber", String.format("onTextChanged(%s,%d,%d,%d)", s, start, before, count));
            Log.d("CreditCardNumber", String.format("selectionStart=%d, selectionEnd=%d", getSelectionStart(), getSelectionEnd()));
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d("CreditCardNumber", String.format("afterTextChanged(%s)", s));
            Log.d("CreditCardNumber", String.format("selectionStart=%d, selectionEnd=%d", getSelectionStart(), getSelectionEnd()));

            final String beforeCardNumber = removeSeparators(beforeText);
            final int beforeRawLength = beforeText.length();

            final String afterCardNumber = removeSeparators(s.toString());
            final int afterRawLength = s.length();
            final StringBuilder afterRawText = new StringBuilder(s);

            boolean trimmed = afterRawLength < beforeRawLength;
            if (TextUtils.equals(beforeCardNumber, afterCardNumber) && trimmed) {
                final int separatorPosition = beforeText.indexOf(SEPARATOR);
                if (separatorPosition > 0 && beforeText.charAt(separatorPosition) != SEPARATOR) {
                    // separator が消されたので一つ前の数字も消す
                    afterRawText.deleteCharAt(separatorPosition - 1);
                }
            }

            final CreditCardBrand brand = CreditCardBrand.getBrand(afterCardNumber);
            // この後にseparatorを一括で挿入するので、すでに挿入済みのseparatorを削除しておく
            removeSeparators(afterRawText, 0);
            insertSeparator(afterRawText, brand, 0);

            if (!TextUtils.equals(s, afterRawText)) {
                s.replace(0, afterRawLength, afterRawText.toString());
                // セパレーター挿入後にカーソル位置がずれるので調整する
                StringBuilder beforeRawText = new StringBuilder(beforeText);
                int selectionIndex = removeSeparators(beforeRawText, beforeIndex);
                selectionIndex = insertSeparator(beforeRawText, brand, selectionIndex);
                setSelection(selectionIndex);
                Log.d("CreditCardNumber", String.format("selectionStart=%d, selectionEnd=%d", getSelectionStart(), getSelectionEnd()));
            }
        }
    };

    private static class CreditCardNumberKeyListener extends NumberKeyListener {
        private final char[] accepted = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', SEPARATOR};

        @Override
        protected char[] getAcceptedChars() {
            return accepted;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            //destにsourceをマージした文字列
            String tempRawText = new StringBuilder(dest).replace(dstart, dend, source.subSequence(start, end).toString()).toString();
            String tempCardNumber = removeSeparators(tempRawText);
            CreditCardBrand tempBrand = CreditCardBrand.getBrand(tempCardNumber);
            int maxLength = tempBrand.getMaxLength() + tempBrand.getSeparatorCount();

            // 入力文字数をチェック
            CharSequence lengthOut = lengthFilter(maxLength, source, start, end, dest, dstart, dend);
            if (TextUtils.equals(lengthOut, EMPTY)) {
                // length over
                return EMPTY;
            }

            if (lengthOut != null) {
                source = lengthOut;
                start = 0;
                end = lengthOut.length();
            }

            // 入力文字（数字かどうか）をチェック
            CharSequence out = super.filter(source, start, end, dest, dstart, dend);
            return out == null ? lengthOut : out;
        }

        // Taken from android.text.InputFilter.LengthFilter
        private static CharSequence lengthFilter(int maxLength, CharSequence source, int start, int end,
                                                 Spanned dest, int dstart, int dend) {
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
     * カード番号からカード会社を返却する
     *
     * @return カード会社
     */
    public CreditCardBrand getBrand() {
        return CreditCardBrand.getBrand(getNumber());
    }

    /**
     * 入力されたカード番号を返却する
     *
     * @return 入力されたカード番号
     */
    public String getNumber() {
        return removeSeparators(getText().toString());
    }

    /**
     * 文字列から全てのセパレーターを除去する
     *
     * @param s 文字列
     * @return セパレーター除去後の文字列
     */
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
}
