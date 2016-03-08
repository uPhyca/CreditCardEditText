package com.uphyca.creditcardedittext;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

/**
 * カード会社に応じたカード番号情報
 * 参考: http://ja.wikipedia.org/wiki/ISO/IEC_7812
 * 参考: http://www.cardservice.co.jp/service/creditcard/csc.html
 * 参考: https://en.wikipedia.org/wiki/Bank_card_number
 */
public enum CreditCardBrand {

    UNKNOWN(8, 19, "^$", new int[]{}), // Defined 'ISO IEC/7812'
    VISA(13, 16, "^4.*", new int[]{4, 4, 4, 4}),
    MASTER_CARD(16, 16, "^5[1-5].*", new int[]{4, 4, 4, 4}),
    AMERICAN_EXPRESS(15, 15, "^3[47].*", new int[]{4, 6, 5}),
    JCB(16, 16, "^35.*", new int[]{4, 4, 4, 4}),
    DINERS_CLUB(14, 14, "^3[0689].*", new int[]{4, 6, 4});

    private final int minLength;
    private final int maxLength;
    private final Pattern pattern;
    private final int[] format;
    private final int separatorCount;

    CreditCardBrand(int minLength, int maxLength, String pattern, int[] format) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pattern = Pattern.compile(pattern);
        this.format = format;
        this.separatorCount = Math.max(0, format.length - 1);
    }

    /**
     * @return 番号の最小長
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * @return 番号の最大長
     */
    public int getMaxLength() {
        return maxLength;
    }

    @NonNull
    public int[] getFormat() {
        return format;
    }

    public int getSeparatorCount() {
        return separatorCount;
    }

    /**
     * 指定のカード番号がこのブランドのものかどうか判定する
     *
     * @param number カード番号
     * @return ブランドに合致する場合はtrue、そうでない場合はfalse
     */
    public boolean matches(String number) {
        return pattern.matcher(number).matches();
    }

    public boolean isSeparatorPosition(int index) {
        int i = 0;
        for (int number : format) {
            i += number;
            if (i > index) {
                return false;
            }
            if (i == index) {
                return true;
            }
            i++;
        }
        return false;
    }

    @NonNull
    public static CreditCardBrand getBrand(@NonNull String number) {
        for (CreditCardBrand each : CreditCardBrand.values()) {
            boolean matches = each.matches(number);
            if (matches) {
                return each;
            }
        }
        return UNKNOWN;
    }
}