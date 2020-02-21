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

import androidx.annotation.NonNull;

import java.util.regex.Pattern;

/**
 * brand of credit card
 * <p>
 * Ref: http://ja.wikipedia.org/wiki/ISO/IEC_7812
 * Ref: http://www.cardservice.co.jp/service/creditcard/csc.html
 * Ref: https://en.wikipedia.org/wiki/Bank_card_number
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

    public int getMinLength() {
        return minLength;
    }

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
            if (each.matches(number)) {
                return each;
            }
        }
        return UNKNOWN;
    }
}
