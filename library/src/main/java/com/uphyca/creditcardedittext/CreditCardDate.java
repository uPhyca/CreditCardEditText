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

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * expiration date of credit card
 */
public final class CreditCardDate {

    private final String month;
    private final String year;

    public CreditCardDate(@NonNull String month, @NonNull String year) {
        this.month = month;
        this.year = year;
    }

    @NonNull
    public String getMonth() {
        return month;
    }

    @NonNull
    public String getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreditCardDate that = (CreditCardDate) o;

        if (!month.equals(that.month)) return false;
        return year.equals(that.year);
    }

    @Override
    public int hashCode() {
        int result = month.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s/%s",
                TextUtils.isEmpty(month) ? "  " : month,
                TextUtils.isEmpty(year) ? "  " : year);
    }
}
