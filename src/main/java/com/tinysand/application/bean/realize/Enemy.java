package com.tinysand.application.bean.realize;

import com.tinysand.application.bean.standard.Plane;
import com.tinysand.application.util.GameUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy extends Plane {

    /**
     * 敌机图片资源
     */
    private static final BufferedImage imageSource =
            GameUtils.getImage(
                    GameUtils.obtainImagePath("airplane"));

    /**
     * 敌机默认只有一条生命
     */
    private static final int DEFAULT_ENEMY_LIFE = 1;

    /**
     * 击毁敌机后默认得到的分数
     */
    private static final int DEFAULT_ENEMY_SCORE = 5;

    /**
     * 敌机构造方法
     * @param positionX X轴位置
     * @param positionY Y轴位置
     */
    public Enemy(int positionX, int positionY) {
        this(positionX, positionY, DEFAULT_ENEMY_SCORE);
    }

    /**
     * 敌机全参构造方法，可以指定敌机分数
     * @param positionX X轴位置
     * @param positionY Y轴位置
     * @param score     击毁敌机后得到的分数
     */
    private Enemy(int positionX, int positionY, int score) {
        super(positionX, positionY, DEFAULT_ENEMY_LIFE);

        this.score = score;

        this.appearanceImage = imageSource;

        this.width = appearanceImage.getWidth();
        this.height = appearanceImage.getHeight();

//        this.bulletList = new ArrayList<>();
    }

    /**
     * 敌机移动方法，没有越界则移动，越界则设置为损毁
     */
    @Override public void move() {
        if (this.outOfBounds())
            this.positionY += this.speed;
        else
            this.setBroken(true);
    }

//    /**
//     * 返回击毁敌机所能获得的分数
//     * @return 分数
//     */
//    public int getScore() { return this.score; }

    @Override public Rectangle getRectangle() {
        return new Rectangle(this.positionX, this.positionY,
                this.width, this.height);
    }

    /**
     * 当前飞机子弹是否击中玩家飞机
     * @param player 玩家飞机
     * @return 是否击中玩家飞机
     */
    public boolean hitTheTarget(Player player) {
        return this.hitTheTarget(player, this.getClass()).isHit();
    }

}
