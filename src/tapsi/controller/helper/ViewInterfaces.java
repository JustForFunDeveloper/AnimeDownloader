package tapsi.controller.helper;

import java.util.List;

public interface ViewInterfaces {
    interface PathSettingsInterface {
        void hideStage();
        void showStage();
    }

    interface MainInterface {
        void hideStage();
        void showStage();
        void btnOkFromPathSettingsClicked (List<String> localPaths, String feedPath);
    }
}
