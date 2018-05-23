package tapsi.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tapsi.logic.*;

import java.net.URL;
import java.util.ArrayList;
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
    private Anime localFeedAnime;
    private AnimeEntry localFeedEntry;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpAnimeListFilter();
        setupListViews();
        setUpChoiceBoxes();
        setUpTxtFields();
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
        List<String> localFeedList = DataInterface.getFeedAnimeNames();
        setListView(listViewFeed, localFeedList);
        listViewFeedListItems = FXCollections.observableArrayList(localFeedList);
        ObservableList<String> downloadItems = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
        listViewDownloads.setItems(downloadItems);
    }

    @FXML
    void btnUpdateOnAction() {
        List<String> localAnimeList = DataInterface.getLocalAnimeNames();
        setListView(listViewAnimeList, localAnimeList);
        listViewAnimeListItems = FXCollections.observableArrayList(localAnimeList);
        lblListCount.setText(Integer.toString(listViewAnimeList.getItems().size()));
        txtFieldSearch.setText("");
        ObservableList<String> downloadItems = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
        listViewDownloads.setItems(downloadItems);
    }

    @FXML
    void btnDownloadUpdateOnAction() {
        List<String> localFeedList = DataInterface.getFeedAnimeNames();
        setListView(listViewFeed, localFeedList);
        listViewFeedListItems = FXCollections.observableArrayList(localFeedList);

        List<String> localAnimeList = DataInterface.getLocalAnimeNames();
        setListView(listViewAnimeList, localAnimeList);
        listViewAnimeListItems = FXCollections.observableArrayList(localAnimeList);
        lblListCount.setText(Integer.toString(listViewAnimeList.getItems().size()));
        txtFieldSearch.setText("");

        ObservableList<String> downloadItems = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
        listViewDownloads.setItems(downloadItems);
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

    private void setupListViews() {
        EventHandler<MouseEvent> mouseEventEventHandlerAnime = (MouseEvent event) -> {
            handleAnimeListMouseClick(event);
        };
        listViewAnimeList.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventEventHandlerAnime);

        EventHandler<MouseEvent> mouseEventEventHandler1Feed = (MouseEvent event) -> {
            handleFeedListMouseClick(event);
        };
        listViewFeed.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventEventHandler1Feed);
    }

    private void setUpChoiceBoxes() {
        List<String> scopeItems = Stream.of(AnimeScope.values())
                .map(AnimeScope::name)
                .collect(Collectors.toList());
        ObservableList<String> itemsAnimeScope = FXCollections.observableArrayList(scopeItems);
        chBoxAnimeScope.setItems(itemsAnimeScope);
        chBoxAnimeScope.setValue(itemsAnimeScope.get(2));

        ChangeListener changeListenerAnime = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (localAnimeDisplayed != null) {
                    localAnimeDisplayed.setAnimeScope(AnimeScope.valueOf(newValue.toString()));
                    DataInterface.setAnimeData(localAnimeDisplayed);
                    setAnimeTab(localAnimeDisplayed.getName());
                }
            }
        };

        chBoxAnimeScope.getSelectionModel().selectedItemProperty().addListener(changeListenerAnime);

        ObservableList<String> itemsFeedScope = FXCollections.observableArrayList(scopeItems);
        chBoxFeedScope.setItems(itemsFeedScope);
        chBoxFeedScope.setValue(itemsFeedScope.get(2));

        ChangeListener changeListenerFeed = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (localFeedAnime != null) {
                    localFeedAnime.setAnimeScope(AnimeScope.valueOf(newValue.toString()));
                    DataInterface.setAnimeData(localFeedAnime);
                } else if (localFeedAnime == null && !newValue.toString().equals(AnimeScope.NOTDEFINED.toString())) {
                    Anime anime = new Anime(localFeedEntry.getName());
                    anime.setAnimeScope(AnimeScope.valueOf(newValue.toString()));
                    List<AnimeEntry> animeEntries = new ArrayList<>();
                    animeEntries.add(localFeedEntry);
                    DataInterface.setAnimeData(anime);

                    if (!listViewDownloads.getItems().contains(localFeedEntry.getName()))
                        listViewDownloads.getItems().add(localFeedEntry.getName());
                }
            }
        };

        chBoxFeedScope.getSelectionModel().selectedItemProperty().addListener(changeListenerFeed);

        ObservableList<String> itemsFeedFilter = FXCollections.observableArrayList("ALL", "Scope IGNORE",
                "Scope MUSTHAVE", "Scope NOTDEFINED", "Exists Local", "Status ONAIR", "Status UNFINISHED",
                "Status INFOMISSING");

        chBoxFeedFilter.setItems(itemsFeedFilter);
        chBoxFeedFilter.setValue(itemsFeedFilter.get(0));

        ChangeListener changeListenerFeedFilter = ((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> list= getFilteredList(newValue.toString());
                listViewFeed.setItems(list);
                lblFeedEpisode.setText("-");
                lblFeedExists.setText("-");
                lblFeedStatus.setText("-");
                lblFeedSeason.setText("-");
                chBoxFeedScope.setValue("NOTDEFINED");
            }
        });

        chBoxFeedFilter.getSelectionModel().selectedItemProperty().addListener(changeListenerFeedFilter);
    }

    private void setUpTxtFields () {
        txtFieldAnimeSeason.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null || !newValue.equals(oldValue) || !newValue.equals("")) {
                localAnimeDisplayed.setSeasonCount(Integer.valueOf(newValue));
                DataInterface.setAnimeData(localAnimeDisplayed);
                setAnimeTab(localAnimeDisplayed.getName());
            }
        }));
    }

    private void setListView(ListView<String> listView, List<String> list) {
        ObservableList<String> listViewItems = FXCollections.observableArrayList(list);
        listView.setItems(listViewItems);
    }

    private void handleAnimeListMouseClick(MouseEvent event) {
        ListView<String> listView = (ListView<String>) event.getSource();

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            setAnimeTab(listView.getSelectionModel().getSelectedItem());
        }
    }

    private void handleFeedListMouseClick(MouseEvent event) {
        ListView<String> listView = (ListView<String>) event.getSource();

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            setFeedTab(listView.getSelectionModel().getSelectedItem());
        }
    }

    private void setAnimeTab(String selectedAnime) {
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

    private void setFeedTab(String selectedFeed) {
        if (selectedFeed == null)
            return;

        localFeedEntry = DataInterface.getFeedEntryByName(selectedFeed);
        localFeedAnime = DataInterface.getAnimeByName(selectedFeed);
        if (localFeedAnime == null) {
            lblFeedExists.setText("false");
            lblFeedEpisode.setText(localFeedEntry.getNumber());
            lblFeedStatus.setText("-");
            lblFeedSeason.setText("-");
            chBoxFeedScope.setValue(AnimeScope.NOTDEFINED.toString());
        } else {
            lblFeedExists.setText("true");
            lblFeedEpisode.setText(localFeedEntry.getNumber());
            lblFeedStatus.setText(localFeedAnime.getAnimeStatus().toString());
            lblFeedSeason.setText(Integer.toString(localFeedAnime.getSeasonCount()));
            chBoxFeedScope.setValue(localFeedAnime.getAnimeScope().toString());
        }
    }

    private ObservableList<String> getFilteredList (String newValue) {
        ObservableList<String> returnValue = FXCollections.observableArrayList();
        List<String> entries = DataInterface.getFeedAnimeNames();

        for (String animeName : entries) {
            Anime anime = DataInterface.getAnimeByName(animeName);
            if (anime != null) {
                switch (newValue) {
                    case "Scope IGNORE":
                        if (anime.getAnimeScope().equals(AnimeScope.valueOf("IGNORE")))
                            returnValue.add(animeName);
                        break;
                    case "Scope MUSTHAVE":
                        if (anime.getAnimeScope().equals(AnimeScope.valueOf("MUSTHAVE")))
                            returnValue.add(animeName);
                        break;
                    case "Scope NOTDEFINED":
                        if (anime.getAnimeScope().equals(AnimeScope.valueOf("NOTDEFINED")))
                            returnValue.add(animeName);
                        break;
                    case "Exists Local":
                        returnValue.add(animeName);
                        break;
                    case "Status ONAIR":
                        if (anime.getAnimeStatus().equals(AnimeStatus.valueOf("ONAIR")))
                            returnValue.add(animeName);
                        break;
                    case "Status UNFINISHED":
                        if (anime.getAnimeStatus().equals(AnimeStatus.valueOf("UNFINISHED")))
                            returnValue.add(animeName);
                        break;
                    case "Status INFOMISSING":
                        if (anime.getAnimeStatus().equals(AnimeStatus.valueOf("INFOMISSING")))
                            returnValue.add(animeName);
                        break;
                }
            } else if (newValue.equals("ALL"))
                returnValue.add(animeName);
        }
        return returnValue;
    }

    //TODO: Setup the txtFieldChangeListener to here and check Status value then!

}
