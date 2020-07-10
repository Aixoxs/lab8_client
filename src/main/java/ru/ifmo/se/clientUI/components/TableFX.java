package ru.ifmo.se.clientUI.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class TableFX extends StackPane{
    private final Label titleLabel = new Label();
    private final StackPane contentPane = new StackPane();
    private Node content;

    public void setContent(Node content) {
        content.getStyleClass().add("bordered-titled-content");
        this.contentPane.getChildren().add(content);
    }

    public Node getContent() {
        return this.content;
    }

    public void setTitle(String title) {
        this.titleLabel.setText(" " + title + " ");
    }

    public String getTitle() {
        return this.titleLabel.getText();
    }

    public TableFX() {
        this.titleLabel.setText("default title");
        this.titleLabel.getStyleClass().add("bordered-titled-title");
        StackPane.setAlignment(this.titleLabel, Pos.TOP_LEFT);
        this.getStyleClass().add("bordered-titled-border");
        this.getChildren().addAll(new Node[]{this.titleLabel, this.contentPane});
    }
}
