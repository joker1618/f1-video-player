package xxx.joker.apps.f1videoplayer.ctx;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;

public class Const {

    private static final Path BASE_FOLDER = JkEnvironment.getAppsFolder();
    public static final Path REPO_FOLDER = BASE_FOLDER.resolve("f1-video-player-repo");
    public static final Path UPDATES_FOLDER = REPO_FOLDER.resolve("updates");
    public static final String DB_NAME = "vp";

    public static boolean showScenicView = false;

}
