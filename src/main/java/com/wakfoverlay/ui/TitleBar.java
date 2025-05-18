package com.wakfoverlay.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import static javafx.scene.layout.Priority.ALWAYS;

public class TitleBar extends HBox {

    public TitleBar(Runnable onOpenFile, Runnable onReset, Runnable onClose, Runnable onShowDptView, Runnable onShowHealView, Runnable onShowShieldView) {
        this.setPadding(new Insets(5));
        this.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Wakfu Meter");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, ALWAYS);

        HBox navigationMenu = createCustomMenu(onShowDptView, onShowHealView, onShowShieldView);
        HBox.setMargin(navigationMenu, new Insets(0, 0, 0, 15));

        Button folderButton = createIconButton("📁", "#2ecc71");
        folderButton.setTooltip(new javafx.scene.control.Tooltip("Ouvrir un fichier"));
        folderButton.setOnAction(event -> {
            onOpenFile.run();
        });

        Button resetButton = createIconButton("🔄", "#3498db");
        resetButton.setTooltip(new javafx.scene.control.Tooltip("Réinitialiser les statistiques"));
        resetButton.setOnAction(event -> onReset.run());

        Button closeButton = createIconButton("✖", "#e74c3c");
        closeButton.setTooltip(new javafx.scene.control.Tooltip("Fermer"));
        closeButton.setOnAction(event -> onClose.run());

        this.getChildren().addAll(titleLabel, navigationMenu, spacer, folderButton, resetButton, closeButton);
    }

    private HBox createCustomMenu(Runnable OnShowDptView, Runnable onShowHealView, Runnable onShowShieldView) {
        HBox menuContainer = new HBox();
        menuContainer.setAlignment(Pos.CENTER_LEFT);
        menuContainer.setStyle("-fx-cursor: hand;");
        menuContainer.setSpacing(0);

        Label menuLabel = new Label("Statistique");
        menuLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 10;"
        );

        MenuButton menuButton = new MenuButton("⋮");
        menuButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 0 0 0 -20; " +
                        "-fx-min-width: 15px; " +
                        "-fx-min-height: 20px; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"
        );

        String menuItemStyle = "-fx-padding: 0 0; -fx-font-size: 11px; -fx-cursor: hand;";

        MenuItem mainViewItem = new MenuItem("DPT");
        mainViewItem.setStyle(menuItemStyle);
        mainViewItem.setOnAction(e -> OnShowDptView.run());

        MenuItem secondViewItem = new MenuItem("HEAL");
        secondViewItem.setStyle(menuItemStyle);
        secondViewItem.setOnAction(e -> onShowHealView.run());

        MenuItem thirdViewItem = new MenuItem("SHIELD");
        thirdViewItem.setStyle(menuItemStyle);
        thirdViewItem.setOnAction(e -> onShowShieldView.run());

        menuButton.getItems().addAll(mainViewItem, secondViewItem, thirdViewItem);

        menuContainer.getChildren().addAll(menuLabel, menuButton);

        return menuContainer;
    }

    private Button createIconButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: " + color + "; " +
                        "-fx-font-size: 15px; " +
                        "-fx-padding: 2 5; " +
                        "-fx-min-width: 20px; " +
                        "-fx-min-height: 20px; " +
                        "-fx-cursor: hand;"
        );
        return button;
    }
}
