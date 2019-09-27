package xxx.joker.apps.f1videoplayer.v2.ctx;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class Const {

    private static final Path BASE_FOLDER = JkEnvironment.getAppsFolder();
    public static final Path REPO_FOLDER = BASE_FOLDER.resolve("f1-video-player-last");
    public static final String DB_NAME = "vp";

    public static AtomicBoolean showScenicView = new AtomicBoolean(false);

}
