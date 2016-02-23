package com.uphyca.creditcardnumberedittext;

import org.assertj.android.api.widget.AbstractTextViewAssert;
import org.assertj.core.api.Assertions;


/**
 * Assertions for {@link CreditCardNumberEditText} instances.
 */
public class CreditCardNumberEditTextAssert extends AbstractTextViewAssert<CreditCardNumberEditTextAssert, CreditCardNumberEditText> {

    public CreditCardNumberEditTextAssert(CreditCardNumberEditText actual) {
        super(actual, CreditCardNumberEditTextAssert.class);
    }

    public CreditCardNumberEditTextAssert hasNumber(String number) {
        this.isNotNull();
        String actualNumber = actual.getNumber();
        Assertions.assertThat(actualNumber).overridingErrorMessage("Expected number <%s> but was <%s>", new Object[]{number, actualNumber}).isEqualTo(number);
        return this;
    }

    public CreditCardNumberEditTextAssert hasBrand(CreditCardBrand brand) {
        this.isNotNull();
        CreditCardBrand actualBrand = actual.getBrand();
        Assertions.assertThat(actualBrand).overridingErrorMessage("Expected brand <%s> but was <%s>", new Object[]{brand, actualBrand}).isEqualTo(brand);
        return this;
    }
}
