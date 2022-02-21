module ph.edu.dlsu.lbycpa2.pcbuilderapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens ph.edu.dlsu.lbycpa2.pcbuilderapplication to javafx.fxml;
    exports ph.edu.dlsu.lbycpa2.pcbuilderapplication;
}