package com.wakfoverlay.ui;

import com.wakfoverlay.domain.fight.model.Character;
import com.wakfoverlay.domain.fight.model.Characters;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

import static javafx.scene.layout.Priority.ALWAYS;

public class CharactersShieldsView extends VBox {
    private static final int NAME_MAX_LENGTH = 40;
    private static final double NAME_COLUMN_WIDTH = 120;

    public CharactersShieldsView(Characters charactersData) {
        this.setSpacing(2);

        List<Character> characters = charactersData.characters();
        int totalShields = calculateTotalShields(characters);

        for (Character character : characters) {
            HBox characterRow = createCharacterRow(character, totalShields);
            this.getChildren().add(characterRow);
        }
    }

    private int calculateTotalShields(List<Character> characters) {
        return characters.stream().mapToInt(Character::shields).sum();
    }

    private HBox createCharacterRow(Character character, int totalShields) {
        HBox characterBox = new HBox();
        characterBox.setPadding(new Insets(2, 5, 2, 5));
        characterBox.setSpacing(10);

        Label nameLabel = createNameLabel(character);
        HBox nameContainer = new HBox(nameLabel);
        nameContainer.setPrefWidth(NAME_COLUMN_WIDTH);
        nameContainer.setMinWidth(NAME_COLUMN_WIDTH);

        Pane shieldBar = createShieldBar(character, totalShields);

        Label shieldLabel = createShieldLabel(character, totalShields);

        characterBox.getChildren().addAll(nameContainer, shieldBar, shieldLabel);
        return characterBox;
    }

    private Label createNameLabel(Character character) {
        String displayName = character.name().value();
        String fullName = character.name().value();

        if (displayName.length() > NAME_MAX_LENGTH) {
            displayName = displayName.substring(0, NAME_MAX_LENGTH - 3) + "...";
        }

        Label label = new Label(displayName);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        label.setUserData(fullName);

        label.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(() -> {
            if (label.getScene() != null && label.getScene().getWindow() != null &&
                    label.getScene().getWindow().getWidth() > 500) {
                return fullName;
            } else {
                if (fullName.length() > NAME_MAX_LENGTH) {
                    return fullName.substring(0, NAME_MAX_LENGTH - 3) + "...";
                }
                return fullName;
            }
        }, label.sceneProperty(), label.widthProperty()));

        return label;
    }

    private Pane createShieldBar(Character character, int totalShields) {
        Pane pane = new Pane();
        pane.setPrefSize(100, 20);
        HBox.setHgrow(pane, ALWAYS);

        Rectangle border = new Rectangle(0, 0, 100, 20);
        border.setStroke(Color.GRAY);
        border.setFill(Color.TRANSPARENT);

        double shieldPercentage = totalShields == 0 ? 0 : (character.shields() / (double) totalShields) * 100;
        Rectangle progressBar = new Rectangle(0, 0, shieldPercentage, 20);

        String color = CharacterColorManager.getColorForCharacter(character.name().value());
        progressBar.setFill(Color.web(color));

        pane.getChildren().addAll(border, progressBar);
        return pane;
    }

    private Label createShieldLabel(Character character, int totalShields) {
        double percentage = totalShields == 0 ? 0 : ((double) character.shields() / totalShields) * 100;
        String text = character.shields() + " (" + String.format("%.2f", percentage) + "%)";

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        return label;
    }
}
