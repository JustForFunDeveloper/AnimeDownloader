package tapsi.controller;

import java.util.ArrayList;
import java.util.List;

public class ViewObserver {
    private static List<ViewInterfaces.MainInterface> mainListeners = new ArrayList<>();
    private static List<ViewInterfaces.PathSettingsInterface> pathSettingsListeners = new ArrayList<>();

    public static void addMainListener(ViewInterfaces.MainInterface mainInterface) {
        mainListeners.add(mainInterface);
    }

    public static void addPathSettingsListener(ViewInterfaces.PathSettingsInterface pathSettingsInterface) {
        pathSettingsListeners.add(pathSettingsInterface);
    }

    public static void hideMain() {
        for (ViewInterfaces.MainInterface mainInterface: mainListeners) {
            mainInterface.hideStage();
        }
    }

    public static void showMain() {
        for (ViewInterfaces.MainInterface mainInterface: mainListeners) {
            mainInterface.showStage();
        }
    }

    public static void btnOkFromPathSettingsClicked(List<String> localPaths, String feedPath) {
        for (ViewInterfaces.MainInterface mainInterface: mainListeners) {
            mainInterface.btnOkFromPathSettingsClicked(localPaths, feedPath);
        }
    }

    public static void hidePathSettings() {
        for (ViewInterfaces.PathSettingsInterface pathSettingsInterface : pathSettingsListeners) {
            pathSettingsInterface.hideStage();
        }
    }

    public static void showPathSettings() {
        for (ViewInterfaces.PathSettingsInterface pathSettingsInterface : pathSettingsListeners) {
            pathSettingsInterface.showStage();
        }
    }
}
