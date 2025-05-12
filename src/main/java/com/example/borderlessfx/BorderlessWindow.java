package com.example.borderlessfx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Composant personnalisé représentant une fenêtre sans bordure avec
 * contrôle d'opacité et bouton de fermeture.
 */
public class BorderlessWindow extends StackPane {
    /**
     * Constructeur par défaut.
     * Configure l'interface utilisateur de la fenêtre sans bordure.
     */
    public BorderlessWindow(VBox content) {
        setupUI(content);
    }

    /**
     * Initialise les composants de l'interface utilisateur.
     */
    private void setupUI(VBox content) {
        // Configuration de la forme et du style de fond
        Rectangle background = new Rectangle();
        background.widthProperty().bind(this.widthProperty());
        background.heightProperty().bind(this.heightProperty());
        background.setArcWidth(15);
        background.setArcHeight(15);
        background.setFill(Color.LIGHTBLUE);
        background.setStroke(Color.LIGHTGRAY);
        background.setStrokeWidth(1);

        // Barre de titre avec bouton de fermeture
        HBox titleBar = createTitleBar();

        // Zone de contenu principale
        VBox contentBox = createContentArea(content);

        // Disposition globale
        VBox mainLayout = new VBox(5);
        mainLayout.setPadding(new Insets(5));
        mainLayout.getChildren().addAll(titleBar, contentBox);

        // Ajout des éléments au conteneur racine
        this.getChildren().addAll(background, mainLayout);

        // Style du conteneur
        this.setPadding(new Insets(0));
    }

    /**
     * Crée la barre de titre avec le bouton de fermeture.
     *
     * @return HBox contenant les éléments de la barre de titre
     */
    private HBox createTitleBar() {
        HBox titleBar = new HBox(10);
        titleBar.setPadding(new Insets(5, 5, 5, 10));
        titleBar.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Fenêtre Sans Bordure");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Button closeButton = new Button("×");
        closeButton.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold; -fx-background-color: transparent; " +
                "-fx-text-fill: #333; -fx-padding: 0 5 0 5;");
        closeButton.setOnAction(event -> closeWindow());

        // Spacer pour pousser le bouton de fermeture à droite
        HBox spacer = new HBox();
        spacer.setMinWidth(10);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        titleBar.getChildren().addAll(titleLabel, spacer, closeButton);
        return titleBar;
    }

    /**
     * Crée la zone de contenu principale avec le contrôle d'opacité.
     *
     * @return VBox contenant les éléments de la zone de contenu
     */
    private VBox createContentArea(VBox content) {
        VBox mainContent = new VBox();
        mainContent.getChildren().add(content);

        Label opacityLabel = new Label("Opacité:");
        Slider opacitySlider = new Slider(0.1, 1.0, 1.0);
        opacitySlider.setShowTickLabels(true);
        opacitySlider.setShowTickMarks(true);
        opacitySlider.setMajorTickUnit(0.1);
        opacitySlider.setBlockIncrement(0.1);
        opacitySlider.valueProperty().addListener((obs, oldVal, newVal) -> updateOpacity(newVal.doubleValue()));

        HBox opacityControl = new HBox(10);
        opacityControl.setAlignment(Pos.CENTER);
        opacityControl.getChildren().addAll(opacityLabel, opacitySlider);

        mainContent.getChildren().add(opacityControl);
        return mainContent;
    }


    /**
     * Met à jour l'opacité de la fenêtre principale.
     *
     * @param opacity Valeur d'opacité entre 0.0 et 1.0
     */
    private void updateOpacity(double opacity) {
        Stage stage = (Stage) this.getScene().getWindow();
        if (stage != null) {
            stage.setOpacity(opacity);
        }
    }

    /**
     * Ferme la fenêtre.
     */
    private void closeWindow() {
        Stage stage = (Stage) this.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
} 