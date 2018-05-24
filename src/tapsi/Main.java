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
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("views/mainWindow.fxml"));
        Parent root = mainLoader.load();
        primaryStage.setTitle("Anime Downloader");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("views/myStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/icon.png")));
        MainController mainController = mainLoader.getController();
        mainController.setStage(primaryStage);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DataInterface.closeApplication();
            }
        });
    }

    private void initPathSettingsView() throws Exception{
        FXMLLoader pathSettingsLoader = new FXMLLoader(getClass().getResource("views/pathSettings.fxml"));
        Parent pathRoot = pathSettingsLoader.load();
        Stage pathStage = new Stage();
        Scene pathScene = new Scene(pathRoot);
        pathScene.getStylesheets().add(Main.class.getResource("views/myStyle.css").toExternalForm());
        pathStage.setScene(pathScene);
        pathStage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/icon.png")));
        PathSettingsController pathSettingsController = pathSettingsLoader.getController();
        pathSettingsController.setStage(pathStage);
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
