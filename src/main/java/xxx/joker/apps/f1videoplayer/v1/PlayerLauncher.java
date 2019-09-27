package xxx.joker.apps.f1videoplayer.v1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.f1videoplayer.v1.model.VideoModel;
import xxx.joker.apps.f1videoplayer.v1.model.VideoModelImpl;
import xxx.joker.apps.f1videoplayer.v1.view.JkVideoPlayerF1;
import xxx.joker.libs.core.files.JkFiles;

import java.io.File;
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
//        Path folder = Paths.get("video-files");
//        String fnContains = "abu dhabXX";

        scenicView = args.length > 0 && "-sv".equals(args[0]);

        Path videoPath = null;
        int index = scenicView && args.length == 2 ? 1 : !scenicView && args.length == 1 ? 0 : -1;
        if(index != -1) {
            videoPath = Paths.get(args[index]);
//        } else {
//            videoPath = JkFiles.findFile(folder, false, p -> StringUtils.containsIgnoreCase(p.getFileName().toString(), fnContains));
        }
//        videoPath = Paths.get("C:\\Users\\fbarbano\\IdeaProjects\\APPS\\video-manager\\src\\test\\resources\\ert2.mp4");
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