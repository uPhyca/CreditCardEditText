package com.uphyca.creditcardedittext.subject;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.uphyca.creditcardedittext.CreditCardBrand;
import com.uphyca.creditcardedittext.CreditCardNumberEditText;

import static com.google.common.truth.Truth.assertAbout;

/**
 * Subject for {@link CreditCardNumberEditText} instances.
 */
public final class CreditCardNumberEditTextSubject extends Subject {

    public static CreditCardNumberEditTextSubject assertThat(CreditCardNumberEditText editText) {
        return assertAbout(editTexts()).that(editText);
    }

    private static Subject.Factory<CreditCardNumberEditTextSubject, CreditCardNumberEditText> editTexts() {
        return CreditCardNumberEditTextSubject::new;
    }

    private final CreditCardNumberEditText actual;

    private CreditCardNumberEditTextSubject(FailureMetadata failureMetadata, CreditCardNumberEditText actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public final void hasNumber(String number) {
        check("getNumber()")
                .that(actual.getNumber())
                .isEqualTo(number);
    }

    public final void hasTextString(String number) {
        check("getNumber()")
                .that(actual.getText().toString())
                .isEqualTo(number);
    }

    public final void hasBrand(CreditCardBrand brand) {
        check("getNumber()")
                .that(actual.getBrand())
                .isEqualTo(brand);
    }
}
