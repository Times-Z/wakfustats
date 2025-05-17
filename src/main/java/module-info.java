module com.wakfoverlay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens com.wakfoverlay.ui to javafx.fxml;
    exports com.wakfoverlay.ui;
}
