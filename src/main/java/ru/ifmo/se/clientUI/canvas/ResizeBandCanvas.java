package ru.ifmo.se.clientUI.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.ifmo.se.musicians.MusicBand;

public class ResizeBandCanvas extends ResizeCanvas{
    private MusicBand musicBand = null;

    public ResizeBandCanvas() {
    }

    public Object findObj(double coordX, double coordY) {
        return null;
    }

    public void setObj(Object obj) {
        this.musicBand = (MusicBand) obj;
    }

    public Object getObj() {
        return this.musicBand;
    }

    public void draw() {
        double width = this.getWidth();
        double height = this.getHeight();
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0.0D, 0.0D, width, height);
        if (this.musicBand != null) {
            this.drawBand(gc);
        }else gc.fillRect(0,0,getWidth(),getHeight());
    }

    private void drawBand(GraphicsContext gc) {
        this.drawParticipants(gc);
        this.drawPerson(gc);
        this.drawStage(gc);
    }

    private void drawParticipants(GraphicsContext gc) {
        gc.setFill(Color.GRAY);
        gc.setStroke(Color.BLACK);
        double nopToDraw = Math.min(this.musicBand.getNumberOfParticipants(), 40);
        double y = getHeight()-45;

        for(int i = 0; (double)i < nopToDraw; ++i) {
            double x = Math.random() * getWidth()-20;
            //musician
            gc.strokeLine(x, y, x + 10, y - 20);
            gc.strokeLine(x + 20, y, x + 10, y - 20);
            gc.strokeLine(x + 10, y - 47, x + 10, y - 20);
            gc.strokeLine(x + 10, y - 37, x + 17, y - 25);
            gc.strokeLine(x + 17, y - 25, x + 26, y - 30);
            gc.strokeLine(x + 10, y - 37, x+3, y - 20);
            gc.strokeLine(x + 15, y - 28, x+33, y - 33);
            gc.strokeOval(x - 4, y - 32, 20, 10);
            gc.fillOval(x - 4, y - 32, 20, 10);
            gc.strokeLine(x+4, y - 25, x+3, y - 20);
            gc.strokeOval(x + 4, y - 60, 12, 12);
            gc.fillOval(x + 4, y - 60, 12, 12);
        }
    }

    private void drawPerson(GraphicsContext gc) {
        double y = getHeight()-45;
        double x = Math.random() * getWidth()-20;

        gc.strokeLine(x+10, y, x + 17, y - 16);
        gc.strokeLine(x + 24, y, x + 17, y - 16);
        gc.strokeLine(x + 17, y - 32, x + 17, y - 16);
        gc.strokeLine(x + 17, y - 24, x + 30, y - 30);
        gc.strokeOval(x + 6, y - 64, 22, 32);
        gc.setFill(Color.color(Math.random(),Math.random(),Math.random()));
        gc.fillOval(x + 6, y - 64, 22, 32);
        gc.strokeLine(x + 20, y - 38, x + 30, y - 30);
        gc.strokeLine(x + 17, y - 24, x , y - 45);
        gc.strokeLine(x + 14, y - 41, x+20, y - 41);
        gc.setFill(Color.valueOf(musicBand.getFrontMan().getHairColor() != null ?
                musicBand.getFrontMan().getHairColor().name() : "Black"));
        gc.fillOval(x + 7, y - 65, 20, 8);
        gc.setFill(Color.valueOf(musicBand.getFrontMan().getEyeColor() != null ?
                musicBand.getFrontMan().getEyeColor().name(): "Black"));
        gc.fillOval(x + 10, y - 52, 5, 4);
        gc.fillOval(x + 19, y - 52, 5, 4);
    }

    private void drawStage(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        double x;
        double y = getHeight();
        gc.strokeLine(0,y-45,getWidth(),y-45);
        gc.strokeLine(getWidth(),y,getWidth(),y-45);
        for (x=1; x<getWidth();x=x+23) {
            gc.strokeLine(x, y, x + 10, y - 20);
            gc.strokeLine(x + 20, y, x + 10, y - 20);
            gc.strokeLine(x + 10, y - 47, x + 10, y - 20);
            gc.strokeLine(x + 10, y - 35, x + 20, y - 45);
            gc.strokeLine(x + 20, y - 63, x + 20, y - 45);
            gc.strokeLine(x + 10, y - 35, x, y - 45);
            gc.strokeLine(x, y - 63, x, y - 45);
            gc.strokeOval(x + 4, y - 60, 12, 12);
            gc.fillOval(x + 4, y - 60, 12, 12);
        }
    }
}
