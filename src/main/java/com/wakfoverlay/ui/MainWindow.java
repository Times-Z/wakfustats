package com.wakfoverlay.ui;

import com.wakfoverlay.domain.fight.model.Characters;
import com.wakfoverlay.domain.fight.port.primary.FetchCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import com.wakfoverlay.domain.logs.model.FileReadStatus;
import com.wakfoverlay.exposition.LogParser;
import com.wakfoverlay.exposition.UserPreferences;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static javafx.scene.layout.Priority.ALWAYS;

public class MainWindow extends VBox {

    private final FetchCharacter fetchCharacter;
    private final UpdateCharacter updateCharacter;
    private final UpdateStatusEffect updateStatusEffect;
    private final LogParser logParser;
    private final UserPreferences userPreferences;

    private final VBox contentContainer;

    private String selectedFilePath;

    private View currentView = View.DPT_VIEW;

    public MainWindow(FetchCharacter fetchCharacter, UpdateCharacter updateCharacter, UpdateStatusEffect updateStatusEffect, LogParser logParser) {
        this.fetchCharacter = fetchCharacter;
        this.updateCharacter = updateCharacter;
        this.updateStatusEffect = updateStatusEffect;
        this.logParser = logParser;
        this.userPreferences = new UserPreferences(MainWindow.class);
        this.selectedFilePath = userPreferences.getFilePath();

        setupWindowAppearance();

        TitleBar titleBar = createTitleBar();
        this.contentContainer = new VBox();
        this.contentContainer.setSpacing(2);
        ScrollPane contentScrollPane = createScrollPane();

        this.getChildren().addAll(titleBar, contentScrollPane);

        showCurrentView();
    }

    public void updateDisplay() {
        showCurrentView();
    }

    public void updateDamagesDisplay() {
        contentContainer.getChildren().clear();

        FileReadStatus status = logParser.readNewLogLines(selectedFilePath);

        if (status != FileReadStatus.SUCCESS) {
            showStatusMessage(getMessageForStatus(status));
            return;
        }

        Characters rankedCharacters = fetchCharacter.rankedCharactersByDamages();

        if (rankedCharacters.characters().isEmpty()) {
            return;
        }

        showCharactersDamages(rankedCharacters);
    }

    public void updateHealsDisplay() {
        contentContainer.getChildren().clear();

        FileReadStatus status = logParser.readNewLogLines(selectedFilePath);

        if (status != FileReadStatus.SUCCESS) {
            showStatusMessage(getMessageForStatus(status));
            return;
        }

        Characters rankedCharacters = fetchCharacter.rankedCharactersByHeals();

        if (rankedCharacters.characters().isEmpty()) {
            return;
        }

        showCharactersHeals(rankedCharacters);
    }

    public void updateShieldsDisplay() {
        contentContainer.getChildren().clear();

        FileReadStatus status = logParser.readNewLogLines(selectedFilePath);

        if (status != FileReadStatus.SUCCESS) {
            showStatusMessage(getMessageForStatus(status));
            return;
        }

        Characters rankedCharacters = fetchCharacter.rankedCharactersByShields();

        if (rankedCharacters.characters().isEmpty()) {
            return;
        }

        showCharactersShields(rankedCharacters);
    }

    private void showDamagesView() {
        currentView = View.DPT_VIEW;
        showCurrentView();
    }

    private void showHealsView() {
        currentView = View.HEAL_VIEW;
        showCurrentView();
    }

    private void showShieldsView() {
        currentView = View.SHIELD_VIEW;
        showCurrentView();
    }

    private void showCurrentView() {
        contentContainer.getChildren().clear();

        switch (currentView) {
            case DPT_VIEW:
                updateDamagesDisplay();
                break;
            case HEAL_VIEW:
                updateHealsDisplay();
                break;
            case SHIELD_VIEW:
                updateShieldsDisplay();
                break;
            default:
                break;
        }
    }

    private void setupWindowAppearance() {
        this.setStyle("-fx-background-color: rgb(18, 18, 18); " +
                "-fx-border-color: rgb(51, 51, 51); " +
                "-fx-border-width: 1;");
        this.setSpacing(2);
        this.setPadding(new Insets(5, 5, 5, 5));
    }

    private ScrollPane createScrollPane() {
        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: rgb(18, 18, 18); -fx-background-color: rgb(18, 18, 18);");

        VBox.setVgrow(scrollPane, ALWAYS);
        return scrollPane;
    }

    private TitleBar createTitleBar() {
        return new TitleBar(
                this::openFileChooser,
                this::resetStats,
                this::closeWindow,
                this::showDamagesView,
                this::showHealsView,
                this::showShieldsView
        );
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");

        configureFileChooserInitialDirectory(fileChooser);
        configureFileChooserFilters(fileChooser);

        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

        if (selectedFile != null) {
            this.selectedFilePath = selectedFile.getAbsolutePath();
            userPreferences.saveFilePath(this.selectedFilePath);
            logParser.resetReadPosition();
        }
    }

    private void configureFileChooserInitialDirectory(FileChooser fileChooser) {
        String lastPath = userPreferences.getFilePath();
        if (lastPath != null) {
            File lastFile = new File(lastPath);
            if (lastFile.exists()) {
                fileChooser.setInitialDirectory(lastFile.getParentFile());
                return;
            }
        }
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    private void configureFileChooserFilters(FileChooser fileChooser) {
        FileChooser.ExtensionFilter logFilter =
                new FileChooser.ExtensionFilter("Fichiers Log (*.log)", "*.log");
        FileChooser.ExtensionFilter textFilter =
                new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt");
        FileChooser.ExtensionFilter allFilter =
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*");

        fileChooser.getExtensionFilters().addAll(logFilter, textFilter, allFilter);
    }

    private void resetStats() {
        updateCharacter.resetCharacters();
        updateStatusEffect.resetStatusEffects();

        FileReadStatus status = logParser.readForFighters(selectedFilePath);

        if (status != FileReadStatus.SUCCESS) {
            showStatusMessage(getMessageForStatus(status));
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) this.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private String getMessageForStatus(FileReadStatus status) {
        return switch (status) {
            case NO_FILE_SELECTED -> "Aucun fichier sélectionné. Veuillez sélectionner un fichier de log.";
            case FILE_NOT_FOUND -> "Le fichier sélectionné n'existe pas.";
            case EMPTY_FILE -> "Le fichier sélectionné est vide.";
            case IO_ERROR -> "Erreur lors de la lecture du fichier.";
            default -> "Erreur inconnue lors de la lecture du fichier.";
        };
    }

    private void showStatusMessage(String message) {
        StatusMessageView statusMessageView = new StatusMessageView(message);
        contentContainer.getChildren().add(statusMessageView);
    }

    private void showCharactersDamages(Characters characters) {
        CharactersDamagesView charactersDamagesView = new CharactersDamagesView(characters);
        contentContainer.getChildren().add(charactersDamagesView);
    }

    private void showCharactersHeals(Characters characters) {
        CharactersHealsView charactersHealsView = new CharactersHealsView(characters);
        contentContainer.getChildren().add(charactersHealsView);
    }

    private void showCharactersShields(Characters characters) {
        CharactersShieldsView charactersShieldsView = new CharactersShieldsView(characters);
        contentContainer.getChildren().add(charactersShieldsView);
    }

    private enum View {
        DPT_VIEW,
        HEAL_VIEW,
        SHIELD_VIEW
    }
}

