module ph.edu.dlsu.lbycpa2.pcbuilderapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires javafx.swing;

    opens ph.edu.dlsu.lbycpa2.pcbuilderapplication to javafx.fxml;
    exports ph.edu.dlsu.lbycpa2.pcbuilderapplication;
}