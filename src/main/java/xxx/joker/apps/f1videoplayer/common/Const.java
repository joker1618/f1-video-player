package xxx.joker.apps.f1videoplayer.common;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Const {

    public static final Path BASE_FOLDER = JkEnvironment.getAppsFolder();
//    public static final Path BASE_FOLDER = Paths.get("");

    public static final Path REPO_FOLDER = BASE_FOLDER.resolve("f1-video-player");
    public static final String DB_NAME = "vp";

}
