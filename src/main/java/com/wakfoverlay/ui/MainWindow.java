package com.wakfoverlay.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static javafx.scene.layout.Priority.ALWAYS;

public class MainWindow extends StackPane {

    private static final Color BACKGROUND_COLOR = Color.LIGHTBLUE;
    private static final Color STROKE_COLOR = Color.LIGHTGRAY;
    private static final String TITLE = "Welcome to Wakfu DPS Meter";

    public MainWindow(VBox content) {
        setupUI(content);
    }

    private void setupUI(VBox content) {
        Rectangle background = new Rectangle();
        background.widthProperty().bind(this.widthProperty());
        background.heightProperty().bind(this.heightProperty());
        background.setFill(BACKGROUND_COLOR);
        background.setStroke(STROKE_COLOR);

        // TODO: check this and other ui options
        // Window corners
        background.setArcWidth(15);
        background.setArcHeight(15);

        // Window internal border
        background.setStrokeWidth(1);

        HBox titleBar = createTitleBar();
        VBox contentBox = createContentArea(content);

        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().addAll(titleBar, contentBox);

        this.getChildren().addAll(background, mainLayout);
    }

    private HBox createTitleBar() {
        HBox titleBar = new HBox();

        Label titleLabel = new Label(TITLE);
        // TODO: change style
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Button closeButton = new Button("×");
        // TODO: change style
        closeButton.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold; -fx-background-color: transparent; " +
                "-fx-text-fill: #333; -fx-padding: 0 5 0 5;");
        closeButton.setOnAction(event -> closeWindow());

        // TODO: change style
        HBox spacer = new HBox();
        spacer.setMinWidth(10);
        HBox.setHgrow(spacer, ALWAYS);

        titleBar.getChildren().addAll(titleLabel, spacer, closeButton);
        return titleBar;
    }

    private VBox createContentArea(VBox content) {
        VBox mainContent = new VBox();
        mainContent.getChildren().add(content);

        Slider opacitySlider = new Slider(0.2, 1.0, 1.0);
        opacitySlider.setShowTickLabels(true);
        opacitySlider.valueProperty()
                .addListener((_, _, newOpacity) ->
                        updateOpacity(newOpacity.doubleValue())
                );
        opacitySlider.setPadding(new Insets(50, 0, 0, 0));

        HBox opacityControl = new HBox();
        opacityControl.getChildren().addAll(opacitySlider);

        mainContent.getChildren().add(opacityControl);
        return mainContent;
    }

    private void updateOpacity(double opacity) {
        Stage stage = (Stage) this.getScene().getWindow();
        if (stage != null) {
            stage.setOpacity(opacity);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) this.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
} 