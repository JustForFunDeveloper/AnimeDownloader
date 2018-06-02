package tapsi.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.UrlValidator;
import tapsi.controller.helper.ViewInterfaces;
import tapsi.controller.helper.ViewObserver;
import tapsi.logic.handler.DataInterface;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PathSettingsController extends AbstractController implements Initializable, ViewInterfaces.PathSettingsInterface {

    @FXML
    private TextField txtFieldFeedPath;

    @FXML
    private TextField txtFieldLocalPath;

    @FXML
    private TextField txtFieldLocalPath1;

    private Stage stage;

    public PathSettingsController(Stage stage) {
        super(stage);
        this.stage = stage;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ViewObserver.addPathSettingsListener(this);
        List<String> localPaths = DataInterface.getLocalPaths();
        if (localPaths == null)
            return;
        if (localPaths.size() > 0)
            txtFieldLocalPath.setText(localPaths.get(0));
        if (localPaths.size() > 1)
            txtFieldLocalPath1.setText(localPaths.get(1));
        txtFieldFeedPath.setText(DataInterface.getFeedPath());
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

        List<String> localPaths = new ArrayList<>();
        localPaths.add(txtFieldLocalPath.getText());
        if (!txtFieldLocalPath1.getText().isEmpty())
            localPaths.add(txtFieldLocalPath1.getText());
        ViewObserver.btnOkFromPathSettingsClicked(localPaths, txtFieldFeedPath.getText());
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