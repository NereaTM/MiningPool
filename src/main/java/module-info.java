module com.svalero.miningpool {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.svalero.ui to javafx.fxml;
    exports com.svalero.ui;
}