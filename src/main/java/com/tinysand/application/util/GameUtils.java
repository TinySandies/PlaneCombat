package com.tinysand.application.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * 该类是游戏中的工具类，提供程序中常用的方法或必要公共常量
 */
public final class GameUtils {

    private static final String IMAGE_PREFIX = "/images/";  // 图片路径前缀

    private static final String IMAGE_SUFFIX = ".png";      // 图片后缀

    /**
     * 获取实体图片资源的工具方法
     * @param resourcePath 图片资源路径
     * @return 读取到的图片资源
     */
    public static BufferedImage getImage(String resourcePath) {
        return getImage(classLoader ->
                classLoader.getResourceAsStream(resourcePath));
    }

    private static BufferedImage getImage
            (Function<Class, InputStream> processor) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(processor.apply(GameUtils.class));
        } catch (IOException e) {
            System.out.println("读取图片失败了");
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 根据图片名称获取图片的完整路径
     * @param name 图片名称
     * @return 完整的图片路径
     */
    public static String obtainImagePath(final String name) {
        return IMAGE_PREFIX + name + IMAGE_SUFFIX;
    }

    /**
     * 线程休眠方法
     * @param millis 毫秒
     */
    public static void interrupted(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
