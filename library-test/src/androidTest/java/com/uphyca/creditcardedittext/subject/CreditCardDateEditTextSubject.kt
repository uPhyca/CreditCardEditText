package com.uphyca.creditcardedittext.subject

import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.uphyca.creditcardedittext.CreditCardDate
import com.uphyca.creditcardedittext.CreditCardDateEditText

/**
 * Subject for [CreditCardDateEditText] instances.
 */
class CreditCardDateEditTextSubject private constructor(
    failureMetadata: FailureMetadata,
    private val actual: CreditCardDateEditText
) : Subject(failureMetadata, actual) {

    fun hasDate(date: CreditCardDate?) {
        check("getNumber()")
            .that(actual.date)
            .isEqualTo(date)
    }

    fun hasTextString(number: String?) {
        check("getNumber()")
            .that(actual.text.toString())
            .isEqualTo(number)
    }

    companion object {

        @JvmStatic
        fun assertThat(editText: CreditCardDateEditText?): CreditCardDateEditTextSubject {
            return assertAbout(editTexts()).that(editText)
        }

        private fun editTexts(): Factory<CreditCardDateEditTextSubject, CreditCardDateEditText> {
            return Factory { failureMetadata, actual ->
                CreditCardDateEditTextSubject(failureMetadata, actual)
            }
        }
    }
}
