package tapsi;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tapsi.logic.DataInterface;

//TODO: Comments overall
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/mainWindow.fxml"));
        primaryStage.setTitle("Anime Downloader");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("views/myStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/icon.png")));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                DataInterface.closeApplication();
            }
        });

        Parent pathRoot = FXMLLoader.load(getClass().getResource("views/pathSettings.fxml"));
        Stage pathStage = new Stage();
        Scene pathScene = new Scene(pathRoot);
        pathScene.getStylesheets().add(Main.class.getResource("views/myStyle.css").toExternalForm());
        pathStage.setScene(pathScene);
        pathStage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/icon.png")));
        pathStage.show();

        pathStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // TODO: Do something here! Basically it should just cancel everything
            }
        });

    }


    public static void main(String[] args) {
        launch(args);
    }
}
