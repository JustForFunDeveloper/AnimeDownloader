package tapsi.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PathSettingsController implements Initializable, ViewInterfaces.PathSettingsInterface {

    @FXML
    private TextField txtFieldFeedPath;

    @FXML
    private TextField txtFieldLocalPath;

    private Stage stage;

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ViewObserver.addPathSettingsListener(this);
    }

    @Override
    public void hideStage() {
        stage.hide();
    }

    @Override
    public void showStage() {
        stage.showAndWait();
    }

    @FXML
    void btnOkOnAction() {
        ViewObserver.btnOkFromPathSettingsClicked(txtFieldLocalPath.getText(), txtFieldFeedPath.getText());
        stage.close();
    }

    @FXML
    void btnAbortOnAction() {
        stage.close();
    }
}

