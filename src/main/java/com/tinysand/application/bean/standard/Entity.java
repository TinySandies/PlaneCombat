package com.tinysand.application.bean.standard;

import com.tinysand.application.util.Constant;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 程序中所有实体基类，定义基本的共同属性及方法，实现所有实体都会用到的移动方法
 */
@Getter
public abstract class Entity implements Movable, Boundary {

    protected int positionX, positionY;   // 实体的X，Y轴位置

    protected int speed;                  // 实体的移动速度

    protected int life;                   // 实体的生命，除玩家外都是 1

    protected boolean broken = false;     // 当前物体是否消亡

    protected int width, height;          // 实体的宽度 & 高度

    protected BufferedImage appearanceImage; // 实体的外观

    /**
     * 根据实体的宽度与高度构建矩形以便对物体进行碰撞检测
     * @return 物体的矩形区域
     */
    public abstract Rectangle getRectangle();

    /**
     * 返回物体是否损坏的标志变量，用以决定是否清除物体
     * @return 是否损坏
     */
    public boolean isBroken() { return this.broken; }

    /**
     * 设置物体损毁变量
     * @param broken 是否损坏
     */
    public void setBroken(boolean broken) { this.broken = broken; }

    /**
     * 物体是否越界
     * @return true 物体越界 | false 物体没有越界
     */
    @Override public boolean outOfBounds() {
        return this.positionY <= Constant.DEFAULT_FRAME_HEIGHT
                && this.positionY >= 0;
    }

}
