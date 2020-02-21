package com.uphyca.creditcardedittext.subject;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import com.uphyca.creditcardedittext.CreditCardDate;
import com.uphyca.creditcardedittext.CreditCardDateEditText;

import static com.google.common.truth.Truth.assertAbout;

/**
 * Subject for {@link CreditCardDateEditText} instances.
 */
public class CreditCardDateEditTextSubject extends Subject {

    public static CreditCardDateEditTextSubject assertThat(CreditCardDateEditText editText) {
        return assertAbout(editTexts()).that(editText);
    }

    private static Subject.Factory<CreditCardDateEditTextSubject, CreditCardDateEditText> editTexts() {
        return CreditCardDateEditTextSubject::new;
    }

    private final CreditCardDateEditText actual;

    private CreditCardDateEditTextSubject(FailureMetadata failureMetadata, CreditCardDateEditText actual) {
        super(failureMetadata, actual);
        this.actual = actual;
    }

    public final void hasDate(CreditCardDate date) {
        check("getNumber()")
                .that(actual.getDate())
                .isEqualTo(date);
    }

    public final void hasTextString(String number) {
        check("getNumber()")
                .that(actual.getText().toString())
                .isEqualTo(number);
    }
}
