package xxx.joker.apps.f1videoplayer.model;

import xxx.joker.apps.f1videoplayer.model.entities.F1Video;
import xxx.joker.libs.datalayer.JkRepo;

import java.nio.file.Path;
import java.util.Set;

public interface VpModel extends JkRepo {

    F1Video getOrAddF1Video(Path videoPath);
    Set<F1Video> getF1Videos();

}
