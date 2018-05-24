package tapsi.controller;

import java.util.List;

public interface ViewInterfaces {
    interface PathSettingsInterface {
        void hideStage();
        void showStage();
    }

    interface MainInterface {
        void hideStage();
        void showStage();
        void btnOkFromPathSettingsClicked (String localPath, String feedPath);
    }
}
