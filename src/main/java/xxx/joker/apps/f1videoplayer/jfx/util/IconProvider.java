package xxx.joker.apps.f1videoplayer.jfx.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import xxx.joker.libs.core.cache.JkCache;

import java.net.URL;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class IconProvider {

    public static final String BACKWARD_5 = "backward5.png";
    public static final String BACKWARD_10 = "backward10.png";
    public static final String BACKWARD_30 = "backward30.png";
    public static final String CAMERA = "camera.png";
    public static final String CLOSE = "close.png";
    public static final String DELETE_RED = "deleteRed.png";
    public static final String FORWARD_5 = "forward5.png";
    public static final String FORWARD_10 = "forward10.png";
    public static final String FORWARD_30 = "forward30.png";
    public static final String MINUS = "minus.png";
    public static final String NEXT = "next.png";
    public static final String PAUSE = "pause.png";
    public static final String PLAY = "play.png";
    public static final String PLUS = "plus.png";
    public static final String PREVIOUS = "previous.png";
    public static final String VOLUME_HIGH = "volumeHigh.png";
    public static final String VOLUME_LOW = "volumeLow.png";
    public static final String VOLUME_MIDDLE = "volumeMiddle.png";
    public static final String VOLUME_MUTE = "volumeMute.png";

    private JkCache<String, Image> imageCache = new JkCache<>();

    public Image getIconImage(String iconName) {
        Image image = imageCache.get(iconName);
        if(image == null) {
            URL url = getClass().getResource(strf("/icons/{}", iconName));
            image = new Image(url.toExternalForm());
            imageCache.add(iconName, image);
        }
        return image;
    }

    public ImageView getIcon(String iconName, Double fitSquareSide) {
        return getIcon(iconName, fitSquareSide, fitSquareSide);
    }
    public ImageView getIcon(String iconName, Double fitWidth, Double fitHeight) {
        return getIcon(iconName, fitWidth, fitHeight, true);
    }
    public ImageView getIcon(String iconName, Double fitSquareSide, boolean preserveRatio) {
        return getIcon(iconName, fitSquareSide, fitSquareSide, preserveRatio);
    }
    public ImageView getIcon(String iconName, Double fitWidth, Double fitHeight, boolean preserveRatio) {
        Image iconImg = getIconImage(iconName);
        ImageView iv = new ImageView(iconImg);
        if(fitWidth != null)    iv.setFitWidth(fitWidth);
        if(fitHeight != null)   iv.setFitHeight(fitHeight);
        iv.setPreserveRatio(preserveRatio);
        return iv;
    }
}
