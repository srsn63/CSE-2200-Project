module com.example.app1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires jbcrypt;
    requires java.net.http;
    requires com.google.gson;
    requires java.sql;
    opens com.example.app1 to javafx.fxml;
    exports com.example.app1;
}