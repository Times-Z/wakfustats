// BorderlessAppTest.java
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@ExtendWith(ApplicationExtension.class)
public class BorderlessAppTest {

    private Stage stage;
    private BorderlessApp app;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        this.app = new BorderlessApp();

        try {
            app.start(stage);
        } catch (Exception e) {
            fail("Exception lors du démarrage de l'application: " + e.getMessage());
        }
    }

    @Test
    public void testWindowInitialProperties() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Vérification que la fenêtre est initialisée avec StageStyle.TRANSPARENT
                assertEquals(StageStyle.TRANSPARENT, stage.getStyle(),
                        "La fenêtre devrait être en style TRANSPARENT");

                // Vérification de la taille de fenêtre
                assertEquals(400, stage.getScene().getWidth(),
                        "La largeur de la fenêtre devrait être 400");
                assertEquals(300, stage.getScene().getHeight(),
                        "La hauteur de la fenêtre devrait être 300");

                // Vérification de l'opacité initiale
                assertEquals(1.0, stage.getOpacity(), 0.01,
                        "L'opacité initiale devrait être 1 (100%)");

                // Vérification que la fenêtre est toujours au premier plan
                assertTrue(stage.isAlwaysOnTop(),
                        "La fenêtre devrait être configurée comme toujours au premier plan");

                // Vérification du titre
                assertEquals("BorderlessFX", stage.getTitle(),
                        "Le titre de la fenêtre devrait être 'BorderlessFX'");
            } finally {
                latch.countDown();
            }
        });

        // Attendre que les tests UI se terminent avec un timeout de 5 secondes
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testWindowDraggability() throws Exception {
        // Ce test vérifie que les gestionnaires d'événements pour le déplacement sont bien configurés
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Scene scene = stage.getScene();
                assertNotNull(scene.getRoot().getOnMousePressed(),
                        "Le gestionnaire d'événement MousePressed doit être configuré");
                assertNotNull(scene.getRoot().getOnMouseDragged(),
                        "Le gestionnaire d'événement MouseDragged doit être configuré");
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
