package com.diaoyonglong.moneyview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.diaoyonglong.moneyviewlib.view.MoneyView;

/**
 * Created by diaoyonglong on 2019/12/13
 *
 * @desc 样例
 */
public class MainActivity extends AppCompatActivity {

    private MoneyView mPriceView;//金额
    private MoneyView mZheView;//折扣

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPriceView = findViewById(R.id.money_view_price);
        mPriceView.setYuanColor(R.color.black);
        mZheView = findViewById(R.id.money_view_zhe);
    }
}
