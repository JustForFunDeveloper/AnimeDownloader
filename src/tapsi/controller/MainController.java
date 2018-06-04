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
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//TODO: Create a few more filter methods for the local implementation
//TODO: Implement dynamic locationPaths in the view
//TODO: Setup Menu for "Auto-Download Timer" values and safe them in the DB
//TODO: Safe date of downloads (implement Filter: "Recent" downloaded episodes -> filter by time)
//TODO: Sort the episode entries in all anime
//TODO: Show path of the episode

public class MainController extends AbstractController implements Initializable, ViewInterfaces.MainInterface {

    @FXML
    private ChoiceBox<String> chBoxFeedFilter;

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
        if (!listViewDownloads.getItems().contains(localFeedEntry.getName())) {
            listViewDownloads.getItems().add(localFeedEntry.getName());
            if (localFeedAnime == null) {
                localFeedAnime = new Anime(localFeedEntry.getName());
                DataInterface.addTempAnime(localFeedAnime);
            }
            localFeedAnime.setAnimeScope(AnimeScope.MUSTHAVE);
            DataInterface.setAnimeData(localFeedAnime);
            setFeedTab(localFeedEntry.getName(), false);
        }
    }

    @FXML
    void btnFeedDeleteOnAction() {
        if (listViewDownloads.getItems().contains(localFeedEntry.getName()))
            listViewDownloads.getItems().remove(localFeedEntry.getName());
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
        int index = listViewAnime.getSelectionModel().getSelectedIndex();
        File file = new File(localAnimeDisplayed.getAnimeEntries().get(index).getFullPathName());
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
        List<String> localFeedList = getFilteredList(chBoxFeedFilter.getSelectionModel().getSelectedItem(), DataInterface.getFeedAnimeNames());
        setListView(listViewFeed, localFeedList);
        listViewFeedListItems = FXCollections.observableArrayList(localFeedList);
        ObservableList<String> downloadItems = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
        listViewDownloads.setItems(downloadItems);
        setFeedTab(listViewFeedListItems.get(0), false);
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
        ObservableList<String> downloadItems = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
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
        List<String> localFeedList = getFilteredList(chBoxFeedFilter.getSelectionModel().getSelectedItem(), DataInterface.getFeedAnimeNames());
        setListView(listViewFeed, localFeedList);
        listViewFeedListItems = FXCollections.observableArrayList(localFeedList);

        List<String> localAnimeList = getFilteredList(chBoxAnimeFilter.getSelectionModel().getSelectedItem(), DataInterface.getLocalAnimeNames());
        setListView(listViewAnimeList, localAnimeList);
        listViewAnimeListItems = FXCollections.observableArrayList(localAnimeList);
        lblListCount.setText(Integer.toString(listViewAnimeList.getItems().size()));
        txtFieldSearch.setText("");

        ObservableList<String> downloadItems = FXCollections.observableArrayList(DataInterface.getAutomaticDownloadFeeds());
        listViewDownloads.setItems(downloadItems);

        setFeedTab(listViewFeedListItems.get(0), false);
        listViewFeed.getSelectionModel().select(listViewFeedListItems.get(0));
        listViewFeed.scrollTo(listViewFeedListItems.get(0));
    }

    @FXML
    void btnStartDownloadOnAction() {
        for (String animeName : listViewDownloads.getItems()) {
            DataInterface.startDownload(animeName);
        }
    }

    @FXML
    void menuFileCloseOnAction() {
        DataInterface.closeApplication();
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
                "Status INFOMISSING");

        chBoxFeedFilter.setItems(itemsFeedFilter);
        chBoxFeedFilter.setValue(itemsFeedFilter.get(0));

        ChangeListener changeListenerFeedFilter = ((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> list = getFilteredList(newValue.toString(), DataInterface.getFeedAnimeNames());
                listViewFeed.setItems(list);
                lblFeedEpisode.setText("-");
                lblFeedExists.setText("-");
                lblFeedStatus.setText("-");
                lblFeedSeason.setText("-");
                chBoxFeedScope.setValue("NOTDEFINED");
            }
        });

        chBoxFeedFilter.getSelectionModel().selectedItemProperty().addListener(changeListenerFeedFilter);

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
        contextMenu.getItems().addAll(menuItem, menuItem1);
        contextMenu.show(listView, event.getScreenX(), event.getScreenY());
        System.out.println(localAnimeDisplayed.getNewestEntry() + " - " + localAnimeDisplayed.getOldestEntry());
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

        if (event.getButton().equals(MouseButton.PRIMARY)) {
            setFeedTab(listView.getSelectionModel().getSelectedItem(), true);
            if (listViewFeed.getItems().contains(listView.getSelectionModel().getSelectedItem())) {
                listViewFeed.getSelectionModel().select(listView.getSelectionModel().getSelectedItem());
                listViewFeed.scrollTo(listView.getSelectionModel().getSelectedItem());
            }
        } else if (event.getButton().equals(MouseButton.SECONDARY)) {
            setAnimeTab(listView.getSelectionModel().getSelectedItem(), true);
            if (listViewAnimeList.getItems().contains(listView.getSelectionModel().getSelectedItem())) {
                listViewAnimeList.getSelectionModel().select(listView.getSelectionModel().getSelectedItem());
                listViewAnimeList.scrollTo(listView.getSelectionModel().getSelectedItem());
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
            setFeedTab(listView.getSelectionModel().getSelectedItem(), true);
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

        listViewAnime.setItems(listEpisodes);
        if (focus) {
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(1);
        }
    }

    private void setFeedTab(String selectedFeed, Boolean focus) {
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
                }
            } else if (newValue.equals("ALL"))
                returnValue.add(animeName);
        }
        return returnValue;
    }

    private void sort() {
        List<Anime> anime = new ArrayList<>(DataInterface.getAnimeMap().values());

        anime.sort(((o1, o2) -> {
            Anime anime1 = (Anime) o1;
            Anime anime2 = (Anime) o2;
            return 0;

        }));

    }
}
