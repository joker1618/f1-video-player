package xxx.joker.apps.f1videoplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.f1videoplayer.model.VideoModel;
import xxx.joker.apps.f1videoplayer.model.VideoModelImpl;
import xxx.joker.apps.f1videoplayer.view.JkVideoPlayerF1;
import xxx.joker.libs.core.files.JkFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class PlayerLauncher extends Application {

    private static final Logger logger = LoggerFactory.getLogger(PlayerLauncher.class);

    private static boolean scenicView = false;
    private static JkVideoPlayerF1 videoPlayer;

    private static final String USAGE = "f1-player  [-sv]  <MP4_FILE>";
    private static final String BASE_FOLDER_CHOOSE_PROP = "base.folder.choose";

    public static void main(String[] args) {
        Path folder = Paths.get("video-files");
        String fnContains = "abu dhab";

        scenicView = args.length > 0 && "-sv".equals(args[0]);

        Path videoPath;
        if(args.length > 1) {
            videoPath = Paths.get(args[1]);
        } else {
            videoPath = JkFiles.findFile(folder, false, p -> StringUtils.containsIgnoreCase(p.getFileName().toString(), fnContains));
        }
        if(videoPath != null && Files.exists(videoPath)) {
            videoPlayer = new JkVideoPlayerF1(videoPath);
        }

        launch();
    }


    @Override
    public void start(Stage primaryStage) {
        if(videoPlayer == null) {
            videoPlayer = createVideoPlayer();
            if(videoPlayer == null) {
                Platform.exit();
                System.exit(1);
            }
        }

        // Create scene
        Group root = new Group();
        Scene scene = new Scene(root);
        scene.setRoot(videoPlayer);
        scene.getStylesheets().add(getClass().getResource("/css/common.css").toExternalForm());

        // Show stage
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        if(scenicView) {
            ScenicView.show(scene);
        }
    }

    private JkVideoPlayerF1 createVideoPlayer() {
        VideoModel model = VideoModelImpl.getModel();
        FileChooser fc = new FileChooser();
        fc.setTitle("Open video...");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP4", "*.mp4"));
        String folderPath = model.getProperty(BASE_FOLDER_CHOOSE_PROP);
        File initialFolder = folderPath == null ? null : new File(folderPath);
        if(initialFolder == null || !initialFolder.exists()) {
            initialFolder = new File(System.getProperty("user.home"), "Desktop");
        }
        if(initialFolder.exists()) {
            fc.setInitialDirectory(initialFolder);
        }

        File cf = fc.showOpenDialog(null);
        if(cf == null) {
            return null;
        }

        display("Choosed file: {}", cf);

        model.setProperty(BASE_FOLDER_CHOOSE_PROP, JkFiles.getParent(cf.toPath()).toAbsolutePath().toString());

        return new JkVideoPlayerF1(cf.toPath());
    }

    @Override
    public void stop() {
        if(videoPlayer != null) {
            videoPlayer.closePlayer();
            logger.info("Closed video-player");
        }
    }

}