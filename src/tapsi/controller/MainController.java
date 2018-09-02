package tapsi.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import tapsi.controller.helper.ViewFactory;
import tapsi.controller.helper.ViewInterfaces;
import tapsi.controller.helper.ViewObserver;
import tapsi.logic.container.Anime;
import tapsi.logic.container.AnimeEntry;
import tapsi.logic.container.AnimeScope;
import tapsi.logic.container.AnimeStatus;
import tapsi.logic.handler.DataInterface;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO: Implement dynamic locationPaths in the view
//TODO: Setup Menu for "Auto-Download Timer" values and safe them in the DB
//TODO: Rework the filter and sort methods and create a class for this
//TODO: Try to rework the MainController too.

public class MainController extends AbstractController implements Initializable, ViewInterfaces.MainInterface {

    @FXML
    private ChoiceBox<String> chBoxAnimeFilter;

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

    private Stage stage;

    private ObservableList<String> listViewAnimeListItems;
    private ObservableList<String> listViewFeedListItems;
    private List<AnimeEntry> localFeedListEntries;
    private List<AnimeEntry> localDownloadlist;
    private Anime localAnimeDisplayed;
    private Anime localFeedAnime;
    private AnimeEntry localFeedEntry;

    private Timeline updateTimer;
    private Timeline downloadTimer;

    private ContextMenu contextMenu;

    public MainController(Stage stage) {
        super(stage);
        this.stage = stage;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ViewObserver.addMainListener(this);

        setUpAnimeListFilter();
        setupListViews();
        setUpChoiceBoxes();
        setUpTxtFields();
        setUpTimer();
    }

    @Override
    public void hideStage() {
        stage.hide();
    }

    @Override
    public void showStage() {
        stage.show();
    }

    @Override
    public void btnOkFromPathSettingsClicked(List<String> localPaths, String feedPath) {
        DataInterface.setPaths(feedPath, localPaths);
    }

    @FXML
    void btnFeedAddOnAction() {
        if (!listViewDownloads.getItems().contains(localFeedEntry.getName() + "#" + localFeedEntry.getNumber())) {
            listViewDownloads.getItems().add(localFeedEntry.getName() + "#" + localFeedEntry.getNumber());
            localDownloadlist.add(localFeedEntry);
            if (localFeedAnime == null) {
                localFeedAnime = new Anime(localFeedEntry.getName());
                DataInterface.addTempAnime(localFeedAnime);
            }
            localFeedAnime.setAnimeScope(AnimeScope.MUSTHAVE);
            DataInterface.setAnimeData(localFeedAnime);
            setFeedTab(localFeedEntry, false);

        }
    }

    @FXML
    void btnFeedDeleteOnAction() {
        if (listViewDownloads.getItems().contains(localFeedEntry.getName() + "#" + localFeedEntry.getNumber())) {
            listViewDownloads.getItems().remove(localFeedEntry.getName() + "#" + localFeedEntry.getNumber());
            localDownloadlist.remove(localFeedEntry);
        }
    }

    @FXML
    void btnAnimeShowInExplorerOnAction() {
        int index = listViewAnime.getSelectionModel().getSelectedIndex();
        String path = localAnimeDisplayed.getAnimeEntries().get(index).getFullPathName();
        try {
            Runtime.getRuntime().exec("explorer.exe /select, " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnAnimePlayOnAction() {
        playEpisode();
    }

    private void playEpisode() {
        AnimeEntry entry = localAnimeDisplayed.getAnimeEntryByFileName(listViewAnime.getSelectionModel().getSelectedItem());
        if (entry == null)
            return;
        File file = new File(entry.getFullPathName());
        if (file.exists()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnAnimeDeleteOnAction() {
        DataInterface.deleteAnime(localAnimeDisplayed.getName());
        btnUpdate();
    }

    @FXML
    void btnDownloadOnAction() {
        btnDownload();
    }

    private void btnDownload() {
        List<String> localFeedList = DataInterface.getFeedAnimeNames();
        localFeedListEntries = DataInterface.getFeedEntries();
        setListView(listViewFeed, localFeedList);
        listViewFeedListItems = FXCollections.observableArrayList(localFeedList);
        localDownloadlist = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
        ObservableList<String> downloadItems = FXCollections.observableArrayList(listOfAnimeEntryToListOfString(localDownloadlist));
        listViewDownloads.setItems(downloadItems);
        setFeedTab(localFeedListEntries.get(0), false);
        listViewFeed.getSelectionModel().select(listViewFeedListItems.get(0));
        listViewFeed.scrollTo(listViewFeedListItems.get(0));
    }

    @FXML
    void btnUpdateOnAction() {
        btnUpdate();
    }

    private void btnUpdate() {
        List<String> localAnimeList = getFilteredList(chBoxAnimeFilter.getSelectionModel().getSelectedItem(), DataInterface.getLocalAnimeNames());
        setListView(listViewAnimeList, localAnimeList);
        listViewAnimeListItems = FXCollections.observableArrayList(localAnimeList);
        lblListCount.setText(Integer.toString(listViewAnimeList.getItems().size()));
        txtFieldSearch.setText("");
        localDownloadlist = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
        ObservableList<String> downloadItems = FXCollections.observableArrayList(listOfAnimeEntryToListOfString(localDownloadlist));
        listViewDownloads.setItems(downloadItems);
        setAnimeTab(listViewAnimeListItems.get(0), false);
        listViewAnimeList.getSelectionModel().select(listViewAnimeListItems.get(0));
        listViewAnimeList.scrollTo(listViewAnimeListItems.get(0));

    }

    @FXML
    void btnDownloadUpdateOnAction() {
        btnDownloadUpdate();
    }

    private void btnDownloadUpdate() {
        List<String> localFeedList = DataInterface.getFeedAnimeNames();
        localFeedListEntries = DataInterface.getFeedEntries();
        setListView(listViewFeed, localFeedList);
        listViewFeedListItems = FXCollections.observableArrayList(localFeedList);

        List<String> localAnimeList = getFilteredList(chBoxAnimeFilter.getSelectionModel().getSelectedItem(), DataInterface.getLocalAnimeNames());
        setListView(listViewAnimeList, localAnimeList);
        listViewAnimeListItems = FXCollections.observableArrayList(localAnimeList);
        lblListCount.setText(Integer.toString(listViewAnimeList.getItems().size()));
        txtFieldSearch.setText("");

        localDownloadlist = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
        ObservableList<String> downloadItems = FXCollections.observableArrayList(listOfAnimeEntryToListOfString(localDownloadlist));
        listViewDownloads.setItems(downloadItems);

        setFeedTab(localFeedListEntries.get(0), false);
        listViewFeed.getSelectionModel().select(listViewFeedListItems.get(0));
        listViewFeed.scrollTo(listViewFeedListItems.get(0));
    }

    @FXML
    void btnStartDownloadOnAction() {
        for (String animeNameAndNumber : listViewDownloads.getItems()) {
            for (AnimeEntry entry : localDownloadlist) {
                String name = animeNameAndNumber.substring(0, animeNameAndNumber.indexOf("#"));
                String number = animeNameAndNumber.substring(animeNameAndNumber.indexOf("#") + 1, animeNameAndNumber.length());
                if (entry.getName().equals(name) && entry.getNumber().equals(number))
                    DataInterface.startDownload(entry);
            }
        }
    }

    @FXML
    void menuFileCloseOnAction() {
        DataInterface.closeApplication();
        stage.close();
    }

    @FXML
    void menuSettingsPathsOnAction() {
        ViewFactory.initPathSettingsView();
        ViewObserver.showPathSettings();
    }

    @FXML
    void menuAutoStartOnAction() {
        updateTimer.play();
        downloadTimer.play();
        lblStatus.setText("Timer ON");
    }

    @FXML
    void menuAutoStopOnAction() {
        updateTimer.stop();
        downloadTimer.stop();
        lblStatus.setText("Timer OFF");
    }

    @FXML
    void menuHelpAboutOnAction() {

    }


    private void setUpTimer() {
        updateTimer = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnDownloadUpdate();
            }
        }));
        updateTimer.setCycleCount(Timeline.INDEFINITE);

        downloadTimer = new Timeline(new KeyFrame(Duration.minutes(30), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnStartDownloadOnAction();
            }
        }));
        downloadTimer.setCycleCount(Timeline.INDEFINITE);
    }

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
        EventHandler<MouseEvent> mouseEventHandlerDownload = (MouseEvent event) -> {
            handleDownloadListMouseClick(event);
        };
        listViewDownloads.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandlerDownload);

        EventHandler<MouseEvent> mouseEventHandlerAnime = (MouseEvent event) -> {
            handleAnimeListMouseClick(event);
        };
        listViewAnimeList.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandlerAnime);

        EventHandler<MouseEvent> mouseEventHandlerFeed = (MouseEvent event) -> {
            handleFeedListMouseClick(event);
        };
        listViewFeed.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandlerFeed);

        EventHandler<MouseEvent> mouseEventHandlerEpisode = (MouseEvent event) -> {
            handleEpisodeListMouseClick(event);
        };
        listViewAnime.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandlerEpisode);
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
                    setAnimeTab(localAnimeDisplayed.getName(), false);
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
                "Status INFOMISSING", "Sort Date Newest", "Sort Date Oldest");

        listViewFeed.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item);
                            super.setStyle("-fx-text-fill: white");
                            if (DataInterface.getNewAnimeNames().contains(item)) {
                                super.setStyle("-fx-text-fill: green");
                            }
                        }
                    }
                };
            }
        });

        ObservableList<String> itemsAnimeFilter = FXCollections.observableArrayList(itemsFeedFilter);
        chBoxAnimeFilter.setItems(itemsAnimeFilter);
        chBoxAnimeFilter.setValue(itemsAnimeFilter.get(0));

        ChangeListener changeListenerAnimeFilter = ((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> list = getFilteredList(newValue.toString(), DataInterface.getLocalAnimeNames());
                listViewAnimeList.setItems(list);
                lblListCount.setText(Integer.toString(listViewAnimeList.getItems().size()));
            }
        });

        chBoxAnimeFilter.getSelectionModel().selectedItemProperty().addListener(changeListenerAnimeFilter);
    }

    private void setUpTxtFields() {
        txtFieldAnimeSeason.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue) && !newValue.equals("") && localAnimeDisplayed != null) {
                localAnimeDisplayed.setSeasonCount(Integer.valueOf(newValue));
                DataInterface.setAnimeData(localAnimeDisplayed);
                setAnimeTab(localAnimeDisplayed.getName(), false);
            }
        }));
    }

    private void setUpContextMenu(ListView<String> listView, MouseEvent event) {
        contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Copy Name");
        menuItem.setOnAction(ae -> copyName(listView));
        contextMenu.getItems().add(menuItem);
        contextMenu.show(listView, event.getScreenX(), event.getScreenY());
    }

    private void setUpContextMenu(ListView<String> listView, MouseEvent event, AnimeEntry entry) {
        contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Copy Name");
        menuItem.setOnAction(ae -> copyName(listView));

        MenuItem menuItem1;
        if (entry.getDownloadDate() != null) {
            menuItem1 = new MenuItem("Download date: " + entry.getDownloadDate());
        } else {
            menuItem1 = new MenuItem("Download date: No information");
        }
        MenuItem menuItem2 = new MenuItem("Path: " + entry.getFullPathName());
        contextMenu.getItems().addAll(menuItem, menuItem1, menuItem2);
        contextMenu.show(listView, event.getScreenX(), event.getScreenY());
    }

    private void copyName(ListView<String> listView) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(listView.getSelectionModel().getSelectedItem());
        clipboard.setContent(content);
    }

    private void setListView(ListView<String> listView, List<String> list) {
        ObservableList<String> listViewItems = FXCollections.observableArrayList(list);
        listView.setItems(listViewItems);
    }

    private void handleDownloadListMouseClick(MouseEvent event) {
        ListView<String> listView = (ListView<String>) event.getSource();

        String animeNameAndNumber = listView.getSelectionModel().getSelectedItem();
        String name = animeNameAndNumber.substring(0, animeNameAndNumber.indexOf("#"));
        String number = animeNameAndNumber.substring(animeNameAndNumber.indexOf("#") + 1, animeNameAndNumber.length());

        AnimeEntry animeEntry = null;
        for (AnimeEntry entry : localFeedListEntries) {
            if (entry.getName().equals(name) && entry.getNumber().equals(number))
                animeEntry = entry;
        }

        if (animeEntry == null)
            return;

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (listViewFeed.getItems().contains(animeEntry.getName())) {
                setFeedTab(animeEntry, true);
                listViewFeed.getSelectionModel().select(localFeedListEntries.indexOf(animeEntry));
                listViewFeed.scrollTo(localFeedListEntries.indexOf(animeEntry));
            }
        } else if (event.getButton().equals(MouseButton.SECONDARY)) {
            if (listViewAnimeList.getItems().contains(animeEntry.getName())) {
                setAnimeTab(animeEntry.getName(), true);
                listViewAnimeList.getSelectionModel().select(animeEntry.getName());
                listViewAnimeList.scrollTo(animeEntry.getName());
            }
        }
    }

    private void handleAnimeListMouseClick(MouseEvent event) {
        if (contextMenu != null)
            contextMenu.hide();

        ListView<String> listView = (ListView<String>) event.getSource();

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            setAnimeTab(listView.getSelectionModel().getSelectedItem(), true);
            listViewAnime.getSelectionModel().select(0);
            if (listViewDownloads.getItems().contains(listView.getSelectionModel().getSelectedItem())) {
                listViewDownloads.getSelectionModel().select(listView.getSelectionModel().getSelectedItem());
                listViewDownloads.scrollTo(listView.getSelectionModel().getSelectedItem());
            }
        } else if (event.getButton().equals(MouseButton.SECONDARY)) {
            setUpContextMenu(listView, event);
        }
    }

    private void handleFeedListMouseClick(MouseEvent event) {
        if (contextMenu != null)
            contextMenu.hide();

        ListView<String> listView = (ListView<String>) event.getSource();

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            setFeedTab(localFeedListEntries.get(listView.getSelectionModel().getSelectedIndex()), true);
            if (listViewDownloads.getItems().contains(listView.getSelectionModel().getSelectedItem())) {
                listViewDownloads.getSelectionModel().select(listView.getSelectionModel().getSelectedItem());
                listViewDownloads.scrollTo(listView.getSelectionModel().getSelectedItem());
            }
        } else if (event.getButton().equals(MouseButton.SECONDARY)) {
            setUpContextMenu(listView, event);
        }
    }

    private void handleEpisodeListMouseClick(MouseEvent event) {
        if (contextMenu != null)
            contextMenu.hide();

        ListView<String> listView = (ListView<String>) event.getSource();

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                playEpisode();
            }

        } else if (event.getButton().equals(MouseButton.SECONDARY)) {
            List<AnimeEntry> entries = localAnimeDisplayed.getAnimeEntries();
            AnimeEntry currentEntry = null;
            for (AnimeEntry entry : entries) {
                if (entry.getFileName().equals(listView.getSelectionModel().getSelectedItem()))
                    currentEntry = entry;
            }
            setUpContextMenu(listView, event, currentEntry);
        }
    }

    private void setAnimeTab(String selectedAnime, Boolean focus) {
        localAnimeDisplayed = DataInterface.getAnimeByName(selectedAnime);
        lblAnimeName.setText(localAnimeDisplayed.getName());
        ObservableList<String> listEpisodes = FXCollections.observableArrayList(DataInterface.toStringListWithNumber(localAnimeDisplayed.getAnimeEntries()));

        lblAnimeEpisodes.setText(Integer.toString(listEpisodes.size()));
        lblAnimeStatus.setText(localAnimeDisplayed.getAnimeStatus().toString());

        if (localAnimeDisplayed.getSeasonCount() != null) {
            txtFieldAnimeSeason.setText(Integer.toString(localAnimeDisplayed.getSeasonCount()));
            Integer localEpisodes = Integer.valueOf(lblAnimeEpisodes.getText());
            Integer missing = Integer.valueOf(localAnimeDisplayed.getSeasonCount()) - localEpisodes;
            if (missing >= 0)
                lblAnimeMissing.setText(String.valueOf(missing));
            else
                lblAnimeMissing.setText("-");
        }

        chBoxAnimeScope.setValue(localAnimeDisplayed.getAnimeScope().toString());

        FXCollections.sort(listEpisodes, Comparator.reverseOrder());
        listViewAnime.setItems(listEpisodes);
        if (focus) {
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(1);
        }
    }

    private void setFeedTab(AnimeEntry selectedFeed, Boolean focus) {
        if (selectedFeed == null)
            return;

        localFeedEntry = DataInterface.getFeedEntryByNameAndNumber(selectedFeed.getName(), selectedFeed.getNumber());
        localFeedAnime = DataInterface.getAnimeByName(selectedFeed.getName());
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
        if (focus) {
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(0);
        }
    }

    private ObservableList<String> getFilteredList(String newValue, List<String> entries) {
        ObservableList<String> returnValue = FXCollections.observableArrayList();

        for (String animeName : entries) {
            Anime anime = DataInterface.getAnimeByName(animeName);
            if (anime != null) {
                switch (newValue) {
                    case "ALL":
                        returnValue.add(animeName);
                        break;
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
                    case "Sort Date Newest":
                        return getSortedList(true);
                    case "Sort Date Oldest":
                        return getSortedList(false);

                }
            } else if (newValue.equals("ALL"))
                returnValue.add(animeName);
        }
        return returnValue;
    }

    private ObservableList<String> getSortedList(boolean newest) {

        List<Anime> anime = new ArrayList<>(DataInterface.getAnimeMap().values());

        anime.sort(((o1, o2) -> {
            Anime anime1 = (Anime) o1;
            Anime anime2 = (Anime) o2;

            int inverse = -1;
            if (newest)
                inverse = 1;

            if (anime1.getNewestEntry() == null && anime2.getNewestEntry() != null)
                return 1;
            if (anime1.getNewestEntry() != null && anime2.getNewestEntry() == null)
                return -1;
            if (anime1.getNewestEntry() == null && anime2.getNewestEntry() == null)
                return 0;

            if (anime1.getNewestEntry().after(anime2.getNewestEntry()))
                return -1 * inverse;
            else if (anime1.getNewestEntry().before(anime2.getNewestEntry()))
                return 1 * inverse;
            else
                return 0;

        }));

        ObservableList<String> returnValue = FXCollections.observableArrayList(listOfAnimeToListOfString(anime));
        return returnValue;
    }

    private List<String> listOfAnimeToListOfString(List<Anime> animes) {
        List<String> returnList = new ArrayList<>();

        for (Anime anime : animes) {
            returnList.add(anime.getName());
        }
        return returnList;
    }

    private List<String> listOfAnimeEntryToListOfString(List<AnimeEntry> animes) {
        List<String> returnList = new ArrayList<>();

        for (AnimeEntry entry : animes) {
            returnList.add(entry.getName() + "#" + entry.getNumber());
        }
        return returnList;
    }
}