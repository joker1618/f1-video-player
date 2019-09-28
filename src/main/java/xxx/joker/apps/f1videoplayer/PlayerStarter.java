package xxx.joker.apps.f1videoplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.f1videoplayer.ctx.Const;
import xxx.joker.apps.f1videoplayer.jfx.player.JfxVideoPlayerF1;
import xxx.joker.apps.f1videoplayer.repo.VideoRepo;
import xxx.joker.apps.f1videoplayer.repo.entities.F1Video;
import xxx.joker.libs.core.datetime.JkDateTime;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.format.JkFormatter;
import xxx.joker.libs.core.runtimes.JkEnvironment;
import xxx.joker.libs.core.runtimes.JkReflection;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class PlayerStarter  extends Application {

    private static final Logger logger = LoggerFactory.getLogger(PlayerStarter.class);

    private static final String USAGE = "f1videoplayer  [-sv]";

    private JfxVideoPlayerF1 videoPlayerPane;

    public static void main(String[] args) {
//        JkEnvironment.setAppsFolder(Paths.get(""));
        Const.showScenicView = args.length == 1 && "-sv".equals(args[0]);
        launch();
    }


    @Override
    public void start(Stage primaryStage) {
        checkForUpdates();

        videoPlayerPane = createVideoPlayer();
        if(videoPlayerPane == null) {
            Platform.exit();
        } else {
            // Create scene
            Group root = new Group();
            Scene scene = new Scene(root);
            scene.setRoot(videoPlayerPane);
            scene.getStylesheets().add(getClass().getResource("/css/common.css").toExternalForm());

            // Show stage
            primaryStage.setOnCloseRequest(e -> Platform.exit());
            primaryStage.setTitle("F1 VIDEO PLAYER");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

            if (Const.showScenicView) {
                ScenicView.show(scene);
            }
        }
    }

    private JfxVideoPlayerF1 createVideoPlayer() {
        VideoRepo repo = VideoRepo.getRepo();
        FileChooser fc = new FileChooser();
        fc.setTitle("Open video...");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP4", "*.mp4"));
        Path initialFolder = repo.getLastOpenedFolder();
        if(initialFolder == null || !Files.exists(initialFolder)) {
            initialFolder = JkEnvironment.getHomeFolder().resolve("Desktop");
        }
        fc.setInitialDirectory(initialFolder.toFile());

        File cf = fc.showOpenDialog(null);
        if(cf == null) {
            return null;
        }

        logger.info("Choosed file: {}", cf);

        repo.setLastOpenedFolder(JkFiles.getParent(cf.toPath()));

        F1Video f1Video = repo.getOrAddF1Video(cf.toPath());
        return new JfxVideoPlayerF1(f1Video, cf.toPath());
    }

    private void checkForUpdates() {
        List<Path> files = JkFiles.findFiles(Const.UPDATES_FOLDER, false);
        if(!files.isEmpty()) {
            Path updateFile = files.get(0);
            logger.info("Start updating repo from file {}", updateFile);
            JkFormatter formatter = JkFormatter.get();
            List<F1Video> newVideos = formatter.parseCsv(updateFile, F1Video.class);
            newVideos.forEach(v -> v.setEntityId(null));
            VideoRepo repo = VideoRepo.getRepo();
            boolean save = false;
            for (F1Video newVideo : newVideos) {
                F1Video byPk = repo.getByPk(newVideo);
                if(byPk == null) {
                    save = true;
                    repo.add(newVideo);
                    logger.info("Added video {}", newVideo);
                } else if(byPk.getMarks().isEmpty()){
                    save = true;
                    JkReflection.copyFields(newVideo, byPk, "marks");
                    logger.info("Updated video {}", byPk);
                }
            }
            if(save) {
                repo.commit();
            }

            String fname = strf("{}_{}", JkDateTime.now().toAod(), updateFile.getFileName());
            JkFiles.move(updateFile, updateFile.getParent().resolve("processed").resolve(fname));
        }
    }

    @Override
    public void stop() {
        if(videoPlayerPane != null) {
            videoPlayerPane.closePlayer();
            VideoRepo.getRepo().commit();
            logger.info("Closed video-player");
        }
    }

}