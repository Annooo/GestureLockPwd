package com.example.administrator.lockpwd.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.lockpwd.R;
import com.example.administrator.lockpwd.entitye.Point;
import com.example.administrator.lockpwd.tool.AppUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/5.
 * 手势密码的锁的容器
 */

public class GestureContentView extends ViewGroup {

    //每个点区域的宽度
    private int blockWidth;

    //声明一个集合用来封装坐标集合 就是9个点的坐标 和ImageView
    private ArrayList<Point> list;
    private Context context;

    //画线的view
    private DrawLine drawLine;

    //当前手势密码的容器宽高
    private int width;
    private int height;

    //通常表示一个点的大小
    private double baseNum = 2.3;

    //
    private boolean firstInitialise = true;
    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public GestureContentView(Context context, DrawLine.GesturePwdCallBack callBack) {
        super(context );
        this.context = context;
        this.list = new ArrayList<Point>();


        //获取屏幕的分辨率
        int[] screenSize = AppUtil.getScreenDispaly(context);

        this.width = screenSize[0];
        this.height = screenSize[1];
        blockWidth = width / 3;

        //把 9 个点都添加到容器里面去
        addChild();
        //初始化一个画线的view
        drawLine = new DrawLine(context, list, width, height, callBack);
    }
/*
    public GestureContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.list = new ArrayList<Point>();

    }
*/

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /**
         * 第一次进入页面才需要布局一次imageView；
         * 后面都是Point的状态改变了才调用
         */
        if(firstInitialise){
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                int row = i / 3;
                int col = i % 3;

                View view = getChildAt(i);
                view.layout( (int)(col * blockWidth + blockWidth / baseNum),(int)(row * blockWidth + blockWidth/baseNum) , (int)(col * blockWidth+blockWidth - blockWidth/baseNum) ,(int)(row * blockWidth + blockWidth - blockWidth/baseNum));
            }
            firstInitialise = false;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 遍历设置每个子view的大小
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 这个是activity中要调用的方法
     * 设置密码滑动完成的回调进来
     *
     */
    public void setData( ViewGroup parent) {
        //设置画线view的宽高
        LayoutParams params = new LayoutParams(width,width);
        this.setLayoutParams(params);
        drawLine.setLayoutParams(params);
        //把画线的view添加到第一个位置上
        parent.addView(drawLine);
        parent.addView(this);
    }

    /**
     * 添加9个点到容器里面去
     */
    public void addChild() {
        for (int i = 0; i < 9; i++) {
            //添加图片标签ImageView
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.normal);
           /*
            LayoutParams layoutParams = new LayoutParams(blockWidth/8,blockWidth/8);
            imageView.setLayoutParams(layoutParams);*/
            this.addView(imageView);
            invalidate();
            //计算当前行，012 只走一遍
            int row = i / 3;
            //计算当前列，012 走3遍
            int col = i % 3;

            //计算出点的可触碰范围距离左右和上下到每个小方格边框的距离
            int leftX = (int)(col * blockWidth + blockWidth / baseNum);
            int rightX = (int)(col * blockWidth + blockWidth - blockWidth / baseNum);
            int topY = (int)(row * blockWidth + blockWidth / baseNum);
            int bottomY = (int)(row * blockWidth + blockWidth - blockWidth / baseNum);
            Log.i("fsesef"," leftX = "+leftX+" rightX = "+rightX+" topY = "+topY+" bottomY = "+bottomY);

            //初始化一个点对象出来 添加到集合中
            Point point = new Point(leftX,rightX,topY,bottomY,imageView,i+1,blockWidth,col,row);
            list.add(point);

        }
    }

    /**
     * 清除划过的线
     * @param delayTime
     */
    public void clearDrawLine( long delayTime){
        if( drawLine != null ){
            drawLine.clearDrawlineState(delayTime);
        }
    }
}

