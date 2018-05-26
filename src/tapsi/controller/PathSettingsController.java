package tapsi.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.UrlValidator;
import tapsi.logic.DataInterface;

import java.io.File;
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
        txtFieldLocalPath.setText(DataInterface.getLocalPath());
        txtFieldFeedPath.setText(DataInterface.getLocalPath());
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
        if (!checkValidLocalPath(txtFieldLocalPath.getText())) {
            System.err.println("Wrong path!");
            return;
        }

        if (!checkValidLink(txtFieldFeedPath.getText())) {
            System.err.println("Wrong link!");
            return;
        }

        ViewObserver.btnOkFromPathSettingsClicked(txtFieldLocalPath.getText(), txtFieldFeedPath.getText());
        stage.close();
    }

    @FXML
    void btnAbortOnAction() {
        stage.close();
    }

    private boolean checkValidLocalPath(String path) {
        if (path.equals("") || path == null)
            return false;
        File file = new File(path);
        if (!file.isDirectory())
            return false;
        else
            return true;
    }

    private boolean checkValidLink(String link) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(link);
    }
}

