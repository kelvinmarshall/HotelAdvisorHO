package dev.marshall.hoteladvisorho;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;

import dev.marshall.hoteladvisorho.common.Common;

public class creditcard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcard);

        String amt=getIntent().getStringExtra("amtcredit");
        CardForm cardForm= findViewById(R.id.cardform);
        TextView txtDes= findViewById(R.id.payment_amount);
        Button btnPay= findViewById(R.id.btn_pay);

        txtDes.setText(amt);
        btnPay.setText(String.format("Payer %s",txtDes.getText()));

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {

            }
        });
    }
}
