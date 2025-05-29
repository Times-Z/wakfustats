package com.wakfoverlay.ui;

import com.wakfoverlay.domain.fight.model.Characters;
import com.wakfoverlay.domain.fight.port.primary.FetchCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateCharacter;
import com.wakfoverlay.domain.fight.port.primary.UpdateStatusEffect;
import com.wakfoverlay.domain.fight.port.secondary.DamagesRepository;
import com.wakfoverlay.domain.fight.port.secondary.HealsRepository;
import com.wakfoverlay.domain.fight.port.secondary.ShieldsRepository;
import com.wakfoverlay.domain.fight.port.secondary.TargetedDamagesRepository;
import com.wakfoverlay.domain.logs.model.FileReadStatus;
import com.wakfoverlay.exposition.LogParser;
import com.wakfoverlay.exposition.UserPreferences;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static javafx.scene.layout.Priority.ALWAYS;

public class MainWindow extends VBox {

    private final FetchCharacter fetchCharacter;
    private final UpdateCharacter updateCharacter;
    private final UpdateStatusEffect updateStatusEffect;
    private final DamagesRepository damagesRepository;
    private final TargetedDamagesRepository targetedDamagesRepository;
    private final HealsRepository healsRepository;
    private final ShieldsRepository shieldsRepository;
    private final LogParser logParser;
    private final UserPreferences userPreferences;

    private final VBox contentContainer;

    private String selectedFilePath;
    private View currentView = View.DPT_VIEW;
    private boolean firstLaunch = true;
    private boolean isInitializing = false;
    private boolean onlyBossDamages = false;

    public MainWindow(
            FetchCharacter fetchCharacter,
            UpdateCharacter updateCharacter,
            UpdateStatusEffect updateStatusEffect,
            DamagesRepository damagesRepository, TargetedDamagesRepository targetedDamagesRepository,
            HealsRepository healsRepository,
            ShieldsRepository shieldsRepository,
            LogParser logParser
    ) {
        this.fetchCharacter = fetchCharacter;
        this.updateCharacter = updateCharacter;
        this.updateStatusEffect = updateStatusEffect;
        this.damagesRepository = damagesRepository;
        this.targetedDamagesRepository = targetedDamagesRepository;
        this.healsRepository = healsRepository;
        this.shieldsRepository = shieldsRepository;
        this.logParser = logParser;
        this.userPreferences = new UserPreferences(MainWindow.class);
        this.selectedFilePath = userPreferences.getFilePath();

        setupWindowAppearance();

        TitleBar titleBar = createTitleBar();
        this.contentContainer = new VBox();
        this.contentContainer.setSpacing(2);
        ScrollPane contentScrollPane = createScrollPane();

        this.getChildren().addAll(titleBar, contentScrollPane);

        if (selectedFilePath != null) {
            initializeFromFile(selectedFilePath);
        }

        showCurrentView();
    }

    public void readLogData() {
        if (selectedFilePath != null && !isInitializing) {
            FileReadStatus status = logParser.readNewLogLines(selectedFilePath, false);

            if (status != FileReadStatus.SUCCESS) {
                handleFileReadStatus(status);
            }
        }
    }

    public void updateDisplay() {
        if (selectedFilePath == null) {
            contentContainer.getChildren().clear();
            showStatusMessage("Aucun fichier sélectionné. Veuillez sélectionner un fichier de log.");
            return;
        }

        if (isInitializing) {
            contentContainer.getChildren().clear();
            showStatusMessage("Initialisation en cours... Veuillez patienter.");
            return;
        }

        showCurrentView();
    }

    private void initializeFromFile(String filePath) {
        if (filePath == null) return;

        isInitializing = true;

        resetInMemoryData();

        CompletableFuture.supplyAsync(() -> {
            return logParser.initializeFromFile(filePath);
        }).thenAcceptAsync(status -> {
            Platform.runLater(() -> {
                if (status != FileReadStatus.SUCCESS) {
                    handleFileReadStatus(status);
                }

                firstLaunch = false;
                isInitializing = false;
            });
        }, Platform::runLater);
    }

    private void resetInMemoryData() {
        updateCharacter.resetCharactersStats();
        updateCharacter.resetCharacters();
        updateStatusEffect.resetStatusEffects();
        damagesRepository.resetDamages();
        targetedDamagesRepository.resetTargetedDamages();
        healsRepository.resetHeals();
        shieldsRepository.resetShields();
    }

    public void updateDamagesDisplay() {
        contentContainer.getChildren().clear();
        Characters rankedCharacters;

        if (onlyBossDamages) {
            rankedCharacters = fetchCharacter.rankedCharactersByDamagesForBoss();
        } else {
            rankedCharacters = fetchCharacter.rankedCharactersByDamages();
        }

        if (rankedCharacters.characters().isEmpty()) {
            showStatusMessage("Aucune donnée de dégâts disponible.");
            return;
        }

        showCharactersDamages(rankedCharacters);
    }

    public void updateHealsDisplay() {
        contentContainer.getChildren().clear();

        Characters rankedCharacters = fetchCharacter.rankedCharactersByHeals();

        if (rankedCharacters.characters().isEmpty()) {
            showStatusMessage("Aucune donnée de soins disponible.");
            return;
        }

        showCharactersHeals(rankedCharacters);
    }

    public void updateShieldsDisplay() {
        contentContainer.getChildren().clear();

        Characters rankedCharacters = fetchCharacter.rankedCharactersByShields();

        if (rankedCharacters.characters().isEmpty()) {
            showStatusMessage("Aucune donnée de boucliers disponible.");
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
        if (isInitializing) {
            updateDisplay();
            return;
        }

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
        }
    }

    private void handleFileReadStatus(FileReadStatus status) {
        if (status != FileReadStatus.SUCCESS) {
            contentContainer.getChildren().clear();
            showStatusMessage(getMessageForStatus(status));
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
                this::showShieldsView,
                this::toggleOnlyBossDamages
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

            initializeFromFile(this.selectedFilePath);
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
        if (selectedFilePath != null) {
            initializeFromFile(selectedFilePath);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) this.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void toggleOnlyBossDamages(boolean enabled) {
        this.onlyBossDamages = enabled;
        if (currentView == View.DPT_VIEW && !isInitializing) {
            updateDamagesDisplay();
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
