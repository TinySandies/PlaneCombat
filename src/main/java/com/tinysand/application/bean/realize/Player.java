package com.tinysand.application.bean.realize;

import com.tinysand.application.bean.support.FireType;
import com.tinysand.application.bean.support.GoodsType;
import com.tinysand.application.bean.standard.Plane;
import com.tinysand.application.util.GameUtils;

import java.awt.*;
import java.util.List;

public class Player extends Plane {

    /**
     * 由于玩家的移动是不规则的，需要使用moveTo()方法，这里move()方法仅提供空实现
     */
    @Override public void move() {  }

    /**
     * 玩家飞机移动方法
     * @param positionX X轴位置
     * @param positionY Y轴位置
     */
    public void moveTo(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * 吃到奖励物品
     * @param goodsList 奖励物品
     */
    public void eatingGoods(List<Goods> goodsList) {
        goodsList.forEach(goods -> {
            if (this.getRectangle().intersects(goods.getRectangle())) {
                if (GoodsType.DOUBLE_FIRE.equals(goods.getGoodsType())) {

                    System.err.println("得到双倍火力");
                    this.setFireType(FireType.DOUBLE_FIRE);
                    // 得打双倍火力，设置得到火力的时间
                    this.gettingDoubleFireTime = System.currentTimeMillis();
                } else {
                    System.err.println("获得一条生命");

                    this.setLife(this.getLife() + Goods.DEFAULT_GOODS_LIFE);
                }

                goods.setBroken(true);
            }
        });
    }

    /**
     * 玩家飞机构造方法
     * @param positionX X轴位置
     * @param positionY Y轴位置
     * @param speed     飞机移动速度
     * @param life      玩家生命
     */
    public Player(int positionX, int positionY, int speed, int life) {
        super(positionX, positionY, speed, life);
        this.appearanceImage = GameUtils.getImage(
                GameUtils.obtainImagePath("hero0"));

        this.width = appearanceImage.getWidth();
        this.height = appearanceImage.getHeight();
    }

    /**
     * 获取玩家飞机的位置和大小用于碰撞检测
     * @return 飞机位置和大小，Rectangle类型
     */
    @Override public Rectangle getRectangle() {
        return new Rectangle(this.positionX, this.positionY,
                this.width, this.height);
    }

    /**
     * 玩家飞机发射出的子弹是否击中敌机
     * @param enemy 敌机
     * @return      是否击中敌机
     */
    public boolean hitTheTarget(Enemy enemy) {
        return this.hitTheTarget(enemy, Player.class).isHit();
    }

}
