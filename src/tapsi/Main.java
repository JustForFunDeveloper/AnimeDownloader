package tapsi;

import javafx.application.Application;
import javafx.stage.Stage;
import tapsi.controller.helper.ViewFactory;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        ViewFactory.initMainView(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
