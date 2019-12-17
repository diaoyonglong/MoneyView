package com.diaoyonglong.moneyviewlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.diaoyonglong.moneyviewlib.R;

import java.text.NumberFormat;

/**
 * Created by diaoyonglong on 2019/12/13
 *
 * @desc 自定义金额 View
 */
public class MoneyView extends View {

    private int mTextWidth;  //文本显示占用的宽
    private int mTextHeight;  //文本显示占用的高
    private String mMoneyText = "0.00";    //钱文本
    private String mPoint = "."; //小数点
    private String mPointPlaces = "%.2f"; //小数点后2位
    private int mPrefixColor = Color.parseColor("#000000");
    private int mYuanColor = Color.parseColor("#F02828");
    private int mPointColor = Color.parseColor("#EECCDD");
    private int mCentColor = Color.parseColor("#FF4081");
    private int mZheKouColor = Color.parseColor("#FFFFFF");
    private String mPrefix; //前缀
    private String mYuan; //多少元
    private String mCent; //多少分
    private String mZheKou = "折";//折
    private boolean mIsGroupingUsed; //是否开启千分符
    private boolean mIsPrefix; //是否要前缀
    private boolean mIsZheKou; //是否要折扣

    /**
     * 文本 元的大小
     */
    private int mPrefixSize = sp2px(14);
    private int mYuanSize = sp2px(18);
    private int mPointSize = sp2px(14);
    private int mCentSize = sp2px(14);
    private int mZheKouSize = sp2px(14);
    private int mPrefixPadding = dp2px(4); //前缀与分的间隔
    private int mPointPaddingLeft = dp2px(3); //小数点与分的间隔
    private int mPointPaddingRight = dp2px(4); //小数点与分的间隔

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mYuanBound;
    private Rect mPrefixBound;
    private Rect mCentBound;
    private Rect mPointBound;
    private Rect mZheKouBound;
    private Paint mPaint;
    //基线高度
    float maxDescent;

    public MoneyView(Context context) {
        this(context, null);
    }

    public MoneyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoneyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MoneyView, defStyle, 0);

        mMoneyText = typedArray.getString(R.styleable.MoneyView_money_text);

        mYuanColor = typedArray.getColor(R.styleable.MoneyView_yuan_color, mYuanColor);
        mYuanSize = typedArray.getDimensionPixelSize(R.styleable.MoneyView_yuan_size, mYuanSize);

        mCentColor = typedArray.getColor(R.styleable.MoneyView_cent_color, mCentColor);
        mCentSize = typedArray.getDimensionPixelSize(R.styleable.MoneyView_cent_size, mCentSize);

        mPrefix = typedArray.getString(R.styleable.MoneyView_prefix_text);
        mPrefixSize = typedArray.getDimensionPixelSize(R.styleable.MoneyView_prefix_size, mPrefixSize);
        mPrefixColor = typedArray.getColor(R.styleable.MoneyView_prefix_color, mPrefixColor);
        mPrefixPadding = typedArray.getDimensionPixelSize(R.styleable.MoneyView_prefix_padding, mPrefixPadding);

        mPointColor = typedArray.getColor(R.styleable.MoneyView_point_color, mPointColor);
        mPointSize = typedArray.getDimensionPixelSize(R.styleable.MoneyView_point_size, mPointSize);
        mPointPlaces = typedArray.getString(R.styleable.MoneyView_point_places);
        mPointPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.MoneyView_point_padding_left, mPointPaddingLeft);
        mPointPaddingRight = typedArray.getDimensionPixelSize(R.styleable.MoneyView_point_padding_right, mPointPaddingRight);

        mZheKouColor = typedArray.getColor(R.styleable.MoneyView_zhekou_color, mZheKouColor);
        mZheKouSize = typedArray.getDimensionPixelSize(R.styleable.MoneyView_zhekou_size, mZheKouSize);

        mIsZheKou = typedArray.getBoolean(R.styleable.MoneyView_zhekou, false);
        mIsPrefix = typedArray.getBoolean(R.styleable.MoneyView_prefix, true);
        mIsGroupingUsed = typedArray.getBoolean(R.styleable.MoneyView_grouping, false);

        if (TextUtils.isEmpty(mMoneyText)) {
            mMoneyText = "0.00";
        }
        double money = Double.parseDouble(mMoneyText.toString());
        mMoneyText = String.format(mPointPlaces, money);

        typedArray.recycle();

        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG); // 消除锯齿
        mYuanBound = new Rect();
        mCentBound = new Rect();
        mPointBound = new Rect();
        mPrefixBound = new Rect();
        mZheKouBound = new Rect();

        if (TextUtils.isEmpty(mPrefix)) {
            mPrefix = "¥";
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

        int pointPosition = mMoneyText.indexOf(mPoint);
        if (!mMoneyText.contains(mPoint)) {
            pointPosition = mMoneyText.length();
        }
        //获取元的文本
        mYuan = mMoneyText.substring(0, pointPosition);
        //如果使用千分符
        if (mIsGroupingUsed) {
            mYuan = NumberFormat.getInstance().format(Long.valueOf(mYuan));
        }
        //如果不是折扣
        if (!mIsPrefix) {
            mPrefix = "";
        }
        //如果不是折扣
        if (!mIsZheKou) {
            mZheKou = "";
        }
        //获取分的文本
        mCent = mMoneyText.substring(pointPosition + 1, mMoneyText.length());
        //获取元小数点、的占据宽高
        mPaint.setTextSize(mYuanSize);
        mPaint.getTextBounds(mYuan, 0, mYuan.length(), mYuanBound);
        mPaint.getTextBounds(mPoint, 0, mPoint.length(), mPointBound);
        //获取分占据宽高
        mPaint.setTextSize(mCentSize);
        mPaint.getTextBounds(mCent, 0, mCent.length(), mCentBound);
        //获取前缀占据宽高
        mPaint.setTextSize(mPrefixSize);
        mPaint.getTextBounds(mPrefix, 0, mPrefix.length(), mPrefixBound);
        //获取折扣占据宽高
        mPaint.setTextSize(mZheKouSize);
        mPaint.getTextBounds(mZheKou, 0, mZheKou.length(), mZheKouBound);
        //文本占据的宽度
        mTextWidth = mYuanBound.width() + mCentBound.width() + mPrefixBound.width() + mPointBound.width() + mZheKouBound.width()
                + mPrefixPadding + mPointPaddingLeft + mPointPaddingRight + mPrefixPadding;
        // 设置宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            width = specSize + getPaddingLeft() + getPaddingRight();
        } else {
            width = mTextWidth + getPaddingLeft() + getPaddingRight();
        }
        // 设置高度
        // 获取最大字号
        int maxSize = Math.max(mYuanSize, mCentSize);
        maxSize = Math.max(maxSize, mPrefixSize);
        mPaint.setTextSize(maxSize);
        // 获取基线距离
        maxDescent = mPaint.getFontMetrics().descent;
        int maxHeight = Math.max(mYuanBound.height(), mCentBound.height());
        maxHeight = Math.max(maxHeight, mPrefixBound.height());
        // 文本占据的高度 (给顶线和底线留间距)
        mTextHeight = maxHeight + (int) (maxDescent * 2 + 0.5f);

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = mTextHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制X坐标
        int drawX = (getMeasuredWidth() - mTextWidth) / 2;
        float drawY = (getMeasuredHeight() + mTextHeight) / 2 - maxDescent;

        //绘制前缀
        mPaint.setColor(mPrefixColor);
        mPaint.setTextSize(mPrefixSize);
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        canvas.drawText(mPrefix, drawX, drawY, mPaint);
        //绘制元
        drawX += mPrefixBound.width() + mPrefixPadding;
        mPaint.setColor(mYuanColor);
        mPaint.setTextSize(mYuanSize);
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        canvas.drawText(mYuan, drawX, drawY, mPaint);
        //绘制小数点
        drawX += mYuanBound.width() + mPointPaddingLeft;
        mPaint.setColor(mPointColor);
        mPaint.setTextSize(mPointSize);
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        canvas.drawText(mPoint, drawX, drawY, mPaint);
        //绘制分
        drawX += mPointBound.width() + mPointPaddingRight;
        mPaint.setColor(mCentColor);
        mPaint.setTextSize(mCentSize);
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        canvas.drawText(mCent, drawX, drawY, mPaint);
        //绘制折
        drawX += mCentBound.width() + mPrefixPadding;
        mPaint.setColor(mZheKouColor);
        mPaint.setTextSize(mCentSize);
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        canvas.drawText(mZheKou, drawX, drawY, mPaint);

    }

    public String getMoneyText() {
        return mMoneyText;
    }

    public void setMoneyText(String string) {
        if (TextUtils.isEmpty(mMoneyText)) {
            string = "0.00";
        }
        double money = Double.parseDouble(string.toString());
        string = String.format(mPointPlaces, money);
        mMoneyText = string;
        requestLayout();
        postInvalidate();
    }

    /**
     * 开启千分符号
     *
     * @param used yes or no
     */
    public void setGroupingUsed(boolean used) {
        mIsGroupingUsed = used;
    }

    public void setPrefix(boolean used) {
        mIsPrefix = used;
    }

    public void setZheKou(boolean used) {
        mIsZheKou = used;
    }

    public void setmPointPlaces(String string) {
        mPointPlaces = string;
    }

    public Paint getPaint() {
        return mPaint;
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private int dp2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}