package ru.ifmo.se.clientUI.canvas;

import javafx.scene.canvas.Canvas;

public abstract class ResizeCanvas extends Canvas {
    public ResizeCanvas() {
        this.widthProperty().addListener((evt) -> {
            this.draw();
        });
        this.heightProperty().addListener((evt) -> {
            this.draw();
        });
    }

    public abstract void draw();

    public abstract Object findObj(double var1, double var3);

    public abstract void setObj(Object var1);

    public abstract Object getObj();

    public boolean isResizable() {
        return true;
    }

    public double prefWidth(double height) {
        return this.getWidth();
    }

    public double prefHeight(double width) {
        return this.getHeight();
    }
}
