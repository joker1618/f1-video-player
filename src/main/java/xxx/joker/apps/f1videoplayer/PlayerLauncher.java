package xxx.joker.apps.f1videoplayer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.f1videoplayer.view.JkVideoPlayerF1;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkConsole;
import xxx.joker.libs.core.utils.JkConvert;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class PlayerLauncher extends Application {

    private static final Logger logger = LoggerFactory.getLogger(PlayerLauncher.class);

    private static boolean scenicView = false;
    private static JkVideoPlayerF1 videoPlayer;

    private static final String USAGE = "f1-player  <MP4_FILE>  [-sv]";

    public static void main(String[] args) {
        Path videoPath = Paths.get("video-files/2018-Singapore-setup-Ferrari.mp4");
        videoPlayer = new JkVideoPlayerF1(videoPath);
        scenicView = args.length > 0 && "-sv".equals(args[0]);

//        if(args.length == 0 || args.length > 2) {
//            throw new JkRuntimeException(USAGE);
//        }
//
//        if(args.length == 2) {
//            if(!"-sv".equals(args[1]))  throw new JkRuntimeException(USAGE);
//            scenicView = true;
//        }
//
//        Path videoPath = Paths.get(JkConvert.unixToWinPath(args[0]));
//        videoPlayer = new JkVideoPlayerF1(videoPath);

        launch();
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
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

    @Override
    public void stop() {
        videoPlayer.closePlayer();
        logger.info("Closed video-player");
    }

}