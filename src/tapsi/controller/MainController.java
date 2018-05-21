package tapsi.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tapsi.logic.*;

import javax.swing.event.ChangeEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainController implements Initializable {

    @FXML
    private ChoiceBox<String> chBoxFeedFilter;

    @FXML
    private ProgressBar prgressBar;

    @FXML
    private Label lblAnimeMissing;

    @FXML
    private Label lblFeedEpisode;

    @FXML
    private Label lblFeedSeason;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private Label lblStatus;

    @FXML
    private TextField txtFieldAnimeSeason;

    @FXML
    private Label lblListCount;

    @FXML
    private Label lblAnimeEpisodes;

    @FXML
    private Label lblAnimeStatus;

    @FXML
    private ListView<String> listViewDownloads;

    @FXML
    private Label lblFeedStatus;

    @FXML
    private Label lblAnimeName;

    @FXML
    private Label lblFeedExists;

    @FXML
    private ChoiceBox<String> chBoxAnimeScope;

    @FXML
    private ChoiceBox<String> chBoxFeedScope;

    @FXML
    private ListView<String> listViewFeed;

    @FXML
    private ListView<String> listViewAnime;

    @FXML
    private ListView<String> listViewAnimeList;

    @FXML
    private TabPane tabPane;

    private ObservableList<String> listViewAnimeListItems;
    private ObservableList<String> listViewFeedListItems;
    private Anime localAnimeDisplayed;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpAnimeListFilter();
        setupListViews();
        setUpChoiceBoxes();
    }

    @FXML
    void btnFeedAddOnAction() {

    }

    @FXML
    void btnFeedDeleteOnAction() {

    }

    @FXML
    void btnAnimeShowInExplorerOnAction() {
        if (localAnimeDisplayed != null) {
            DataInterface.setAnimeData(localAnimeDisplayed);
        }
    }

    @FXML
    void btnAnimeDeleteOnAction() {

    }

    @FXML
    void btnDownloadOnAction() {
        List<String> localFeedList = DataInterface.getFeedAnimes();
        setListView(listViewFeed, localFeedList);
        listViewFeedListItems = FXCollections.observableArrayList(localFeedList);
    }

    @FXML
    void btnUpdateOnAction() {
        List<String> localAnimeList = DataInterface.getLocalAnimeNames();
        setListView(listViewAnimeList,localAnimeList);
        listViewAnimeListItems = FXCollections.observableArrayList(localAnimeList);
        lblListCount.setText(Integer.toString(listViewAnimeList.getItems().size()));
        txtFieldSearch.setText("");
    }

    @FXML
    void btnDownloadUpdateOnAction() {
        List<String> localFeedList = DataInterface.getFeedAnimes();
        setListView(listViewFeed, localFeedList);
        listViewFeedListItems = FXCollections.observableArrayList(localFeedList);

        List<String> localAnimeList = DataInterface.getLocalAnimeNames();
        setListView(listViewAnimeList,localAnimeList);
        listViewAnimeListItems = FXCollections.observableArrayList(localAnimeList);
        lblListCount.setText(Integer.toString(listViewAnimeList.getItems().size()));
        txtFieldSearch.setText("");
    }

    @FXML
    void menuFileCloseOnAction() {
        DataInterface.closeApplication();
    }

    @FXML
    void menuSettingsPathsOnAction() {

    }

    @FXML
    void menuHelpAboutOnAction() {

    }

    //TODO: Create another TAB with Statistics and also Buttons to extend the localAnimeList Filter

    /**
     * Sets up the on edit listener for the txtFieldSearch which handles the listViewTagList.
     * The listViewTagList will be filtered to the input from the txtFieldSearch.
     */
    private void setUpAnimeListFilter() {
        txtFieldSearch.textProperty().addListener(((observable, oldValue, newValue) -> {
            ObservableList<String> listViewItems = listViewAnimeListItems;
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

    private void setListView (ListView<String> listView, List<String> list) {
        ObservableList<String> listViewItems = FXCollections.observableArrayList(list);
        listView.setItems(listViewItems);
    }

    private void setupListViews() {
        EventHandler<MouseEvent> mouseEventEventHandler = (MouseEvent event) -> {
          handleAnimeListMouseClick(event);
        };
        listViewAnimeList.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventEventHandler);
    }

    private void handleAnimeListMouseClick (MouseEvent event) {
        ListView<String> listView = (ListView<String>) event.getSource();

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            setAnimeTab(listView.getSelectionModel().getSelectedItem());
        }
    }

    private void setAnimeTab (String selectedAnime) {
        localAnimeDisplayed = DataInterface.getAnimeByName(selectedAnime);
        lblAnimeName.setText(localAnimeDisplayed.getName());
        ObservableList<String> listEpisodes = FXCollections.observableArrayList(DataInterface.toStringListWithNumber(localAnimeDisplayed.getAnimeEntries()));

        lblAnimeEpisodes.setText(Integer.toString(listEpisodes.size()));
        lblAnimeStatus.setText(localAnimeDisplayed.getAnimeStatus().toString());

        if (localAnimeDisplayed.getSeasonCount() != null)
            txtFieldAnimeSeason.setText(Integer.toString(localAnimeDisplayed.getSeasonCount()));

        chBoxAnimeScope.setValue(localAnimeDisplayed.getAnimeScope().toString());

        listViewAnime.setItems(listEpisodes);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(1);

        //TODO: Missing value not implemented yet
    }

    private void setUpChoiceBoxes () {
        List<String> scopeItems = Stream.of(AnimeScope.values())
                .map(AnimeScope::name)
                .collect(Collectors.toList());
        ObservableList<String> itemsAnimeScope = FXCollections.observableArrayList(scopeItems);
        chBoxAnimeScope.setItems(itemsAnimeScope);
        chBoxAnimeScope.setValue(itemsAnimeScope.get(2));

        ChangeListener changeListener = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (localAnimeDisplayed != null) {
                    localAnimeDisplayed.setAnimeScope(AnimeScope.valueOf(newValue.toString()));
                    DataInterface.setAnimeData(localAnimeDisplayed);
                }
            }
        };

        //TODO: Setup all other Changelisteners here for the ChoiceBoxes

        chBoxAnimeScope.getSelectionModel().selectedItemProperty().addListener(changeListener);

        ObservableList<String> itemsFeedScope = FXCollections.observableArrayList(scopeItems);
        chBoxFeedScope.setItems(itemsFeedScope);
        chBoxFeedScope.setValue(itemsFeedScope.get(2));

        ObservableList<String> itemsFeedFilter = FXCollections.observableArrayList("Scope IGNORE",
                "Scope MUSTHAVE", "Scope NOTDEFINED", "Exists Local", "Status ONAIR", "Status UNFINISHED",
                "Status INFOMISSING");

        chBoxFeedFilter.setItems(itemsFeedFilter);
        chBoxFeedFilter.setValue(itemsFeedFilter.get(6));
    }

    //TODO: Setup the txtFieldChangeListener to here and check Status value then!

}
