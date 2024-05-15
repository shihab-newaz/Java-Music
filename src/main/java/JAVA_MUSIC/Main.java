package JAVA_MUSIC;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static Stage stage1;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage1 = primaryStage;
        Parent root;
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/loginInterface.fxml")));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    @Override
    public void init() throws Exception {
        super.init();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
