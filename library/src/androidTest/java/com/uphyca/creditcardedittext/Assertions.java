package com.uphyca.creditcardedittext;

public class Assertions {

    protected Assertions() {
        //noop
    }

    public static CreditCardNumberEditTextAssert assertThat(CreditCardNumberEditText actual) {
        return new CreditCardNumberEditTextAssert(actual);
    }

    public static CreditCardDateEditTextAssert assertThat(CreditCardDateEditText actual) {
        return new CreditCardDateEditTextAssert(actual);
    }
}
