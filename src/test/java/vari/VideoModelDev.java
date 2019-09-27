package vari;

import xxx.joker.apps.f1videoplayer.repo.VideoRepoImpl;

import java.nio.file.Path;

public class VideoModelDev extends VideoRepoImpl {

    public VideoModelDev(Path repoFolder, String dbName) {
        super(repoFolder, dbName);
    }

}
