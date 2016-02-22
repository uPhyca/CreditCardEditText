package com.uphyca.creditcardnumberedittext;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;

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

    // TODO: 要修正
    private final TextWatcher textWatcher = new TextWatcher() {

        private String beforeText;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            beforeText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            final String beforeCardNumber = removeSeparator(beforeText);
            final int beforeRawLength = beforeText.length();

            final String afterCardNumber = removeSeparator(s.toString());
            final int afterRawLength = s.length();
            final StringBuilder afterRawText = new StringBuilder(s);

            boolean trimmed = afterRawLength < beforeRawLength;
            if (hasSameSequence(beforeCardNumber, afterCardNumber) && trimmed) {
                final int separatorPosition = beforeText.indexOf(SEPARATOR);
                if (separatorPosition > 0 && beforeText.charAt(separatorPosition) != SEPARATOR) {
                    // separator が消されたので一つ前の数字も消す
                    afterRawText.deleteCharAt(separatorPosition - 1);
                }
            }

            final CreditCardBrand brand = CreditCardBrand.getBrand(afterCardNumber);
            // この後にseparatorを一括で挿入するので、すでに挿入済みのseparatorを削除しておく
            removeSeparator(afterRawText);
            insertSeparator(afterRawText, brand);

            if (!hasSameSequence(s, afterRawText)) {
                s.replace(0, afterRawLength, afterRawText.toString());
            }
        }
    };

    // TODO: 要修正
    private static class CreditCardNumberKeyListener extends NumberKeyListener {
        private final char[] accepted = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', SEPARATOR};

        @Override
        protected char[] getAcceptedChars() {
            return accepted;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            String cardNumber = removeSeparator(dest.toString());
            CreditCardBrand brand = CreditCardBrand.getBrand(cardNumber);
            int maxLength = brand.getMaxLength() + brand.getSeparatorCount();

            // 入力文字数をチェック
            CharSequence lengthOut = lengthFilter(maxLength, source, start, end, dest, dstart, dend);
            if (hasSameSequence(lengthOut, EMPTY)) {
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
            if (out != null) {
                source = out;
                start = 0;
                end = out.length();
            }

            // カード番号情報が定まらない場合、書式化不能なので処理を打ち切る
            if (brand == CreditCardBrand.UNKNOWN) {
                return out;
            }

            if (brand.isSeparatorPosition(dstart) && source.length() > start && source.charAt(start) == SEPARATOR) {
                if (dest.length() > dstart && dest.charAt(dstart) == SEPARATOR) {
                    return EMPTY;
                } else {
                    return String.valueOf(SEPARATOR);
                }
            }

            return out;
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
        return removeSeparator(getText().toString());
    }

    /**
     * 文字列から全てのセパレーターを除去する
     *
     * @param s 文字列
     * @return セパレーター除去後の文字列
     */
    private static String removeSeparator(String s) {
        return s.replace(String.valueOf(SEPARATOR), EMPTY);
    }

    /**
     * 文字列から全てのセパレーターを除去する
     *
     * @param sb 文字列
     */
    private static void removeSeparator(StringBuilder sb) {
        for (int i = sb.length() - 1; i >= 0; i--) {
            if (sb.charAt(i) == SEPARATOR) {
                sb.deleteCharAt(i);
            }
        }
    }

    /**
     * ２つの文字シーケンスが同じ等価な場合に真を返す
     *
     * @param a 文字シーケンス
     * @param b 文字シーケンス
     * @return 等価な場合true、そうでない場合false
     */
    private static boolean hasSameSequence(CharSequence a, CharSequence b) {
        if (a == null) {
            return b == null;
        }
        if (b != null) {
            return a.toString().equals(b.toString());
        }
        return false;
    }

    /**
     * カード番号情報に対応する書式に従ってセパレーターを挿入する
     *
     * @param sb    カード番号
     * @param brand カード番号情報
     */
    private void insertSeparator(StringBuilder sb, CreditCardBrand brand) {
        final int[] format = brand.getFormat();
        int i = 0;
        for (int number : format) {
            i += number;
            if (sb.length() > i) {
                sb.insert(i, SEPARATOR);
            } else {
                break;
            }
            i++;
        }
    }
}