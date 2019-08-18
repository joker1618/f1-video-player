package xxx.joker.apps.f1videoplayer.model;

import xxx.joker.apps.f1videoplayer.model.entities.F1Video;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.datalayer.JkRepo;
import xxx.joker.libs.datalayer.JkRepoFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class VpModelImpl extends JkRepoFile implements VpModel {


    private static final  VpModel model = new VpModelImpl();

    private VpModelImpl() {
        super(Paths.get("video-files"), "vp", "xxx.joker.apps.f1videoplayer.model.entities");
    }

    public static VpModel getModel() {
        return model;
    }


    @Override
    public F1Video getOrAddF1Video(Path videoPath) {
        String md5 = JkEncryption.getMD5(videoPath);
        F1Video f1Video = new F1Video(md5);
        f1Video = getOrAddByPk(f1Video);
        return f1Video;
    }

    @Override
    public Set<F1Video> getF1Videos() {
        return getDataSet(F1Video.class);
    }
}
