package com.wakfoverlay.ui;

import com.wakfoverlay.domain.player.model.Players;
import com.wakfoverlay.domain.player.port.primary.FetchPlayer;
import com.wakfoverlay.domain.player.port.primary.UpdatePlayerDamages;
import com.wakfoverlay.exposition.TheAnalyzer;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static javafx.scene.layout.Priority.ALWAYS;

public class MainWindow extends VBox {

    // Services
    private final FetchPlayer fetchPlayer;
    private final UpdatePlayerDamages updatePlayerDamages;
    private final TheAnalyzer theAnalyzer;
    private final UserPreferences userPreferences;

    // UI Components
    private final ScrollPane contentScrollPane;
    private final VBox contentContainer;
    private PlayerListView playerListView;
    private StatusMessageView statusMessageView;

    // État
    private String selectedFilePath;

    public MainWindow(FetchPlayer fetchPlayer, UpdatePlayerDamages updatePlayerDamages,
                      TheAnalyzer theAnalyzer) {
        this.fetchPlayer = fetchPlayer;
        this.updatePlayerDamages = updatePlayerDamages;
        this.theAnalyzer = theAnalyzer;
        this.userPreferences = new UserPreferences(MainWindow.class);

        setupWindowAppearance();

        this.selectedFilePath = userPreferences.getFilePath();

        TitleBar titleBar = createTitleBar();
        this.contentContainer = new VBox();
        this.contentContainer.setSpacing(2);

        this.contentScrollPane = createScrollPane();

        WindowResizer resizer = new WindowResizer(this);

        this.getChildren().addAll(titleBar, contentScrollPane, resizer.getResizeHandle());

        updateDisplay();
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
                this::closeWindow
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
            updateDisplay();
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
        updatePlayerDamages.resetPlayersDamages();
        updateDisplay();
    }

    private void closeWindow() {
        Stage stage = (Stage) this.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public void resetPreferences() {
        userPreferences.clearPreferences();
        this.selectedFilePath = null;
        updateDisplay();
    }

    public void updateDisplay() {
        contentContainer.getChildren().clear();

        TheAnalyzer.FileReadStatus status = theAnalyzer.readLogFile(selectedFilePath);

        if (status != TheAnalyzer.FileReadStatus.SUCCESS) {
            showStatusMessage(getMessageForStatus(status));
            return;
        }

        Players rankedPlayers = fetchPlayer.rankedPlayers();
        if (rankedPlayers.players().isEmpty()) {
            showStatusMessage("Aucune donnée disponible dans le fichier sélectionné.");
            return;
        }

        showPlayerList(rankedPlayers);
    }

    private String getMessageForStatus(TheAnalyzer.FileReadStatus status) {
        return switch (status) {
            case NO_FILE_SELECTED -> "Aucun fichier sélectionné. Veuillez sélectionner un fichier de log.";
            case FILE_NOT_FOUND -> "Le fichier sélectionné n'existe pas.";
            case EMPTY_FILE -> "Le fichier sélectionné est vide.";
            case IO_ERROR -> "Erreur lors de la lecture du fichier.";
            default -> "Erreur inconnue lors de la lecture du fichier.";
        };
    }

    private void showStatusMessage(String message) {
        statusMessageView = new StatusMessageView(message);
        contentContainer.getChildren().add(statusMessageView);
    }

    private void showPlayerList(Players players) {
        playerListView = new PlayerListView(players);
        contentContainer.getChildren().add(playerListView);
    }
}
