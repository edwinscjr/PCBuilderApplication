package ph.edu.dlsu.lbycpa2.pcbuilderapplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class PCBuilderApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PCBuilderApplication.class.getResource("PCBuilderGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.setFill(Color.AQUAMARINE);
        primaryStage.setTitle("PC Builder Application");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }
}
