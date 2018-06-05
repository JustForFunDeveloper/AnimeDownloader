package tapsi.controller.helper;

import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tapsi.controller.MainController;
import tapsi.controller.PathSettingsController;
import tapsi.logic.handler.DataInterface;

public class ViewFactory {

    public static void initMainView(Stage primaryStage) {
        ViewBuilder viewBuilder = new ViewBuilder();
        MainController mainController = new MainController(primaryStage);
        viewBuilder.initializeScene("/tapsi/views/mainWindow.fxml", mainController, primaryStage)
                .setCSS("/tapsi/views/myStyle.css")
                .setTitle("Anime Downloader")
                .setIcon("/tapsi/resources/icon.png");

        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DataInterface.closeApplication();
            }
        });
    }

    public static void initPathSettingsView() {
        ViewBuilder viewBuilder = new ViewBuilder();
        Stage stage = new Stage();
        PathSettingsController pathSettingsController = new PathSettingsController(stage);
        viewBuilder.initializeScene("/tapsi/views/pathSettings.fxml", pathSettingsController, stage)
                .setCSS("/tapsi/views/myStyle.css")
                .setTitle("Anime Downloader")
                .setIcon("/tapsi/resources/icon.png");

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
    }
}
