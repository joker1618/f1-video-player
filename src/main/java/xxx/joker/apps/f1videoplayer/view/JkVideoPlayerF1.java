package xxx.joker.apps.f1videoplayer.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.f1videoplayer.model.VideoModel;
import xxx.joker.apps.f1videoplayer.model.VideoModelImpl;
import xxx.joker.apps.f1videoplayer.model.entities.F1Video;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkVideoPlayerF1 extends BorderPane {

	private static Logger logger = LoggerFactory.getLogger(JkVideoPlayerF1.class);

	private VideoModel model = VideoModelImpl.getModel();
	private F1Video f1Video;
	private Path videoPath;

	private MediaView mediaView;

	private Button btnPlay;
	private Label lblTotalTime;
	private Label lblActualTime;
	private Slider sliderTime;
	private Slider sliderVolume;
	private Label lblVolume;

	private boolean isMediaTerminated;
	private boolean isAlreadyStarted = false;

	private ObservableList<JkDuration> bookmarks;

	private List<Double> rateList = Arrays.asList(0.1, 0.3, 0.5, 1.0, 2.0, 5.0);

	public JkVideoPlayerF1(Path videoPath) {
		this.videoPath = videoPath;
		this.f1Video = model.getOrAddF1Video(videoPath);

		setTop(createTopPane());
		setCenter(createMediaViewPane());
		setBottom(createMediaBarPane());
		setRight(createBookmarkPane());

		getChildren().forEach(ch -> ch.getStyleClass().add("subPane"));

		getStyleClass().add("borderedRoot");
		getStylesheets().add(getClass().getResource("/css/JkVideoPlayerF1.css").toExternalForm());

	}

	public void closePlayer() {
		MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
		if(mediaPlayer.getStatus() != MediaPlayer.Status.DISPOSED) {
			logger.trace("closing video player for {}", JkFiles.getFileName(videoPath));
			mediaPlayer.stop();
			mediaPlayer.dispose();
			model.commit();
		}
	}

	public Path getVideoPath() {
		return videoPath;
	}

	public List<JkDuration> getBookmarks() {
		return bookmarks;
	}

	private Pane createTopPane() {
		Label lblHeading = new Label(strf("{}", videoPath.getFileName()));

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
		ChangeListener<Number> fitListener = (obs, old, nez) -> fitMediaViewPane(mvPane);
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
            }
		});

		return mvPane;
	}

	private void fitMediaViewPane(Pane mvPane) {
		if(videoPath == null || mediaView == null)	return;

		MediaPlayer mplayer = mediaView.getMediaPlayer();
		if(mplayer == null)		return;

		Media media = mplayer.getMedia();
		if(media == null)	return;

		int wmedia = media.getWidth();
		int hmedia = media.getHeight();
		if(wmedia <= 0 || hmedia <= 0)	return;
		double wpane = mvPane.getWidth();
		double hpane = mvPane.getHeight();
		if(wpane <= 0d || hpane <= 0d)	return;

		double videoFormat = (double) wmedia / hmedia;
		double paneFormat = wpane / hpane;
		double fitWidth = (paneFormat < videoFormat) ? wpane : (hpane * videoFormat);
		mediaView.setFitWidth(fitWidth);
	}
	private Pane createMediaBarPane() {
		MediaPlayer mediaPlayer = mediaView.getMediaPlayer();

		HBox mediaBar = new HBox();
		mediaBar.getStyleClass().add("mediaBarPane");
		BorderPane.setAlignment(mediaBar, Pos.CENTER);

		// Play button
		btnPlay = new Button(">");
		HBox hboxPlay = new HBox(btnPlay);
		hboxPlay.getStyleClass().add("barBox");
		mediaBar.getChildren().add(hboxPlay);

		// Label for time progress
		lblTotalTime = new Label("");
		lblActualTime = new Label("");

		// time slider
		sliderTime = new Slider();
		HBox.setHgrow(sliderTime, Priority.ALWAYS);
		sliderTime.setMinWidth(50);
		sliderTime.setMaxWidth(Double.MAX_VALUE);

		// Label for total time
		HBox hboxTime = new HBox(lblActualTime, sliderTime, lblTotalTime);
		hboxTime.getStyleClass().add("barBox");
		HBox.setHgrow(hboxTime, Priority.ALWAYS);
		mediaBar.getChildren().add(hboxTime);

		// Reproduction rate
		Button btnPrevious = new Button("-");
		btnPrevious.setOnAction(e -> setVideoRate(true));
		Button btnNext = new Button("+");
		btnNext.setOnAction(e -> setVideoRate(false));
		Label lblRate = new Label("1.0");
		mediaPlayer.rateProperty().addListener((observable, oldValue, newValue) -> lblRate.setText(newValue.toString()));
		HBox hboxRate = new HBox(btnPrevious, lblRate, btnNext);
		hboxRate.getStyleClass().add("barBoxSmallBtn");
		mediaBar.getChildren().add(hboxRate);

		// Go-back
		Button btnBack01 = new Button("<");
		Button btnBack05 = new Button("<<");
		Button btnBack2 = new Button("<<<");
		btnBack01.setOnAction(e -> mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(0.1))));
		btnBack05.setOnAction(e -> mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(0.5))));
		btnBack2.setOnAction(e ->  mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(2))));
		HBox hbBack = new HBox(btnBack01, btnBack05, btnBack2);
		hbBack.getStyleClass().addAll("seekBox");
		mediaBar.getChildren().add(hbBack);

		Button btnForw01 = new Button(">");
		Button btnForw05 = new Button(">>");
		Button btnForw2 = new Button(">>>");
		btnForw01.setOnAction(e -> mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(0.1))));
		btnForw05.setOnAction(e -> mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(0.5))));
		btnForw2.setOnAction(e ->  mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(2))));
		HBox hbForw = new HBox(btnForw2, btnForw05, btnForw01);
		hbForw.getStyleClass().addAll("seekBox");
		mediaBar.getChildren().add(hbForw);

		// volume slider
		sliderVolume = new Slider();
		sliderVolume.setPrefWidth(100);
		sliderVolume.setMaxWidth(Region.USE_PREF_SIZE);
		sliderVolume.setMinWidth(30);

		// label for volume %
		lblVolume = new Label("");

		HBox hboxVol = new HBox(sliderVolume, lblVolume);
		hboxVol.getStyleClass().add("barBox");
		mediaBar.getChildren().add(hboxVol);

		initMediaBarBindings();

		return mediaBar;
	}
	private void setVideoRate(boolean slower) {
		double actualRate = mediaView.getMediaPlayer().getRate();
		int idx = rateList.indexOf(actualRate);
		Double newRate = null;
		if(idx > 0 && slower) {
			newRate = rateList.get(idx-1);
		} else if(!slower && idx < rateList.size()-1) {
			newRate = rateList.get(idx+1);
		}
		if(newRate != null) {
			mediaView.getMediaPlayer().setRate(newRate);
		}
	}
	private void initMediaBarBindings() {
		MediaPlayer mediaPlayer = mediaView.getMediaPlayer();

		// play button
		btnPlay.setOnAction(event -> {
			logger.trace("button PLAY action");

			MediaPlayer.Status status = mediaPlayer.getStatus();
			logger.trace("player status:{},  terminated media: {}", status, isMediaTerminated);

			if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
				return;
			}

			if (isMediaTerminated) {
				mediaPlayer.seek(mediaPlayer.getStartTime());
				isMediaTerminated = false;
				status = MediaPlayer.Status.PAUSED;
			}

			if (status == MediaPlayer.Status.PAUSED
					|| status == MediaPlayer.Status.READY
					|| status == MediaPlayer.Status.STOPPED) {
				// rewind the movie if we're sitting at the end
				mediaPlayer.play();
				btnPlay.setText("||");
			} else {
				mediaPlayer.pause();
				btnPlay.setText(">");
			}
		});


		// MediaPlayer events
		mediaPlayer.setOnReady(() -> {
			logger.trace("player event: READY");
			updateValues();
		});
		mediaPlayer.setOnPlaying(() -> {
			logger.trace("player event: PLAYING");
			btnPlay.setText("||");
		});
		mediaPlayer.setOnPaused(() -> {
			logger.trace("player event: PAUSE");
			btnPlay.setText(">");
		});
		mediaPlayer.setOnEndOfMedia(() -> {
			logger.trace("player event: END OF MEDIA");
			isMediaTerminated = true;
			mediaPlayer.pause();
			btnPlay.setText("R");
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
				mediaPlayer.setVolume(sliderVolume.getValue() / 100.0);
				lblVolume.setText(((int) sliderVolume.getValue()) + "%");
			}
		});
	}

	private void updateValues() {
		Platform.runLater(
			() -> {
				logger.trace("updating values");
				MediaPlayer mp = mediaView.getMediaPlayer();
				JkDuration currentTime = JkDuration.of(mp.getCurrentTime());
				lblActualTime.setText(currentTime.toStringElapsed(ChronoUnit.MINUTES));
				JkDuration totalTime = JkDuration.of(mp.getTotalDuration());
				if(!isAlreadyStarted) {
					isAlreadyStarted = true;
					fitMediaViewPane((Pane)getCenter());
					lblTotalTime.setText(totalTime.toStringElapsed(ChronoUnit.MINUTES));
				}
				if (!sliderTime.isValueChanging()) {
					double divided = (double) currentTime.toMillis() / totalTime.toMillis();
					sliderTime.setValue(divided * 100.0);
				}
				if (!sliderVolume.isValueChanging()) {
					sliderVolume.setValue((int) Math.round(mp.getVolume() * 100.0));
					lblVolume.setText(((int) sliderVolume.getValue()) + "%");
				}
			}
		);
	}

	private Pane createBookmarkPane() {
		MediaPlayer mediaPlayer = mediaView.getMediaPlayer();

		bookmarks = FXCollections.observableArrayList(new ArrayList<>());
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
		HBox headerBox = new HBox(btnMark);
		headerBox.getStyleClass().addAll("subBox", "headerBox");
		bookmarkPane.setTop(headerBox);

		GridPane gridPane = new GridPane();
		bookmarks.addListener((ListChangeListener<JkDuration>)c -> {
			GridPaneBuilder gpBuilder = new GridPaneBuilder();
			AtomicInteger rowNum = new AtomicInteger(-1);
			sortedList.forEach(b -> {
				rowNum.incrementAndGet();
				gpBuilder.add(rowNum.get(), 0, rowNum.get()+1);
				gpBuilder.add(rowNum.get(), 1, b.toStringElapsed(ChronoUnit.MINUTES));
				Button seek = new Button("seek");
				seek.setOnAction(e -> mediaPlayer.seek(b.toFxDuration()));
				gpBuilder.add(rowNum.get(), 2, seek);
			});
			gpBuilder.createGridPane(gridPane);
		});
		bookmarks.setAll(f1Video.getMarks());
		HBox gpBox = new HBox(new ScrollPane(gridPane));
//		HBox gpBox = new HBox(gridPane);
		gpBox.getStyleClass().addAll("subBox", "centerBox");
//		bookmarkPane.setCenter(new ScrollPane(gpBox));
		bookmarkPane.setCenter(gpBox);

		return bookmarkPane;
	}
}
