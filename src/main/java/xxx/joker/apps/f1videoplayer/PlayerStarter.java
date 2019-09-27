package xxx.joker.apps.f1videoplayer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.f1videoplayer.ctx.Const;
import xxx.joker.apps.f1videoplayer.dl.VideoRepo;
import xxx.joker.apps.f1videoplayer.dl.entities.F1Video;
import xxx.joker.apps.f1videoplayer.jfx.player.JfxVideoPlayerF1Pane;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class PlayerStarter  extends Application {

    private static final Logger logger = LoggerFactory.getLogger(PlayerStarter.class);

    private static final String USAGE = "f1-player  [-sv]";

    private JfxVideoPlayerF1Pane videoPlayerPane;

    public static void main(String[] args) {
        JkEnvironment.setAppsFolder(Paths.get(""));
        Const.showScenicView.set(args.length == 1 && "-sv".equals(args[0]));
        launch();
    }


    @Override
    public void start(Stage primaryStage) {
        videoPlayerPane = createVideoPlayer();

        // Create scene
        Group root = new Group();
        Scene scene = new Scene(root);
        scene.setRoot(videoPlayerPane);
        scene.getStylesheets().add(getClass().getResource("/css/common.css").toExternalForm());

        // Show stage
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        if(Const.showScenicView.get()) {
            ScenicView.show(scene);
        }
    }

    private JfxVideoPlayerF1Pane createVideoPlayer() {
        VideoRepo repo = VideoRepo.getRepo();
//        System.exit(1);
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

        display("Choosed file: {}", cf);

        repo.setLastOpenedFolder(JkFiles.getParent(cf.toPath()));

        F1Video f1Video = repo.getOrAddF1Video(cf.toPath());
        return new JfxVideoPlayerF1Pane(f1Video, cf.toPath());
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