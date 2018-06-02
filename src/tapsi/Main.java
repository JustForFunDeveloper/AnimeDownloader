package tapsi;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tapsi.controller.MainController;
import tapsi.controller.PathSettingsController;
import tapsi.views.ViewBuilder;
import tapsi.controller.ViewObserver;
import tapsi.logic.DataInterface;

//TODO: Comments overall
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        initMainView(primaryStage);
        initPathSettingsView();
    }

    private void initMainView(Stage primaryStage) throws Exception{
        ViewBuilder viewBuilder = new ViewBuilder();
        MainController mainController = new MainController();
        viewBuilder.initializeScene("mainWindow.fxml", mainController, primaryStage)
                .setCSS("myStyle.css")
                .setTitle("Anime Downloader")
                .setIcon("resources/icon.png");

        Stage stage = viewBuilder.getStage();
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DataInterface.closeApplication();
            }
        });
    }

    private void initPathSettingsView() throws Exception{
        ViewBuilder viewBuilder = new ViewBuilder();
        PathSettingsController pathSettingsController = new PathSettingsController();
        viewBuilder.initializeScene("pathSettings.fxml", pathSettingsController)
                .setCSS("myStyle.css")
                .setTitle("Anime Downloader")
                .setIcon("resources/icon.png");
//        FXMLLoader pathSettingsLoader = new FXMLLoader(getClass().getResource("views/pathSettings.fxml"));
//        Parent pathRoot = pathSettingsLoader.load();
//        Stage pathStage = new Stage();
//        Scene pathScene = new Scene(pathRoot);
//        pathScene.getStylesheets().add(Main.class.getResource("views/myStyle.css").toExternalForm());
//        pathStage.setScene(pathScene);
//        pathStage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/icon.png")));
//        PathSettingsController pathSettingsController = pathSettingsLoader.getController();
//        pathSettingsController.setStage(pathStage);

        Stage pathStage = viewBuilder.getStage();
        pathStage.setResizable(false);
        pathStage.initModality(Modality.APPLICATION_MODAL);

        pathStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                ViewObserver.showMain();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
