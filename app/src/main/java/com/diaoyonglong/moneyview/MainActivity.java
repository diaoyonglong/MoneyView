package com.diaoyonglong.moneyview;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.diaoyonglong.moneyviewlib.view.MoneyView;

/**
 * Created by diaoyonglong on 2019/12/13
 *
 * @desc 样例
 */
public class MainActivity extends AppCompatActivity {

    private MoneyView mPriceView;//金额
    private MoneyView mZheView;//折扣
    private TextView mtxtTypeface;
    private Typeface TF1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //从asset 读取字体
        //得到AssetManager
        AssetManager mgr = getAssets();
        //根据路径得到Typeface
        TF1 = Typeface.createFromAsset(mgr, "fonts/DINOT-Bold.ttf");//

        mPriceView = findViewById(R.id.money_view_price);
        mPriceView.setYuanColor(R.color.black);
        mZheView = findViewById(R.id.money_view_zhe);
        mtxtTypeface = findViewById(R.id.txt_typeface);
        mtxtTypeface.setTypeface(TF1);
    }
}
