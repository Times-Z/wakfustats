// BorderlessWindowTest.java
package com.example.borderlessfx;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class BorderlessWindowTest {

    private BorderlessWindow window;
    private Stage stage;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        this.window = new BorderlessWindow(new VBox());
        Scene scene = new Scene(window, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testWindowTitle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Accéder à la barre de titre (premier élément VBox)
                VBox mainLayout = (VBox) window.getChildren().get(1);
                HBox titleBar = (HBox) mainLayout.getChildren().get(0);

                // Vérifier le titre (premier élément de la titleBar)
                Label titleLabel = (Label) titleBar.getChildren().get(0);
                assertEquals("Fenêtre Sans Bordure", titleLabel.getText(),
                        "Le titre devrait être 'Fenêtre Sans Bordure'");
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testOpacitySlider() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Accéder à la zone de contenu
                VBox mainLayout = (VBox) window.getChildren().get(1);
                VBox contentBox = (VBox) mainLayout.getChildren().get(1);

                // Accéder au contrôle d'opacité
                HBox opacityControl = (HBox) contentBox.getChildren().get(1);
                Label opacityLabel = (Label) opacityControl.getChildren().get(0);
                Slider opacitySlider = (Slider) opacityControl.getChildren().get(1);

                // Vérifier le libellé
                assertEquals("Opacité:", opacityLabel.getText(),
                        "Le libellé devrait être 'Opacité:'");

                // Vérifier les propriétés du slider
                assertEquals(0.1, opacitySlider.getMin(), 0.01,
                        "La valeur minimale du slider devrait être 0.1");
                assertEquals(1.0, opacitySlider.getMax(), 0.01,
                        "La valeur maximale du slider devrait être 1.0");
                assertEquals(0.9, opacitySlider.getValue(), 0.01,
                        "La valeur par défaut du slider devrait être 0.9");

                // Tester la fonctionnalité du slider
                opacitySlider.setValue(0.5);
                assertEquals(0.5, stage.getOpacity(), 0.01,
                        "Lorsque le slider est réglé à 0.5, l'opacité de la fenêtre devrait être 0.5");
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testCloseButton() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Accéder au bouton de fermeture
                VBox mainLayout = (VBox) window.getChildren().get(1);
                HBox titleBar = (HBox) mainLayout.getChildren().get(0);
                Button closeButton = (Button) titleBar.getChildren().get(2);

                // Vérifier le texte du bouton
                assertEquals("×", closeButton.getText(),
                        "Le texte du bouton de fermeture devrait être '×'");

                // Vérifier que le bouton a un gestionnaire d'événement
                assertNotNull(closeButton.getOnAction(),
                        "Le bouton de fermeture devrait avoir un gestionnaire d'événement");

                // On ne teste pas la fonction de fermeture réelle car elle fermerait le test
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
