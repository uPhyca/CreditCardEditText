package com.uphyca.creditcardedittext;

import org.assertj.android.api.widget.AbstractTextViewAssert;
import org.assertj.core.api.Assertions;

/**
 * Assertions for {@link CreditCardDateEditText} instances.
 */
public class CreditCardDateEditTextAssert extends AbstractTextViewAssert<CreditCardDateEditTextAssert, CreditCardDateEditText> {

    public CreditCardDateEditTextAssert(CreditCardDateEditText actual) {
        super(actual, CreditCardDateEditTextAssert.class);
    }

    public CreditCardDateEditTextAssert hasDate(CreditCardDate date) {
        this.isNotNull();
        CreditCardDate actualDate = actual.getDate();
        Assertions.assertThat(actualDate).overridingErrorMessage("Expected date <%s> but was <%s>", new Object[]{date, actualDate}).isEqualTo(date);
        return this;
    }
}
