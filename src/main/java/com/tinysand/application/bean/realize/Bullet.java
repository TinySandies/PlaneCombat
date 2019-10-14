package com.tinysand.application.bean.realize;

import com.tinysand.application.bean.standard.Direction;
import com.tinysand.application.bean.standard.Entity;
import com.tinysand.application.util.GameUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 子弹实体类
 */
public class Bullet extends Entity {

    /**
     * 子弹图片资源
     */
    private static final BufferedImage imageSource =
            GameUtils.getImage("/images/bullet.png");

    /**
     * 默认子弹移动速度
     */
    private static final int DEFAULT_BULLET_SPEED = 5;

    /**
     * 开火方向 Direction.DOWN 下 | Direction.UP 上，默认向下
     */
    private Direction fireDirection;

    /**
     * 子弹全参构造方法
     * @param positionX X轴位置
     * @param positionY Y轴位置
     * @param speed     子弹移动速度
     * @param fireDirection 开火方向
     */
    @SuppressWarnings("all") public Bullet
    (int positionX, int positionY, int speed, Direction fireDirection) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.speed = speed;
        this.appearanceImage = imageSource;

        this.width = appearanceImage.getWidth();
        this.height = appearanceImage.getHeight();

        this.fireDirection = fireDirection;
    }

    /**
     * 带默认速度的子弹构造方法
     * @param positionX     X轴位置
     * @param positionY     Y轴位置
     * @param fireDirection 开火方向
     */
    public Bullet(int positionX, int positionY, Direction fireDirection)
    {
        this(positionX, positionY, DEFAULT_BULLET_SPEED, fireDirection);
    }

    /**
     * 仅带坐标的子弹构造方法
     * @param positionX X轴位置
     * @param positionY Y轴位置
     */
    @SuppressWarnings("unused")
    public Bullet(int positionX, int positionY) {
        this(positionX, positionY, DEFAULT_BULLET_SPEED, Direction.DOWN);
    }

    /**
     * 子弹移动方法，根据设定方向决定向上或向下
     */
    @Override public void move() {
//        System.out.println("子弹[" + this +
//                "]移动方法被调用，现在Y轴位置是：" + this.positionY);
        if (this.outOfBounds()){
            if (this.fireDirection.equals(Direction.DOWN))
                this.positionY += this.speed;
            else if (this.fireDirection.equals(Direction.UP))
                this.positionY -= this.speed;
        } else {
            this.setBroken(true);
        }
    }

    @Override public Rectangle getRectangle() {
        return new Rectangle(this.positionX, this.positionY,
                this.width, this.height);
    }

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

}
