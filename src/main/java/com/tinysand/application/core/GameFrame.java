package com.tinysand.application.core;

import com.tinysand.application.bean.realize.Bullet;
import com.tinysand.application.bean.realize.Enemy;
import com.tinysand.application.bean.realize.Goods;
import com.tinysand.application.bean.realize.Player;
import com.tinysand.application.bean.standard.Direction;
import com.tinysand.application.bean.standard.Plane;
import com.tinysand.application.util.Constant;
import com.tinysand.application.util.GameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static com.tinysand.application.util.Constant.*;

public class GameFrame extends JFrame {

    private static List<Plane> planeList = new CopyOnWriteArrayList<>(); // 飞机容器

    private static List<Goods> goodsList = new CopyOnWriteArrayList<>(); // 奖励物品

    private boolean gamePause = true, gameOver;

    public GameFrame(final String frameTitle) {
        super(frameTitle);
        initializeGameApplication(planeList1 -> {
            planeList1.clear();

            // 实例化玩家数据
            planeList.add(new Player(Constant.DEFAULT_FRAME_WIDTH / 2 - 100,
                    DEFAULT_FRAME_HEIGHT - 120 , 5, 3));

            // 实例化敌人数据
            for (int num = 0; num < DEFAULT_ENEMY_SIZE; num ++)
                planeList.add(obtainEnemyWithRandomPosition());
        });
        timerTaskExecutor();
    }

    /**
     * 逻辑任务调度方法
     */
    private void timerTaskExecutor() {
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                if (!gamePause && !gameOver) {
                    planeList.forEach(plane -> {
                        if (plane.getClass().isAssignableFrom(Enemy.class)) {
                            plane.move();                                // 敌机移动
                            plane.fire(Direction.DOWN);    // 敌机开火
                        } else if (plane.getClass().isAssignableFrom(Player.class)) {
                            plane.fire(Direction.UP);      // 玩家飞机开火
                        }

                        moveBullets(plane.getBulletList());   // 移动子弹
                    });
                    generatedRandomGoods(goodsList);          // 随机生成奖励物品

                    goodsList.forEach(Goods::move);           // 移动奖励物品

                    intersecting();                           // 碰撞检测
                    clearBrokenEntity();                      // 清除损坏的物体

                }
                repaint();
            }
        }, TIMER_TASK_DELAY, TIMER_TASK_PERIOD);
    }

    /**
     * 生成随机奖励物品
     * @param goodsList 存放奖励的集合
     */
    private void generatedRandomGoods(List<Goods> goodsList) {
        Random random = new Random();
        if (goodsList.isEmpty())
            if (random.nextInt(200) == 1)
                goodsList.add(new Goods(random.nextInt(DEFAULT_FRAME_WIDTH - 50),
                        random.nextInt(30)));
    }

    /**
     * 清除所有消亡的物体，确保在下次图像绘制时不会出现
     */
    private void clearBrokenEntity() {
        for (int i = 0; i < planeList.size(); i ++) {
            Plane plane = planeList.get(i);

            if (plane.isBroken()) {               // 飞机损坏
                plane.getBulletList().clear();    // 清除即将移除的飞机里的子弹
                planeList.remove(plane);          // 从飞机列表中移除被击毁的飞机

                // 如果消亡的不是玩家的飞机，那就再向飞机列表中添加一架飞机
                if (plane.getClass().isAssignableFrom(Enemy.class))
                    planeList.add(obtainEnemyWithRandomPosition());
            } else if (!plane.isBroken()) {       // 飞机还未消亡

                //清除飞机里越界的子弹
                for (int j = plane.getBulletList().size() - 1; j > 0; j --)
                    if (plane.getBulletList().get(j)[0].isBroken())
                        plane.getBulletList().remove(j);
            }
        }

        goodsList.forEach(goods -> {              // 将消亡的奖励物品移除
            if (goods.isBroken())
                goodsList.remove(goods);
        });
    }

    /**
     * 碰撞检测
     */
    private void intersecting() {
        planeList.forEach(plane -> {
            // 当前飞机是玩家飞机
            if (plane.getClass().isAssignableFrom(Player.class)) {
                ((Player) plane).eatingGoods(goodsList); // 检测玩家飞机与奖励物品的碰撞

                planeList.forEach(comparablePlane -> {
                    if (comparablePlane != plane)    // 当前遍历出的飞机不是玩家飞机
                        if (((Enemy) comparablePlane).hitTheTarget((Player) plane)) {

                            if (plane.getLife() > 1) {
                                plane.setLife(plane.getLife() - 1);
                            } else {
                                plane.setBroken(true);
                                plane.setLife(0);
                                gameOver = true;
                                System.out.println("玩家飞机被击毁");
                            }
                        }
                });
            }
            // 当前飞机是敌机
            else if (plane.getClass().isAssignableFrom(Enemy.class)) {
                planeList.forEach(comparablePlane -> {
                    // 找到玩家的飞机
                    if (comparablePlane.getClass().isAssignableFrom(Player.class)
                            && comparablePlane != plane)
                        if (((Player) comparablePlane).hitTheTarget((Enemy) plane)) {
                            plane.setBroken(true);
                            comparablePlane.addScore(plane.getScore());  // 给玩家加分
                        }
                });
            }
        });
    }

    /**
     * 遍历飞机中的所有子弹，让子弹移动起来
     * @param bulletList 子弹列表
     */
    private void moveBullets(List<Bullet[]> bulletList) {
        for (int i = 0; i < bulletList.size(); i ++) {
            Bullet[] bullets = bulletList.get(i);
            if (bullets[0].isBroken())
                bulletList.remove(bullets);
            else
                Arrays.stream(bullets).forEach(Bullet::move);
        }
    }

    /**
     * 游戏程序初始化方法
     */
    private void initializeGameApplication(Consumer<List<Plane>>
                                                   planeListConsumer) {
        this.setLocationByPlatform(false);  // 设置窗体的位置
//        this.setLocation(0, 0);
        this.setResizable(false);           // 设置窗体不可改变大小
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(new GamePanel());          // 添加画图面板

        this.pack();                        // 让框架自动适配大小
        this.setVisible(true);
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                planeList.forEach(plane -> {
                    if (plane.getClass().isAssignableFrom(Player.class)
                            && !gamePause && !gameOver) {
                        Point locationOnScreen = e.getPoint();

                        ((Player) plane).moveTo((int) locationOnScreen.getX(),
                                (int) locationOnScreen.getY());
                    }
                });
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (KeyEvent.VK_SPACE == (e.getKeyChar()) && !gameOver) {  // 暂停游戏
                    gamePause = !gamePause;
                }

                if ('r' == e.getKeyChar() && gameOver) {                   // 重新开始游戏
                    // 实例化玩家数据
                    planeList.add(new Player(Constant.DEFAULT_FRAME_WIDTH / 2 - 100,
                            DEFAULT_FRAME_HEIGHT - 120 , 5, 3));

                    gamePause = gameOver = false;
                }
            }
        });

        planeListConsumer.accept(planeList); // 填充游戏数据
    }

    /**
     * 构建一个随机位置的敌机
     * @return 敌机
     */
    private Enemy obtainEnemyWithRandomPosition() {
        Random random = new Random();
        int randomPositionX = random.nextInt(DEFAULT_FRAME_WIDTH) %
                (DEFAULT_FRAME_WIDTH-50+1);

        int randomPositionY = random.nextInt(65);

        return new Enemy(randomPositionX, randomPositionY);
    }

    private class GamePanel extends JPanel {

        GamePanel() { super(true); }

        /**
         * 具体绘制方法
         * @param graphics 画笔
         */
        @Override public void paint(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics;

            graphics2D.addRenderingHints(new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON));

            // 绘制背景
            graphics2D.drawImage(GameUtils.getImage(GameUtils.obtainImagePath
                    ("background")), 0, 0, null);

            if (gamePause) {
                BufferedImage pauseImageSource = GameUtils.getImage(GameUtils
                        .obtainImagePath("pause"));

                Point point = onScreenCenter(pauseImageSource);
                graphics2D.drawImage(pauseImageSource,
                        (int) point.getX(), (int) point.getY(), null);
            }

            if (gameOver) {
                BufferedImage gameOverImage = GameUtils.getImage(GameUtils
                        .obtainImagePath("gameover"));

                Point point = onScreenCenter(gameOverImage);
                graphics2D.drawImage(gameOverImage,
                        (int) point.getX(), (int) point.getY(), null);
            }

            // 绘制奖励物品
            goodsList.forEach(goods -> graphics2D.drawImage(goods.getAppearanceImage(),
                    goods.getPositionX(), goods.getPositionY(), null));

            // 绘制所有飞机
            planeList.forEach(plane -> {
                graphics2D.drawImage(plane.getAppearanceImage(),
                            plane.getPositionX(), plane.getPositionY(), null);

                    plane.getBulletList().forEach(bulletsArray ->
                            Arrays.stream(bulletsArray).forEach(bullet ->
                                    graphics.drawImage(bullet.getAppearanceImage(),
                                            bullet.getPositionX(),
                                            bullet.getPositionY(), null)));

                    if (plane.getClass().isAssignableFrom(Player.class)) {
                        graphics2D.setFont(new Font("楷体", Font.BOLD, 16));

                        graphics2D.drawString(
                                String.format("当前分数:【 %s 】 还剩【 %s 】条命",
                                        plane.getScore(), plane.getLife()),

                                DEFAULT_FRAME_WIDTH / 5, 35);
                    }
                });

        }

        /**
         * 计算图片在面板中心的位置
         * @param bufferedImage 要计算的图片
         * @return 图片位置
         */
        private Point onScreenCenter(BufferedImage bufferedImage) {
            return new Point((DEFAULT_FRAME_WIDTH - bufferedImage.getWidth()) / 2,
                    (DEFAULT_FRAME_HEIGHT - bufferedImage.getHeight()) / 2);
        }

        /**
         * 返回窗体的宽度和高度数据，该方法将会由框架自行调用，因为使用了pack()
         * @return 包装成 Dimension 类型的窗体宽高
         */
        @Override public Dimension getPreferredSize() {
            return new Dimension(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
        }
    }

}
