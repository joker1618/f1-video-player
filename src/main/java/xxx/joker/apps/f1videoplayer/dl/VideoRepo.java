package xxx.joker.apps.f1videoplayer.dl;

import xxx.joker.apps.f1videoplayer.dl.entities.F1Video;
import xxx.joker.libs.datalayer.JkRepo;

import java.nio.file.Path;

public interface VideoRepo extends JkRepo {

    static VideoRepo getRepo() {
        return VideoRepoImpl.getInstance();
    }

    Path getLastOpenedFolder();
    void setLastOpenedFolder(Path folder);

    F1Video getOrAddF1Video(Path videoPath);
}
