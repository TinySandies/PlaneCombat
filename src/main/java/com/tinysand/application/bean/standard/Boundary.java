package com.tinysand.application.bean.standard;

/**
 * 物体越界接口规范，除玩家机体之外的物体须实现此接口
 */
public interface Boundary {
    boolean outOfBounds();
}
