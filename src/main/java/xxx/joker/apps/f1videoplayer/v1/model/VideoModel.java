package xxx.joker.apps.f1videoplayer.v1.model;

import xxx.joker.apps.f1videoplayer.v2.dl.entities.F1Video;
import xxx.joker.libs.datalayer.JkRepo;

import java.nio.file.Path;
import java.util.Set;

public interface VideoModel extends JkRepo {

    F1Video getOrAddF1Video(Path videoPath);
    Set<F1Video> getF1Videos();

}
