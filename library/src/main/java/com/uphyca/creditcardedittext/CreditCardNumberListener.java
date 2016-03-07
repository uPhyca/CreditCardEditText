package com.uphyca.creditcardedittext;

/**
 * {@link CreditCardNumberEditText}の変更を受け取るためのリスナー
 */
public interface CreditCardNumberListener {

    /**
     * クレジットカードの番号が変更された時に呼ばれる
     *
     * @param number クレジットカードの番号。セパレーターは含まれない
     * @param brand  クレジットカードのブランド
     */
    void onChanged(String number, CreditCardBrand brand);
}
