package xxx.joker.apps.f1videoplayer.v2.dl;

import xxx.joker.apps.f1videoplayer.v2.ctx.Const;
import xxx.joker.apps.f1videoplayer.v2.dl.entities.F1Video;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.datalayer.JkRepoFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class VideoRepoImpl extends JkRepoFile implements VideoRepo {

    private static final String PROP_LAST_OPENED_FOLDER = "base.folder.choose";

    private static VideoRepoImpl instance;

    private VideoRepoImpl() {
        super(Const.REPO_FOLDER, Const.DB_NAME, "xxx.joker.apps.f1videoplayer.v2.dl.entities");
    }

    protected static synchronized VideoRepo getInstance() {
        if(instance == null) {
            instance = new VideoRepoImpl();
        }
        return instance;
    }


    @Override
    public Path getLastOpenedFolder() {
        String prop = getProperty(PROP_LAST_OPENED_FOLDER);
        return prop == null ? null : Paths.get(prop);
    }

    @Override
    public void setLastOpenedFolder(Path folder) {
        setProperty(PROP_LAST_OPENED_FOLDER, folder.toString());
    }

    @Override
    public F1Video getOrAddF1Video(Path videoPath) {
        String md5 = JkEncryption.getMD5(videoPath);
        F1Video f1Video = new F1Video(md5);
        return getOrAddByPk(f1Video);
    }
}
