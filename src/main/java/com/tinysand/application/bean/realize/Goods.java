package com.tinysand.application.bean.realize;

import com.tinysand.application.bean.standard.Entity;
import com.tinysand.application.bean.standard.GoodsType;
import com.tinysand.application.util.Constant;
import com.tinysand.application.util.GameUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Goods extends Entity {

    /**
     * 默认吃到货物后可以获得的生命数
     */
    @SuppressWarnings("all")
    public static final int DEFAULT_GOODS_LIFE = 1;

    /**
     * 双倍火力持续时间
     */
    public static final int DOUBLE_FIRE_DURATION = 3500;

    /**
     * 奖励物品外观图片
     */
    private static BufferedImage imageSource =
            GameUtils.getImage(GameUtils.obtainImagePath("bee"));


    /**
     * 奖励物品构造方法，传入初始位置
     * @param positionX X轴位置
     * @param positionY Y轴位置
     */
    public Goods(int positionX, int positionY) {
        this(positionX, positionY, Constant.DEFAULT_ITEM_SPEED);
    }

    /**
     * 奖励物品构造方法，传入初始位置和移动速度
     * @param positionX X轴位置
     * @param positionY Y轴位置
     * @param speed     移动速度
     */
    @SuppressWarnings("all")
    public Goods(int positionX, int positionY, int speed) {
        this.appearanceImage = imageSource;
        randomGoodsType();
        this.speed = speed;
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = appearanceImage.getWidth();
        this.height = appearanceImage.getHeight();
    }

    /**
     * 随机奖励类型
     */
    private void randomGoodsType() {
        this.goodsType = new Random().nextBoolean()
                ? GoodsType.DOUBLE_FIRE : GoodsType.ONE_LIFE;
    }

    /**
     * 奖励类型
     */
    private GoodsType goodsType;

    /**
     * 获取奖励类型
     * @return 奖励类型
     */
    @SuppressWarnings("all")
    public GoodsType getGoodsType() { return this.goodsType; }

//    /**
//     * 获取吃到奖励物品后得到的生命数
//     * @return 生命数
//     */
//    public int getLife() { return this.life; }

    /**
     * 奖励物品的移动方法
     */
    @Override public void move() {
        if (this.outOfBounds())
            this.positionY += this.speed;
        else
            this.setBroken(true);
    }

    @Override public Rectangle getRectangle() {
        return new Rectangle(this.positionX, this.positionY,
                this.width, this.height);
    }

}
