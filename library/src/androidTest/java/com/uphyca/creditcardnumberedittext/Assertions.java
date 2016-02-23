package com.uphyca.creditcardnumberedittext;

public class Assertions {

    protected Assertions() {
        //noop
    }

    public static CreditCardNumberEditTextAssert assertThat(CreditCardNumberEditText actual) {
        return new CreditCardNumberEditTextAssert(actual);
    }
}
