package com.tinysand.application;

import com.tinysand.application.core.GameFrame;

import java.awt.*;

public class Launcher {
    public static void main(String[] args) {
        EventQueue.invokeLater(() ->
                new GameFrame("Plane combat"));
    }
}
