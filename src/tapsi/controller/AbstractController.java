package tapsi.controller;

import javafx.stage.Stage;

public abstract class AbstractController {

    private Stage stage;

    public AbstractController(Stage stage) {
        this.stage = stage;
    }
}
