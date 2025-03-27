module org.example.financiaifinalfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires itextpdf;

    opens org.example.financiaifinalfx to javafx.fxml;
    exports org.example.financiaifinalfx.view;
    opens org.example.financiaifinalfx.view to javafx.fxml;
}