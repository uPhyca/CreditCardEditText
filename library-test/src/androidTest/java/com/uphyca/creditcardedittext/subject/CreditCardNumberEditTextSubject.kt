package com.uphyca.creditcardedittext.subject

import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.uphyca.creditcardedittext.CreditCardBrand
import com.uphyca.creditcardedittext.CreditCardNumberEditText

/**
 * Subject for [CreditCardNumberEditText] instances.
 */
class CreditCardNumberEditTextSubject private constructor(
    failureMetadata: FailureMetadata,
    private val actual: CreditCardNumberEditText
) : Subject(failureMetadata, actual) {

    fun hasNumber(number: String?) {
        check("getNumber()")
            .that(actual.number)
            .isEqualTo(number)
    }

    fun hasTextString(number: String?) {
        check("getNumber()")
            .that(actual.text.toString())
            .isEqualTo(number)
    }

    fun hasBrand(brand: CreditCardBrand?) {
        check("getNumber()")
            .that(actual.brand)
            .isEqualTo(brand)
    }

    companion object {

        @JvmStatic
        fun assertThat(editText: CreditCardNumberEditText?): CreditCardNumberEditTextSubject {
            return assertAbout(editTexts()).that(editText)
        }

        private fun editTexts(): Factory<CreditCardNumberEditTextSubject, CreditCardNumberEditText> {
            return Factory { failureMetadata, actual ->
                CreditCardNumberEditTextSubject(failureMetadata, actual)
            }
        }
    }
}
