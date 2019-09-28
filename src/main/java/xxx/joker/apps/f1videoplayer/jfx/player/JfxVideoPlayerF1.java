package xxx.joker.apps.f1videoplayer.jfx.player;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.f1videoplayer.repo.entities.F1Video;
import xxx.joker.apps.f1videoplayer.jfx.util.IconProvider;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.javafx.JfxUtil;

import java.io.BufferedWriter;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static xxx.joker.libs.core.javafx.JfxControls.createHBox;
import static xxx.joker.libs.core.utils.JkConsole.display;

public class JfxVideoPlayerF1 extends BorderPane {

	private static Logger logger = LoggerFactory.getLogger(JfxVideoPlayerF1.class);

	private Path videoPath;

	protected MediaView mediaView;

	private Button btnPlay;

	private Label lblActualTime;
	private Slider sliderTime;
	private ImageView ivVolume;
	private Slider sliderVolume;
	private Label lblVolume;

	private boolean isMediaTerminated;

	private IconProvider iconProvider;
	private List<Double> rateList = Arrays.asList(0.1, 0.3, 0.5, 1.0, 2.0, 5.0);

	public JfxVideoPlayerF1(F1Video f1Video, Path videoPath) {
		logger.info("Creating new JfxVideoPlayerF1 for: video={}, path={}", f1Video, videoPath);

		this.videoPath = videoPath;
		this.iconProvider = new IconProvider();

		setTop(createTopPane());
		setCenter(createMediaViewPane());
		setBottom(createMediaBarPane());
		setRight(createBookmarkPane(f1Video));

		getChildren().forEach(ch -> ch.getStyleClass().add("subPane"));

		getStyleClass().add("borderedRoot");
		getStylesheets().add(getClass().getResource("/css/JfxVideoPlayerF1.css").toExternalForm());
	}

	public void play() {
		if(!isPlaying()) {
			mediaView.getMediaPlayer().play();
		}
	}
	public boolean isPlaying() {
		return mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING;
	}
	public void pause() {
		if(mediaView.getMediaPlayer().getStatus() != MediaPlayer.Status.PAUSED) {
			mediaView.getMediaPlayer().pause();
		}
	}

	public void closePlayer() {
		MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
		if(mediaPlayer.getStatus() != MediaPlayer.Status.DISPOSED) {
			mediaPlayer.stop();
			mediaPlayer.dispose();
			logger.info("closed media player for file {}", videoPath);
		}
	}

	public MediaView getMediaView() {
		return mediaView;
	}

	private Pane createTopPane() {
		Label lblHeading = new Label(videoPath.getFileName().toString());

		HBox headingBox = new HBox();
		headingBox.getStyleClass().add("headingBox");

		Pane fill1 = new Pane();
		Pane fill2 = new Pane();
		Arrays.asList(fill1, fill2).forEach(f -> HBox.setHgrow(f, Priority.ALWAYS));
		headingBox.getChildren().addAll(fill1, lblHeading, fill2);

		return headingBox;
	}

	private Pane createMediaViewPane() {
		// Create media view
		mediaView = new MediaView();
		Media media = new Media(JkFiles.toURL(videoPath));
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(false);
		mediaPlayer.setCycleCount(1);
		mediaView.setPreserveRatio(true);
		mediaView.setMediaPlayer(mediaPlayer);

		// Create container pane
		Pane mvPane = new Pane() {};
		mvPane.getStyleClass().add("mediaViewPane");
		ChangeListener<Number> fitListener = getMediaViewFitListener(mvPane);
		mvPane.widthProperty().addListener(fitListener);
		mvPane.heightProperty().addListener(fitListener);

		// Create HBox (needed to center the media view)
		HBox hbox = new HBox(this.mediaView);
		hbox.prefWidthProperty().bind(mvPane.widthProperty());
		hbox.prefHeightProperty().bind(mvPane.heightProperty());
		hbox.setAlignment(Pos.CENTER);
		mvPane.getChildren().add(hbox);

		mvPane.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.PRIMARY) {
				btnPlay.fire();

			} else if(e.getButton() == MouseButton.MIDDLE) {
				Stage stage = JfxUtil.getStage(this);
				stage.setFullScreen(!stage.isFullScreen());

			} else if(e.getButton() == MouseButton.SECONDARY) {
				Stage stage = JfxUtil.getStage(this);
				stage.setMaximized(!stage.isMaximized());
			}
		});

		return mvPane;
	}
	private ChangeListener<Number> getMediaViewFitListener(Pane mvPane) {
		return (obs, old, nez) -> {
			if (mvPane.getWidth() > 0d && mvPane.getHeight() > 0d) {
				mediaView.setFitWidth(mvPane.getWidth());
				mediaView.setFitHeight(mvPane.getHeight());
			}
		};
	}
	private Pane createMediaBarPane() {
		HBox mediaBar = new HBox();
		mediaBar.getStyleClass().add("mediaBarPane");
		BorderPane.setAlignment(mediaBar, Pos.CENTER);

		double btnSize = 40d;
		double btnSizeSmall = 30d;

		// Play button
		btnPlay = new Button();
		ImageView ivPlayPause = iconProvider.getIcon(IconProvider.PLAY, btnSize);
		btnPlay.setGraphic(ivPlayPause);
		mediaBar.getChildren().add(btnPlay);

		// Label for time progress
		lblActualTime = new Label("");
		lblActualTime.getStyleClass().add("center-right");

		// time slider
		sliderTime = new Slider();
		HBox.setHgrow(sliderTime, Priority.ALWAYS);
		sliderTime.setMinWidth(50);
		sliderTime.setMaxWidth(Double.MAX_VALUE);

		// Previous and next buttons
		// Label for total time
		Label lblTotalTime = new Label();
		lblTotalTime.getStyleClass().add("center-left");
		mediaView.getMediaPlayer().totalDurationProperty().addListener((obs,o,n) -> lblTotalTime.setText(JkDuration.of(n).toStringElapsed(true, ChronoUnit.MINUTES)));
		HBox hboxTime = createHBox("lessSpacingBox sliderBox", lblActualTime, sliderTime, lblTotalTime);
		HBox.setHgrow(hboxTime, Priority.ALWAYS);
		mediaBar.getChildren().add(hboxTime);

		// Reproduction rate
		Button btnMinus = new Button();
		btnMinus.setGraphic(iconProvider.getIcon(IconProvider.MINUS, btnSizeSmall));
		btnMinus.setOnAction(e -> setVideoRate(true));
		Button btnPlus = new Button();
		btnPlus.setGraphic(iconProvider.getIcon(IconProvider.PLUS, btnSizeSmall));
		btnPlus.setOnAction(e -> setVideoRate(false));
		Label lblRate = new Label("1.0");
		mediaView.getMediaPlayer().rateProperty().addListener((obs,o,n) -> lblRate.setText(n.toString()));
		HBox hboxRate = createHBox("lessSpacingBox plusMinusBox", btnMinus, lblRate, btnPlus);
		mediaBar.getChildren().add(hboxRate);

		// Seek buttons
		HBox hboxSeek = createHBox("lessSpacingBox", getSeekButtons(btnSize));
		mediaBar.getChildren().add(hboxSeek);

		// volume
		ivVolume = iconProvider.getIcon(IconProvider.VOLUME_HIGH, btnSizeSmall);
		Button btnVolume = new Button();
		btnVolume.setGraphic(ivVolume);
		SimpleDoubleProperty lastVolumeValue = new SimpleDoubleProperty(1.0);
		btnVolume.setOnAction(e -> {
			MediaPlayer mp = mediaView.getMediaPlayer();
			if(mp.getVolume() == 0) {
				mp.setVolume(lastVolumeValue.get());
			} else {
				lastVolumeValue.set(mp.getVolume());
				mp.setVolume(0d);
			}
			updateVolumeIcon();
		});
		sliderVolume = new Slider();
		sliderVolume.setPrefWidth(100);
		sliderVolume.setMaxWidth(Region.USE_PREF_SIZE);
		sliderVolume.setMinWidth(30);
		lblVolume = new Label("");
		HBox hboxVol = createHBox("lessSpacingBox", btnVolume, sliderVolume, lblVolume);
		mediaBar.getChildren().add(hboxVol);

		initMediaBarBindings(ivPlayPause);

		return mediaBar;
	}
	private void setVideoRate(boolean minus) {
		double actualRate = mediaView.getMediaPlayer().getRate();
		int idx = rateList.indexOf(actualRate);
		Double newRate = null;
		if(idx > 0 && minus) {
			newRate = rateList.get(idx-1);
		} else if(!minus && idx < rateList.size()-1) {
			newRate = rateList.get(idx+1);
		}
		if(newRate != null) {
			mediaView.getMediaPlayer().setRate(newRate);
		}
	}
	private List<Button> getSeekButtons(double btnSize) {
		List<Button> seekButtons = new ArrayList<>();

		Button btnBack30 = new Button();
		btnBack30.setGraphic(iconProvider.getIcon(IconProvider.BACKWARD_30, btnSize));
		btnBack30.setOnAction(e -> {
			MediaPlayer mp = mediaView.getMediaPlayer();
			Duration currentTime = mp.getCurrentTime();
			Duration toSubtract = Duration.millis(3000.0);
			mp.seek(currentTime.subtract(toSubtract));
		});
		seekButtons.add(btnBack30);

		Button btnBack10 = new Button();
		btnBack10.setGraphic(iconProvider.getIcon(IconProvider.BACKWARD_10, btnSize));
		btnBack10.setOnAction(e -> {
			MediaPlayer mp = mediaView.getMediaPlayer();
			Duration currentTime = mp.getCurrentTime();
			Duration toSubtract = Duration.millis(500.0);
			mp.seek(currentTime.subtract(toSubtract));
		});
		seekButtons.add(btnBack10);

		Button btnBack5 = new Button();
		btnBack5.setGraphic(iconProvider.getIcon(IconProvider.BACKWARD_5, btnSize));
		btnBack5.setOnAction(e -> {
			MediaPlayer mp = mediaView.getMediaPlayer();
			Duration currentTime = mp.getCurrentTime();
			Duration toSubtract = Duration.millis(100.0);
			mp.seek(currentTime.subtract(toSubtract));
		});
		seekButtons.add(btnBack5);

		Button btnFor5 = new Button();
		btnFor5.setGraphic(iconProvider.getIcon(IconProvider.FORWARD_5, btnSize));
		btnFor5.setOnAction(e -> {
			MediaPlayer mp = mediaView.getMediaPlayer();
			Duration currentTime = mp.getCurrentTime();
			Duration toAdd = Duration.millis(100.0);
			mp.seek(currentTime.add(toAdd));
		});
		seekButtons.add(btnFor5);

		Button btnFor10 = new Button();
		btnFor10.setGraphic(iconProvider.getIcon(IconProvider.FORWARD_10, btnSize));
		btnFor10.setOnAction(e -> {
			MediaPlayer mp = mediaView.getMediaPlayer();
			Duration currentTime = mp.getCurrentTime();
			Duration toAdd = Duration.millis(500.0);
			mp.seek(currentTime.add(toAdd));
		});
		seekButtons.add(btnFor10);

		Button btnFor30 = new Button();
		btnFor30.setGraphic(iconProvider.getIcon(IconProvider.FORWARD_30, btnSize));
		btnFor30.setOnAction(e -> {
			MediaPlayer mp = mediaView.getMediaPlayer();
			Duration currentTime = mp.getCurrentTime();
			Duration toAdd = Duration.millis(3000.0);
			mp.seek(currentTime.add(toAdd));
		});
		seekButtons.add(btnFor30);

		return seekButtons;
	}

	private void updateVolumeIcon() {
		int vol = (int)(100 * mediaView.getMediaPlayer().getVolume());
		Image img;
		if(vol == 0) 		img = iconProvider.getIconImage(IconProvider.VOLUME_MUTE);
		else if(vol <= 30) 	img = iconProvider.getIconImage(IconProvider.VOLUME_LOW);
		else if(vol <= 70) 	img = iconProvider.getIconImage(IconProvider.VOLUME_MIDDLE);
		else 			 	img = iconProvider.getIconImage(IconProvider.VOLUME_HIGH);
		ivVolume.setImage(img);
		updateValues();
	}

	private void initMediaBarBindings(ImageView ivPlayPause) {
		MediaPlayer mediaPlayer = mediaView.getMediaPlayer();

		Image imgPlay = iconProvider.getIconImage(IconProvider.PLAY);
		Image imgPause = iconProvider.getIconImage(IconProvider.PAUSE);

		// play button
		btnPlay.setOnAction(event -> {
			MediaPlayer.Status status = mediaPlayer.getStatus();
			logger.trace("pressed button PLAY  -  player status: {},  terminated media: {}", status, isMediaTerminated);

			if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
				return;
			}

			boolean isSetPauseImage = false;
			if (isMediaTerminated) {
				mediaPlayer.seek(mediaPlayer.getStartTime());
				isMediaTerminated = false;
				status = MediaPlayer.Status.PAUSED;
				isSetPauseImage = true;
			}

			if (status == MediaPlayer.Status.PAUSED
					|| status == MediaPlayer.Status.READY
					|| status == MediaPlayer.Status.STOPPED) {
				// rewind the movie if we're sitting at the end
				mediaPlayer.play();
				if(isSetPauseImage) {
					ivPlayPause.setImage(imgPause);
				}
			} else {
				mediaPlayer.pause();
			}
		});

		// MediaPlayer events
		mediaPlayer.setOnReady(() -> {
			logger.trace("player event: READY");
			updateValues();
		});
		mediaPlayer.setOnPlaying(() -> {
			logger.trace("player event: PLAYING");
			ivPlayPause.setImage(imgPause);
		});
		mediaPlayer.setOnPaused(() -> {
			logger.trace("player event: PAUSE");
			ivPlayPause.setImage(imgPlay);
		});
		mediaPlayer.setOnEndOfMedia(() -> {
			logger.trace("player event: END OF MEDIA");
			isMediaTerminated = true;
			mediaPlayer.pause();
			ivPlayPause.setImage(imgPlay);
		});
		mediaPlayer.currentTimeProperty().addListener(ov -> updateValues());

		// time slider
		sliderTime.setOnMouseClicked(event -> {
			sliderTime.setValueChanging(true);
			double value = (event.getX() / sliderTime.getWidth()) * sliderTime.getMax();
			sliderTime.setValue(value);
			sliderTime.setValueChanging(false);
		});
		sliderTime.valueProperty().addListener(ov -> {
			if(sliderTime.isValueChanging()) {
				Duration seek = mediaPlayer.getMedia().getDuration().multiply(sliderTime.getValue() / 100.0);
				mediaPlayer.seek(seek);
			}
		});

		// volume slider
		sliderVolume.setOnMouseClicked(event -> {
			sliderVolume.setValueChanging(true);
			double value = (event.getX() / sliderVolume.getWidth()) * sliderVolume.getMax();
			sliderVolume.setValue(value);
			sliderVolume.setValueChanging(false);
		});
		sliderVolume.valueProperty().addListener(ov -> {
			if (sliderVolume.isValueChanging()) {
				double vol = sliderVolume.getValue();
				mediaPlayer.setVolume(vol / 100.0);
				lblVolume.setText(((int) vol) + "%");
				updateVolumeIcon();
			}
		});
	}

	private Pane createBookmarkPane(F1Video f1Video) {
		MediaPlayer mediaPlayer = mediaView.getMediaPlayer();

		ObservableList<JkDuration> bookmarks = FXCollections.observableArrayList(new ArrayList<>());
		SortedList<JkDuration> sortedList = new SortedList<>(bookmarks, JkDuration::compareTo);

		BorderPane bookmarkPane = new BorderPane();
		bookmarkPane.getStyleClass().add("bookmarkPane");

		Button btnMark = new Button("MARK");
		btnMark.setOnAction(e -> {
			JkDuration btime = JkDuration.of(mediaView.getMediaPlayer().getCurrentTime());
			if(bookmarks.contains(btime)) {
				bookmarks.remove(btime);
				f1Video.getMarks().remove(btime);
			} else {
				bookmarks.add(btime);
				f1Video.getMarks().add(btime);
			}
		});
		HBox headerBox = createHBox("subBox headerBox", btnMark);
		bookmarkPane.setTop(headerBox);

		GridPane gridPane = new GridPane();
		bookmarks.addListener((ListChangeListener<JkDuration>) c -> {
			JfxGridPaneBuilder gpBuilder = new JfxGridPaneBuilder();
			AtomicInteger rowNum = new AtomicInteger(-1);
			sortedList.forEach(b -> {
				rowNum.incrementAndGet();
				gpBuilder.add(rowNum.get(), 0, String.valueOf(rowNum.get()+1));
				gpBuilder.add(rowNum.get(), 1, b.toStringElapsed(ChronoUnit.MINUTES));
				Button seek = new Button("SEEK");
				seek.setOnAction(e -> mediaPlayer.seek(b.toFxDuration()));
				gpBuilder.add(rowNum.get(), 2, seek);
			});
			gpBuilder.createGridPane(gridPane);
		});
		bookmarks.setAll(f1Video.getMarks());
		ScrollPane scrollPane = new ScrollPane(gridPane);
		gridPane.widthProperty().addListener(obs -> scrollPane.setPrefWidth(gridPane.getWidth() + 30));
		HBox gpBox = createHBox("subBox centerBox", scrollPane);
		bookmarkPane.setCenter(gpBox);

		return bookmarkPane;
	}

	private void updateValues() {
		Platform.runLater(
			() -> {
				MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
				if(mediaPlayer != null) {
					Duration currentTime = mediaPlayer.getCurrentTime();
					Duration totTime = mediaPlayer.getTotalDuration();
                    JkDuration of = JkDuration.of((long) currentTime.toMillis());
                    lblActualTime.setText(of.toStringElapsed(true, ChronoUnit.MINUTES));
                    if (!sliderTime.isValueChanging()) {
                        Duration divided = currentTime.divide(totTime.toMillis());
                        sliderTime.setValue(divided.toMillis() * 100.0);
                    }
                    if (!sliderVolume.isValueChanging()) {
                        sliderVolume.setValue((int) Math.round(mediaPlayer.getVolume() * 100.0));
                        lblVolume.setText(((int) sliderVolume.getValue()) + "%");
                    }
                }
			}
		);
	}

}
