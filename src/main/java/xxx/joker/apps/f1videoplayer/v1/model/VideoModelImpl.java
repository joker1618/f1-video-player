package xxx.joker.apps.f1videoplayer.v1.model;

import xxx.joker.apps.f1videoplayer.v1.common.Const;
import xxx.joker.apps.f1videoplayer.v2.dl.entities.F1Video;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.datalayer.JkRepoFile;

import java.nio.file.Path;
import java.util.Set;

public class VideoModelImpl extends JkRepoFile implements VideoModel {


    private static final VideoModel model = new VideoModelImpl();

    protected VideoModelImpl() {
        super(Const.REPO_FOLDER, Const.DB_NAME, "xxx.joker.apps.f1videoplayer.v2.dl.entities");
    }
    protected VideoModelImpl(Path repoFolder, String dbName) {
        super(repoFolder, dbName, "xxx.joker.apps.f1videoplayer.v2.dl.entities");
    }

    public static VideoModel getModel() {
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
