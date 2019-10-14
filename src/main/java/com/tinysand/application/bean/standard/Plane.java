package com.tinysand.application.bean.standard;

import com.tinysand.application.bean.realize.Bullet;
import com.tinysand.application.bean.realize.Goods;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 所有飞机类的基类，定义所有飞机的公共方法，实现飞机行为接口方法
 */
public abstract class Plane extends Entity implements Behavior {

    /**
     * 机体默认的移动速度
     */
    private static final int DEFAULT_SPEED = 2;

    /**
     * 是否是双倍火力，默认是单发 SINGLE_FIRE
     */
    private FireType fireType = FireType.SINGLE_FIRE;

    protected int score;             // 击毁敌机所能获得的分数和玩家当前分数

    protected long gettingDoubleFireTime;                // 获得双倍火力的时间

    private List<Bullet[]> bulletList;                   // 子弹集合

    private long previousShootingTime;                   // 上次射击时间

    private static final long shootingCoolingTime = 500; // 射击冷却时间

    /**
     * 全参构造方法
     * @param positionX  X轴位置
     * @param positionY  Y轴位置
     * @param speed      飞机移动速度
     * @param life       机体生命数
     */
    protected Plane(int positionX, int positionY, int speed, int life) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.speed = speed;
        this.life = life;
        this.bulletList = new CopyOnWriteArrayList<>();
    }

    /**
     * 使用默认移动速度的构造方法
     * @param positionX  X轴位置
     * @param positionY  Y轴位置
     * @param life       机体生命数
     */
    protected Plane(int positionX, int positionY, int life) {
        this(positionX, positionY, DEFAULT_SPEED, life);
    }

    @Override public void fire(Direction fireDirection) {

        int bulletPositionX, bulletPositionY;  // 子弹在X，Y轴位置

        // 能否射击
        boolean canShoot = (new Random().nextBoolean()) && canShoot();

        if (fireType.equals(FireType.SINGLE_FIRE) && canShoot) {        // 单发火力
            bulletPositionX = (this.positionX + this.width / 2);
            bulletPositionY = (this.positionY + 25);

            bulletList.add(new Bullet[] {
                    new Bullet(bulletPositionX, bulletPositionY, fireDirection) });

            previousShootingTime = System.currentTimeMillis();

        } else if (fireType.equals(FireType.DOUBLE_FIRE) && canShoot) { // 双发火力
            bulletPositionX = this.positionX + this.width / 5;
            bulletPositionY = this.positionY + 25;

            bulletList.add(new Bullet[] {
                    new Bullet(bulletPositionX, bulletPositionY, fireDirection),
                    new Bullet((bulletPositionX + this.width / 2),
                            bulletPositionY, fireDirection)
            });

            // 双倍火力持续时间已过，变回普通火力
            if (System.currentTimeMillis() - this.gettingDoubleFireTime
                    > Goods.DOUBLE_FIRE_DURATION)
                this.setFireType(FireType.SINGLE_FIRE);

            previousShootingTime = System.currentTimeMillis();
        }
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


    /**
     * 能否再次进行射击
     * @return true 可以射击 | false 无法射击
     */
    private boolean canShoot() {
        return System.currentTimeMillis() - this.previousShootingTime >
                shootingCoolingTime;
    }

    /**
     * 设置火力类型
     * @param fireType 火力类型 FireType.SINGLE_FIRE(单发)
     *                 FireType.DOUBLE_FIRE(双发)
     */
    protected void setFireType(FireType fireType) {
        this.fireType = fireType;
    }

    /**
     * 玩家击毁敌机，增加分数
     * @param score 分数
     */
    public void addScore(int score) { this.score += score; }

    /**
     * 获取玩家当前总分
     * @return 分数
     */
    public int getScore() { return this.score; }

    /**
     * 获取生命数
     * @return 剩余的生命条数
     */
    public int getLife() { return this.life; }

    /**
     * 设置生命数
     * @param life 生命数
     */
    public void setLife(int life) { this.life = life; }

    /**
     * 获取飞行物对象的子弹集合
     * @return 子弹集合
     */
    public List<Bullet[]> getBulletList() { return this.bulletList; }

    /**
     * 本机射出的子弹是否击中目标飞机。为了不使敌机子弹击中敌机，飞机分两派，敌机和玩家飞机
     * @param target 目标飞机
     * @return 是否击中目标飞机的标志
     */
    protected ResultOfHit hitTheTarget(Plane target, Class<? extends Plane> exclude) {
        ResultOfHit resultOfHit = new ResultOfHit();
        bulletList.forEach(bullets ->
                Arrays.stream(bullets).forEach(bullet -> {
                    // 如果目标飞机不是不是指定类型的飞机才判断是否击中
                    if (!target.getClass().isAssignableFrom(exclude)
                            && bullet.getRectangle().intersects(
                            target.getRectangle())) {
                        resultOfHit.setHit(true);      // 击中目标
                        bullet.setBroken(true);        // 子弹消亡
                    }
                } ));
        return resultOfHit;
    }

}
