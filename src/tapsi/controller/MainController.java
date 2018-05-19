package tapsi.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tapsi.logic.FeedHandler;
import tapsi.logic.FileHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label lblListCount;

    @FXML
    private TextArea txtArea;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private Label lblStatus;

    @FXML
    private ListView<String> listViewAnimeList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void btnDownloadOnAction() {
        FeedHandler.downloadFile();
        txtArea.setText(FeedHandler.getNewEpisodes().toString());
        FileHandler.readFolders();
    }
}
