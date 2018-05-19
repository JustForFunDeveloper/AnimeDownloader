package tapsi.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tapsi.logic.FeedHandler;
import tapsi.logic.FileHandler;

import java.net.URL;
import java.util.ListIterator;
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
        setUpListFilter();
    }

    @FXML
    void btnDownloadOnAction() {
        FeedHandler.downloadFile();
        txtArea.setText(FeedHandler.getNewEpisodes().toString());
        FileHandler.readFolders();

        ObservableList<String> listViewItems = FXCollections.observableArrayList(FileHandler.getAnimeNames());
        listViewAnimeList.setItems(listViewItems);
        lblListCount.setText(Integer.toString(listViewItems.size()));
    }

    /**
     * Sets up the on edit listener for the txtFieldSearch which handles the listViewTagList.
     * The listViewTagList will be filtered to the input from the txtFieldSearch.
     */
    private void setUpListFilter() {
        txtFieldSearch.textProperty().addListener(((observable, oldValue, newValue) -> {
            ObservableList<String> listViewItems = FXCollections.observableArrayList(FileHandler.getAnimeNames());
            if (newValue == null || newValue.equals("")) {
                listViewAnimeList.setItems(listViewItems);
            }
            ObservableList<String> subEntries = FXCollections.observableArrayList();
            for (String entry : listViewItems) {
                if (entry.toLowerCase().contains(newValue.toLowerCase()))
                    subEntries.add(entry);
            }
            listViewAnimeList.setItems(subEntries);
            lblListCount.setText(Integer.toString(subEntries.size()));
        }));
    }
}
