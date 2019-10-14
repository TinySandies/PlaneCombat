package com.tinysand.application.bean.standard;

import com.tinysand.application.bean.support.Direction;

/**
 * 机体实体行为接口，所有的机体类都应该实现此接口
 */
public interface Behavior {
    void fire(Direction fireDirection);
}
