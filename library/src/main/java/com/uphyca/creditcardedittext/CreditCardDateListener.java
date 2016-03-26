package com.uphyca.creditcardedittext;

/**
 * {@link CreditCardDateEditText}の変更を受け取るためのリスナー
 */
public interface CreditCardDateListener {

    /**
     * クレジットカードの番号が変更された時に呼ばれる
     *
     * @param date クレジットカードの有効期限
     */
    void onChanged(CreditCardDate date);
}
