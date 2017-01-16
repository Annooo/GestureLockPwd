package com.example.administrator.lockpwd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.example.administrator.lockpwd.R;

/**
 * Created by Administrator on 2017/1/10.
 *
 * 手势密码的图案提示
 */

public class GesturePrompt extends View {

    //行
    private int rowNum = 3;
    //列
    private int colNum = 3;
    //图标的宽度
    private int imgWidth = 15;
    //图标的高度
    private int imgHeight = 15;
    //没有选中的图标
    private Drawable imgNormal;
    //选中的图标
    private Drawable imgPress;
    //画笔
    private Paint paint = null;
    //画笔的宽度
    private int paintWidth = 3;
    //手势密码
    private String pwdStr ;

    private int f = 5;
    private int g = 5;


    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public GesturePrompt(Context context) {
        super(context);
    }
    public GesturePrompt(Context context, AttributeSet attrs){
        super(context, attrs,0);
        //初始化画笔
        paint = new Paint();
        //设置画笔抗锯齿
        paint.setAntiAlias(true);
        //画笔风格
        paint.setStyle(Paint.Style.STROKE);
        //设置画笔的颜色
        paint.setColor(getResources().getColor(R.color.red));
        //画笔宽度
        paint.setStrokeWidth(paintWidth);
        //获取图标资源
        imgNormal = getResources().getDrawable(R.drawable.normal);
        imgPress = getResources().getDrawable(R.drawable.checked);
        if( imgPress != null){
            //获取图标已有的宽高,限制死图标的大小 不需要获取了
            /*imgWidth = imgPress.getIntrinsicWidth();
            imgHeight = imgPress.getIntrinsicHeight();*/
            this.f = (imgWidth / 4);
            this.g = (imgHeight / 4);
            imgNormal.setBounds(0,0,imgWidth,imgHeight);
            imgPress.setBounds(0,0,imgWidth,imgHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //校验图标是否存在
        if( imgNormal == null || imgPress == null){
            return ;
        }
        /**
         * 绘制3*3的图标
         */
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                //计算出每画一个点的偏移量
                int i1 = j * imgHeight + j * this.g;
                int i2 = i * imgWidth + i * this.f;
                //保存当前画布的状态，避免已经画的点位置错乱
                canvas.save();
                //平移
                canvas.translate(i1,i2);
                //获取当前点对应的数字
                String curNum = String.valueOf(colNum * i + (j+1));
                //如果密码不为null
                if( !TextUtils.isEmpty(pwdStr) ){
                    /**
                     * 从传入的密码串里面去那对应的数字，如果有说明选中了，画选中的点
                     * 没有对应的数字，那就画没有选中的点
                     */
                    if( pwdStr.indexOf(curNum) != -1){
                        imgPress.draw(canvas);
                    }else{
                        imgNormal.draw(canvas);
                    }
                }else{
                    // 重置状态
                    imgNormal.draw(canvas);
                }
                //画完一个点 那就取出前面保存的状态
                canvas.restore();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设定密码提示view的大小
        if( imgPress != null){
            setMeasuredDimension( colNum * imgWidth + this.g * (colNum + -1 ), rowNum * imgHeight + this.f * (-1 + rowNum));
        }
    }
    /**
     * 设置密码的方法
     */
    public void setPromptPwd(String pwd ){
        this.pwdStr = pwd;
        invalidate();
    }
}
