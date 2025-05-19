module com.wakfoverlay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens com.wakfoverlay.ui to javafx.fxml;
    exports com.wakfoverlay.ui;
    exports com.wakfoverlay.exposition;
    exports com.wakfoverlay.domain.fight.model;
    exports com.wakfoverlay.domain.fight.port.primary;
    exports com.wakfoverlay.infrastructure;
}
