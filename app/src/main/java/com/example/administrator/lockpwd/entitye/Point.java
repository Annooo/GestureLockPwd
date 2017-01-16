package com.example.administrator.lockpwd.entitye;

import android.widget.ImageView;

import com.example.administrator.lockpwd.R;
import com.example.administrator.lockpwd.tool.Constants;

/**
 * Created by Administrator on 2017/1/5.
 * 9个点的坐标实体
 */

public class Point {

    //左边x的值
    private int leftX;
    //右边x的值
    private int rightX;
    //上边y的值
    private int topY;
    //下边y的值
    private int bottomY;
    //这个点的ImageView
    private ImageView imageView;
    //blockWidth
    private int blockWidth;
    //中心x值
    private int centerX;
    //中心y值
    private int centerY;
    //当前点的状态：没选中 选中 错误
    private int pointState;
    //这个点代表的数字
    private int num;
    //该点在第几行
    private int row;
    //该点在第几列
    private int col;

    //没有选中的baseNum 数字越小，圈越小
    private double baseNumNormal = 2.3;
    //错误和选中的baseNum
    private double baseNumErrorSel = 4.5;

    public Point(int leftX, int rightX, int topY, int bottomY, ImageView image, int num, int blockWidth, int col, int row) {
        this.leftX = leftX;
        this.rightX = rightX;
        this.topY = topY;
        this.bottomY = bottomY;
        this.imageView = image;
        this.blockWidth = blockWidth;
        this.row = row;
        this.col = col;

        this.centerX = (leftX + rightX) / 2;
        this.centerY = (topY + bottomY) / 2;

        this.num = num;
    }
   /* @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bottomY;
        result = prime * result + ((imageView == null) ? 0 : imageView.hashCode());
        result = prime * result + leftX;
        result = prime * result + rightX;
        result = prime * result + topY;
        return result;
    }*/

    public void setBlockWidth(int blockWidth) {
        this.blockWidth = blockWidth;
    }

    public int getBlockWidth() {
        return blockWidth;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Point other = (Point) obj;
        if (bottomY != other.bottomY)
            return false;
        if (imageView == null) {
            if (other.imageView != null)
                return false;
        } else if (!imageView.equals(other.imageView))
            return false;
        if (leftX != other.leftX)
            return false;
        if (rightX != other.rightX)
            return false;
        if (topY != other.topY)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Point{" +
                "leftX=" + leftX +
                ", rightX=" + rightX +
                ", topY=" + topY +
                ", bottomY=" + bottomY +
                ", imageView=" + imageView +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                ", pointState=" + pointState +
                ", num=" + num +
                '}';
    }

    public int getLeftX() {
        return leftX;
    }

    public void setLeftX(int leftX) {
        this.leftX = leftX;
    }

    public int getRightX() {
        return rightX;
    }

    public void setRightX(int rightX) {
        this.rightX = rightX;
    }

    public int getTopY() {
        return topY;
    }

    public void setTopY(int topY) {
        this.topY = topY;
    }

    public int getBottomY() {
        return bottomY;
    }

    public void setBottomY(int bottomY) {
        this.bottomY = bottomY;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getPointState() {
        return pointState;
    }

    public void setPointState(int pointState) {
        this.pointState = pointState;
        switch (pointState) {
            case Constants.POINT_STATE_ERROR:
                imageView.layout((int)(col * blockWidth + blockWidth / baseNumErrorSel),(int)( row * blockWidth + blockWidth / baseNumErrorSel),
                        (int)( col * blockWidth + blockWidth - blockWidth / baseNumErrorSel),(int)( row * blockWidth + blockWidth - blockWidth / baseNumErrorSel));
                this.imageView.setImageResource(R.drawable.error);
                break;
            case Constants.POINT_STATE_NORMAL:
                imageView.layout((int)(col * blockWidth + blockWidth / baseNumNormal), (int)(row * blockWidth + blockWidth / baseNumNormal),
                        (int)(col * blockWidth + blockWidth - blockWidth / baseNumNormal), (int)(row * blockWidth + blockWidth - blockWidth / baseNumNormal));
                this.imageView.setImageResource(R.drawable.normal);
                break;
            case Constants.POINT_STATE_SELECT:
                imageView.layout((int)(col * blockWidth + blockWidth / baseNumErrorSel), (int)(row * blockWidth + blockWidth / baseNumErrorSel),
                        (int)(col * blockWidth + blockWidth - blockWidth / baseNumErrorSel), (int)(row * blockWidth + blockWidth - blockWidth / baseNumErrorSel));
                this.imageView.setImageResource(R.drawable.checked);
                break;
        }
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
