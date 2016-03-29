package com.uphyca.creditcardedittext;

import android.text.TextUtils;

import java.util.Locale;

/**
 * クレジットカードの有効期限を表す
 */
public class CreditCardDate {

    private final String month;
    private final String year;

    public CreditCardDate(String month, String year) {
        this.month = month;
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreditCardDate that = (CreditCardDate) o;

        if (month != null ? !month.equals(that.month) : that.month != null) return false;
        return year != null ? year.equals(that.year) : that.year == null;

    }

    @Override
    public int hashCode() {
        int result = month != null ? month.hashCode() : 0;
        result = 31 * result + (year != null ? year.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%s/%s",
                TextUtils.isEmpty(month) ? "  " : month,
                TextUtils.isEmpty(year) ? "  " : year);
    }
}
