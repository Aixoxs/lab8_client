package ru.ifmo.se.clientUI.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.ifmo.se.musicians.MusicBand;
import java.util.ArrayList;
import java.util.Comparator;

public class ResizeCoordCanvas extends ResizeCanvas{
    private static final int SCREEN_START_MARGIN_ERROR_X = 10;
    private static final int SCREEN_START_MARGIN_ERROR_Y = 90;
    private ArrayList<MusicBand> musicBands;
    private int lastID = -1;
    private int actualUserID;
    private double scale = 0.0D;

    public ResizeCoordCanvas(ArrayList<MusicBand> musicBands, int actualUserID) {
        this.musicBands = musicBands;
        this.actualUserID = actualUserID;
    }

    public void draw() {
        double width = this.getWidth();
        double height = this.getHeight();
        double min1 = Math.min(width, height);
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0.0D, 0.0D, width, height);
        double maxx = this.musicBands.stream().mapToDouble((d) ->
                (double)d.getCoordinates().getX()).max().orElse(this.getWidth());
        double minx = this.musicBands.stream().mapToDouble((d) ->
            (double)d.getCoordinates().getX()).min().orElse(this.getHeight());
        double maxy = this.musicBands.stream().mapToDouble((d) ->
                (double)d.getCoordinates().getY()).max().orElse(0.0D);
        double miny = this.musicBands.stream().mapToDouble((d) ->
            (double)d.getCoordinates().getY()).min().orElse(0.0D);
        this.scale = 2.0D * Math.max(maxx, Math.max(-Math.min(minx, miny), maxy));
        double min = min1 - 0.1*scale;
        scale += 0.1*scale;
        gc.setFill(Color.DARKGREY);
        gc.fillRect(0.0D, 0.0D, width, height);
        gc.setFill(Color.BLACK);
        gc.strokeLine(0.0D, min / 2.0D, min, min / 2.0D);
        gc.strokeLine(min / 2.0D, 0.0D, min / 2.0D, min);
        gc.fillText("0.0", min / 2.0D, min / 2.0D + 20.0D);
        gc.fillText(String.valueOf((int)(-this.scale / 4.0D)), min / 4.0D, min / 2.0D + 20.0D);
        gc.fillText(String.valueOf((int)(this.scale / 4.0D)), min * 3.0D / 4.0D, min / 2.0D + 20.0D);
        this.musicBands.stream().sorted(Comparator.comparingInt(MusicBand::getUserId)).
                forEach((d) -> this.drawBands(gc, d, min));
    }

    private void drawBands(GraphicsContext gc, MusicBand musicBand, double width) {
        if (this.lastID != musicBand.getUserId()) {
            if (actualUserID == musicBand.getUserId()) {
                gc.setFill(Color.GREEN);
            }else gc.setFill(Color.RED);
        }

        if (this.actualUserID == musicBand.getUserId()) {
            gc.fillOval(((double) musicBand.getCoordinates().getX() +
                    this.scale / 2.0D) * (width / this.scale),
                    (this.scale / 2.0D - (double)musicBand.getCoordinates().getY()) *
                            (width / this.scale), 11.0D, 11.0D);
            gc.strokeOval(((double)musicBand.getCoordinates().getX() +
                    this.scale / 2.0D) * (width / this.scale),
                    (this.scale / 2.0D - (double)musicBand.getCoordinates().getY()) *
                            (width / this.scale), 11.0D, 11.0D);
            gc.strokeOval(((double)musicBand.getCoordinates().getX() +
                    this.scale / 2.0D) * (width / this.scale) + 4.25D,
                    (this.scale / 2.0D - (double)musicBand.getCoordinates().getY()) *
                            (width / this.scale) + 4.25D, 2.0D, 2.0D);
        } else {
            gc.strokeOval(((double)musicBand.getCoordinates().getX() +
                    this.scale / 2.0D) * (width / this.scale),
                    (this.scale / 2.0D - (double)musicBand.getCoordinates().getY()) *
                            (width / this.scale), 8.0D, 8.0D);
            gc.fillOval(((double)musicBand.getCoordinates().getX() +
                    this.scale / 2.0D) * (width / this.scale),
                    (this.scale / 2.0D - (double)musicBand.getCoordinates().getY()) *
                            (width / this.scale), 8.0D, 8.0D);
        }

        this.lastID = musicBand.getUserId();
    }

    public Object findObj(double coordX, double coordY) throws NullPointerException {
        double min = Math.min(this.getWidth(), this.getHeight());
        double finalCoordX = (coordX - 10.0D) * (this.scale / min) - this.scale / 2.0D;
        double finalCoordY = this.scale / 2.0D - (coordY - 90.0D) * (this.scale / min);
        return this.musicBands.stream().filter((musicBand) -> {
            return Math.abs((double)musicBand.getCoordinates().getX() - finalCoordX) < this.scale * 0.018D;
        }).filter((musicBand) -> {
            return Math.abs((double)musicBand.getCoordinates().getY() - finalCoordY) < this.scale * 0.018D;
        }).findAny().orElse(null);
    }

    public void setObj(Object obj) {
        this.musicBands = (ArrayList<MusicBand>)obj;
    }

    public Object getObj() {
        return this.musicBands;
    }
}
