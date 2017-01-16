package com.example.administrator.lockpwd.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.administrator.lockpwd.entitye.Point;
import com.example.administrator.lockpwd.tool.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 * 手势密码的线绘制
 */

public class DrawLine extends View {

    //起点坐标 x y
    private int mov_x;
    private int mov_y;
    //画笔
    private Paint paint;
    //画布
    private Canvas canvas;
    //位图
    private Bitmap bitmap;
    //声明一个集合用来封装坐标集合 就是9个点的坐标 和ImageView
    private List<Point> list;
    //记录划过的线
    private List<Pair<Point, Point>> lineList;
    //自动选中的情况，例如 起始是1， 选中的是3，那么1和3之间就夹了个2。这个时候就把2和3同时选中加入到划过的线，这个map记录的就是夹了没有选中的点的情况
    private HashMap<String, Point> autoSelPoints;
    //是否允许绘制
    private boolean allowDraw = true;

    //手势密码容器的宽高
    private int width;
    private int height;
    //当前手指选中了那个点
    private Point point;
    //手势密码的回调
    private GesturePwdCallBack callBack;
    //用户当前绘制的手势密码
    private StringBuilder pwdSb;


    //标识有时间延迟的清除任务是否执行 true 为没有执行，false为已经执行了，
    private boolean b = true;
    //清除所有线的任务接口
    private cleanAllStateLine cleanAllStateLine;

    /**
     * 这个画线的view是在手势密码容器中生成
     *
     * @param context  上下文
     * @param list     装有9个点的集合
     * @param width    手势密码容器的宽
     * @param height   手势密码容器的高
     * @param callBack 手势密码容器的回调
     */
    public DrawLine(Context context, List<Point> list, int width, int height, GesturePwdCallBack callBack) {
        super(context);
        this.width = width;
        this.height = height;
        this.callBack = callBack;
        this.list = list;
        this.pwdSb = new StringBuilder();
        this.lineList = new ArrayList<Pair<Point, Point>>();
        paint = new Paint(Paint.DITHER_FLAG);//新建一支画笔
        //这里的bitmap相当于是个画布一样的 宽高跟手势密码容器的大小一致，手滑动线才能看的见
        bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);//创建一张图片，根据手势密码容器的大小
        canvas = new Canvas();//新建一张画布
        //有的时候paint的抗锯齿不够，就需要为canvas来设置抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.setBitmap(bitmap);
        paint.setStyle(Paint.Style.STROKE);//设置线的样式，实线
        paint.setStrokeWidth(5);//设置线的宽度
        paint.setColor(Color.rgb(60, 90, 156));//设置线的颜色
        paint.setAntiAlias(true);//不显示锯齿
        //初始化所有的间隔没有选中点的情况
        initAutoCheckPointMap();

    }

    /**
     * 初始化所有的间隔没有选中点的情况
     */
    private void initAutoCheckPointMap() {
        autoSelPoints = new HashMap<String, Point>();
        autoSelPoints.put("1,3", getPointByNum(2));
        autoSelPoints.put("1,7", getPointByNum(4));
        autoSelPoints.put("1,9", getPointByNum(5));
        autoSelPoints.put("2,8", getPointByNum(5));
        autoSelPoints.put("3,7", getPointByNum(5));
        autoSelPoints.put("3,9", getPointByNum(6));
        autoSelPoints.put("4,6", getPointByNum(5));
        autoSelPoints.put("7,9", getPointByNum(8));
    }

    /**
     * 根据数字获取对应的点
     */
    public Point getPointByNum(int num) {
        for (Point point : list) {
            if (point.getNum() == num) {
                return point;
            }
        }
        return null;
    }

    /**
     * 判断当前触碰到的点是否在9个点中任意一个点的触发范围内，如果是那就返回这个对应的点Point
     */
    public Point getPointByXY(int x, int y) {

        for (Point point : list) {
            int leftX = point.getLeftX();
            int rightX = point.getRightX();
            //leftX,topY...统一加减30  为了扩大空触控的范围，如果点太小不利于触发
            if (!(leftX - 43 <= x && x < rightX + 43)) {
                //是在这范围区间的会跳到下一个if去判断y轴是否在其范围内
                continue;
            }
            int topY = point.getTopY();
            int bottomY = point.getBottomY();
            if (!(topY - 43 <= y && y < bottomY + 43)) {
                continue;
            }
            //一旦x，y在任意一个point范围内就返回它
            return point;
        }
        //如果 x,y不在任何一个point范围内就返回null
        return null;
    }

    /**
     * 触碰事件
     */
    /**
     * Implement this method to handle touch screen motion events.
     * <p>
     * If this method is used to detect click actions, it is recommended that
     * the actions be performed by implementing and calling
     * {@link #performClick()}. This will ensure consistent system behavior,
     * including:
     * <ul>
     * <li>obeying click sound preferences
     * <li>dispatching OnClickListener calls
     * accessibility features are enabled
     * </ul>
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        try {
            super.onTouchEvent(event);
            //设置画笔颜色为正常的颜色
            paint.setColor(Color.rgb(60, 90, 156));
            //用switch 来处理手势的按下 移动 拿起 的动作
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://在手指按下的时候
                    /**
                     * 判断清除任务是否已经执行了，true：没有执行，
                     * 现在用户要重新绘制密码，所以清除任务没有执行那我们就要手动移除延迟清除任务，然后手动清除所有的线
                     */
                    if (b == true) {
                        this.removeCallbacks(cleanAllStateLine);
                        cleanLine();
                    }
                    //获取当前按下的 x y 坐标点
                    int mov_x = (int) event.getX();
                    int mov_y = (int) event.getY();
                    //根据 x y 坐标去获取对应的point
                    point = getPointByXY(mov_x, mov_y);
                    /**
                     * 如果point不是null ， 那当前按下的xy坐标就是某一个point的范围内，这个点就是起始点
                     * 这时候就要记住点对应的数字按键，
                     * 这个点的状态也要设为按下
                     */
                    if (point != null) {
                        pwdSb.append(point.getNum());
                        point.setPointState(Constants.POINT_STATE_SELECT);
                    }
                    //重新记载本view
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    /**
                     * 移动的时候，要先把已经画好的所有的线清除干净，也就是页面清空，接着在againDrawLineByLineList方法中会把已经记录下来的线段重新画出来
                     */
                    againDrawLineByLineList();
                    //根据当前的xy轴去获取对应范围的点
                    Point movPoint = getPointByXY((int) event.getX(), (int) event.getY());
                    /**
                     * 判断当前点和xy移动到的对应的点movPoint是否存在
                     * 如果两个同时不存在的情况 那就是第一次按下，没有按住点而是空白处，所以我们可以return掉
                     *
                     * 否则else被执行的时候，就是第一下没有按中点，后面接着滑到点上去了，else就会被执行，所以这时候point会是null ，而movPoint 则不是null
                     * 我们就要把这个点设为按下状态，然后记住点对应的数字
                     * 把这个movPoint 赋给 point这个当前点的引用
                     */
                    if (point == null && movPoint == null) {
                        return true;
                    } else {
                        if (point == null) {
                            point = movPoint;
                            point.setPointState(Constants.POINT_STATE_SELECT);
                            pwdSb.append(point.getNum());
                        }
                    }

                    /**
                     * 如果movPoint 是null的（没有移动到一个点的范围内），或者当前点和movPoint一样 ，又或者movPoint的状态是选中的 都是要根据手指滑动的地方来画线
                     *
                     * 记录划过的线的状态用pair来记录，具体可以去查android.util.pair的api
                     */
                    if (movPoint == null || point.equals(movPoint) || movPoint.getPointState() == Constants.POINT_STATE_SELECT) {
                        //从当前点point 和 当前的x,y坐标点之间不停的画线
                        canvas.drawLine(point.getCenterX(), point.getCenterY(), event.getX(), event.getY(), paint);
                    } else {
                        /**
                         * 否则那就是当前手移动到的位置是某一个点的范围并且是没有被选中的,那就从当前点到movPoint之间划一根线，设置按下的状态，
                         * 然后用movPoint和point当前点去检查一下看看这两点之间是否夹了一个没有选中的点，如果是那就把这个中间的点也选中
                         * 然后记录下两个点的线的记录，设置被夹在中间的点的状态为按下，记录这两个点对应的数字，把movPoint赋给point当前点
                         */
                        //在当前点和当下手指划到的点之间画一根线
                        canvas.drawLine(point.getCenterX(), point.getCenterY(), movPoint.getCenterX(), movPoint.getCenterY(), paint);
                        //设置划到对应点的状态为选中
                        movPoint.setPointState(Constants.POINT_STATE_SELECT);
                        //检查当前点和当前移动到的点中间是否夹了个没选中的点
                        Point betweenPoint = checkBetweenPoint(point, movPoint);
                        if (betweenPoint != null && Constants.POINT_STATE_SELECT != betweenPoint.getPointState()) {
                            /**
                             * 如果有夹在中间没有选中的点，那就就可以把这个点也记录下来，
                             *
                             * 记录划过的线的状态用pair来记录，具体可以去查android.util.pair的api
                             */
                            Pair<Point, Point> pair1 = new Pair<Point, Point>(point, betweenPoint);
                            pwdSb.append(betweenPoint.getNum());
                            lineList.add(pair1);
                            //把这两个点对应的数字和线都记录下来
                            Pair<Point, Point> pair2 = new Pair<>(betweenPoint, movPoint);
                            pwdSb.append(movPoint.getNum());
                            lineList.add(pair2);

                            //把夹在中间点的状态设为选中
                            betweenPoint.setPointState(Constants.POINT_STATE_SELECT);
                            //把移动到的点赋给point当前点
                            point = movPoint;
                        } else {
                            /**
                             * 否则movPoint和point当前点之间没有夹未选中的点的话，那就只添加movPoint这个点的线的记录，记录movPoint对应的数字，把movPoint赋给point当前点
                             */
                            Pair<Point, Point> pair = new Pair<Point, Point>(point, movPoint);
                            lineList.add(pair);
                            pwdSb.append(movPoint.getNum());
                            point = movPoint;
                        }
                    }
                    //重新加载view
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    /**
                     * 当手指拿起来的时候，就把用户绘制的密码用回调发回去,处理的事情交给调用者
                     */
                    if (callBack != null) {
                        callBack.onGesturePwdInput(pwdSb.toString());
                    }
                    break;
            }

        } catch (Exception e) {
            Log.i("SelfException", "DrawLine-onTouchEvent e.getMessage()" + e.getMessage());
        }
        return true;
    }

    /**
     * 指定时间去清除绘制的状态
     */
    public void clearDrawlineState(long delayTime) {
        //每次执行清除任务，都要初始化是否执行延迟任务 为true ，也就是还没有执行延迟任务
        b = true;
        //首先判断时间是否大于0，如果就不是那就不需要绘制错误的状态线
        //如果大于0 那就要绘制红色的错误状态线
        if (delayTime > 0) {
            //改变是否可绘制的状态，如果用户划入第二次手势密码错误 不更改状态为false,用户就可以立马再次划入密码
//            allowDraw = false;
            //绘制红色的状态线
            drawErrorStateLine();
        }
        cleanAllStateLine = new cleanAllStateLine();
        //执行延迟任务
        this.postDelayed(cleanAllStateLine, delayTime);
    }

    /**
     * 清除页面上已有的绘制线和状态，用Runnable 来执行清楚操作，因为可以用到view.postDelayed来执行延迟操作，实际上也是去调用了 mHandler的postDelayed方法
     */
    final class cleanAllStateLine implements Runnable {
        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            //如果本延迟任务已经执行，那么做个标识标注本清楚任务已经执行，然后手按下的时候那里就不会移除本任务
            b = false;
            cleanLine();
        }
    }

    public void cleanLine() {
        //首先需要重置构建密码的StringBuilder
        pwdSb = new StringBuilder();
        //清空已经保存的划过的点
        lineList.clear();
        //重新绘制屏幕，是根据记录手势划过的路径来画的，也就是lineList，但是这里我们把它清空了，所以屏幕上就是空白的正常状态
        againDrawLineByLineList();
        //设置9个点的状态都是正常状态
        for (Point point : list) {
            point.setPointState(Constants.POINT_STATE_NORMAL);
        }
        //重新加载页面
        invalidate();
    }

    /**
     * 清除屏幕上所有的线，然后画出集合里的线
     */
    public void againDrawLineByLineList() {
        //清空屏幕，上所有的线
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //设置线的颜色为正常色
        paint.setColor(Color.rgb(60, 90, 156));
        //循环把记录的线 全部画出来
        for (Pair<Point, Point> pair : lineList) {
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(), pair.second.getCenterX(), pair.second.getCenterY(), paint);
        }
    }

    /**
     * 检查两点之间是否夹了没有选中的点
     * 如果有就返回这个没有选中的点
     */
    public Point checkBetweenPoint(Point startPoint, Point endPoint) {
        String key = null;
        if (startPoint.getNum() > endPoint.getNum()) {
            key = endPoint.getNum() + "," + startPoint.getNum();
        } else {
            key = startPoint.getNum() + "," + endPoint.getNum();
        }
        return autoSelPoints.get(key);
    }

    /**
     * 绘制错误 红色的线
     */
    public void drawErrorStateLine() {
        //清除用户自己的划入的所有的线
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //设置画笔的颜色 错误的颜色为红色
        paint.setColor(Color.rgb(254, 0, 0));
        //根据用户划过的线 重新绘制成红色
        for (Pair<Point, Point> pair : lineList) {
            //pair对象中存放的当前点和下一个点的线，也就是所有的线段
            //把所有的点的状态都设为错误
            pair.first.setPointState(Constants.POINT_STATE_ERROR);
            pair.second.setPointState(Constants.POINT_STATE_ERROR);
            //把用户划过的线换成红色
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(), pair.second.getCenterX(), pair.second.getCenterY(), paint);
        }
        invalidate();
    }

    /**
     * 画位图
     */
    protected void onDraw(Canvas canvas) {
        //有的时候paint的抗锯齿不够，就需要为canvas来设置抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    /**
     * 定义个回调的接口，处理滑动完成后密码是否正确的处理
     */
    public interface GesturePwdCallBack {
        /**
         * 用户 手动滑动完密码的时候，用于返回滑动完的密码，根据密码 处理相应的逻辑
         */
        public void onGesturePwdInput(String inputPwd);

    }

    /**
     * 释放资源
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeCallbacks(cleanAllStateLine);
    }
}
