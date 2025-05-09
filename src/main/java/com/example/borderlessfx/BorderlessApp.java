package com.example.borderlessfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Application JavaFX principale qui implémente une fenêtre sans bordure, 
 * avec opacité configurable, toujours au premier plan et déplaçable.
 */
public class BorderlessApp extends Application {

    private double xOffset = 0;
    private double yOffset = 0;
    
    /**
     * Point d'entrée principal de l'application.
     * 
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Création du contenu principal
        BorderlessWindow mainWindow = new BorderlessWindow();
        
        // Configuration de la scène
        Scene scene = new Scene(mainWindow, 400, 300);
        scene.setFill(Color.TRANSPARENT);
        
        // Configuration du stage (fenêtre)
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("BorderlessFX");
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setOpacity(1.0); // Opacité par défaut à 100%
        
        // Ajout des gestionnaires d'événements pour permettre le déplacement de la fenêtre
        setupWindowDragging(mainWindow, primaryStage);
        
        // Affichage de la fenêtre
        primaryStage.show();
    }
    
    /**
     * Configure les événements souris pour permettre de déplacer la fenêtre.
     * 
     * @param root Conteneur racine de l'application
     * @param stage Fenêtre à rendre déplaçable
     */
    private void setupWindowDragging(StackPane root, Stage stage) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
} 