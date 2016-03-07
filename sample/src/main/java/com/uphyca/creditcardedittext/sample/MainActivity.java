package com.uphyca.creditcardedittext.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.uphyca.creditcardedittext.CreditCardBrand;
import com.uphyca.creditcardedittext.CreditCardNumberEditText;
import com.uphyca.creditcardedittext.CreditCardNumberListener;

public class MainActivity extends AppCompatActivity {

    private CreditCardNumberEditText numberView;
    private TextView brandView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        numberView = (CreditCardNumberEditText) findViewById(R.id.credit_card);
        brandView = (TextView) findViewById(R.id.brand);
        numberView.addNumberListener(new CreditCardNumberListener() {
            @Override
            public void onChanged(String number, CreditCardBrand brand) {
                brandView.setText(brand.name());
            }
        });
    }
}
