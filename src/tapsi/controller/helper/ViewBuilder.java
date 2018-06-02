package tapsi.controller.helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tapsi.Main;
import tapsi.controller.AbstractController;

public class ViewBuilder {

    private Scene scene;
    private Stage stage;

    public ViewBuilder initializeScene(String fxmlPath, AbstractController controller, Stage stage) {
        FXMLLoader loader;
        Parent parent;
        this.stage = stage;
        try {
            loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(controller);
            parent = loader.load();
        } catch (Exception e) {
            return null;
        }

        scene = new Scene(parent);
        this.stage.setScene(scene);
        return this;
    }

    public ViewBuilder setCSS(String cssPath) {
        scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        return this;
    }

    public ViewBuilder setTitle(String title) {
        stage.setTitle(title);
        return this;
    }

    public ViewBuilder setIcon(String iconPath) {
        stage.getIcons().add(new Image(Main.class.getResourceAsStream(iconPath)));
        return this;
    }
}
