package com.uphyca.creditcardnumberedittext;

import android.support.annotation.NonNull;

/**
 * カード会社に応じたカード番号情報
 * 参考: http://ja.wikipedia.org/wiki/ISO/IEC_7812
 * 参考: http://www.cardservice.co.jp/service/creditcard/csc.html
 */
public enum CreditCardBrand {
    UNKNOWN(8, 19, new int[]{}), // Defined 'ISO IEC/7812'
    VISA(13, 16, new int[]{4, 4, 4, 4}),
    MASTER_CARD(16, 16, new int[]{4, 4, 4, 4}),
    AMERICAN_EXPRESS(15, 15, new int[]{4, 6, 5}),
    JCB(16, 16, new int[]{4, 4, 4, 4}),
    DINERS_CLUB(14, 14, new int[]{4, 6, 4});

    private final int minLength;
    private final int maxLength;
    private final int[] format;
    private final int separatorCount;

    CreditCardBrand(int minLength, int maxLength, int[] format) {
        this.minLength = minLength;
        this.maxLength = maxLength;
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
        if (number.startsWith("4")) {
            return VISA;
        } else if (number.startsWith("5")) {
            return MASTER_CARD;
        } else if (number.startsWith("34") || number.startsWith("37")) {
            return AMERICAN_EXPRESS;
        } else if (number.startsWith("35")) {
            return JCB;
        } else if (number.startsWith("3")) {
            return DINERS_CLUB;
        }
        return UNKNOWN;
    }

}