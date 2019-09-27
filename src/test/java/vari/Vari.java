package vari;

import org.junit.Test;
import xxx.joker.apps.f1videoplayer.dl.entities.F1Video;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.format.JkFormatter;
import xxx.joker.libs.core.runtimes.JkReflection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkConsole.displayColl;

public class Vari {

    @Test
    public void in() {
//        JkWeb.downloadResource("https://youtu.be/XCs6zHTBFBE", Paths.get("data/we.mp4"));
    }

    @Test
    public void in22() {
        List<Integer> l = new ArrayList<>();
        Class<?> aClass = l.getClass();
        display("Gen interfaces: {}", Arrays.toString(aClass.getGenericInterfaces()));
        display("Gen superclass: {}", aClass.getGenericSuperclass());
    }

    @Test
    public void repoAddChangesTry() {
        JkFormatter fmt = JkFormatter.get();
        Path fpath = Paths.get("C:\\Users\\fbarbano\\.appsFolder\\testAddRepo\\db", "vp#F1Video#jkrepo.data");
        display("FILE  {}", fpath);
        displayColl(JkFiles.readLines(fpath));
        display("\n\n\n\n\n");
        List<F1Video> videos = fmt.parseCsv(fpath, F1Video.class);
        display("VIDEOS");
        displayColl(videos, F1Video::strFull);
    }

    @Test
    public void dre() {
        Path repoFolder = Paths.get("src/test/resources/f1Repo");
        VideoModelDev model = new VideoModelDev(repoFolder, "vp");
        display(model.toStringRepo(false));
        display("\n\n##########################################################\n\n");

        JkFormatter fmt = JkFormatter.get();
        List<F1Video> fromCsvVideos = fmt.parseCsv(Paths.get("f1-updates/videosUpdate.csv"), F1Video.class);
        fromCsvVideos.forEach(v -> v.setEntityId(null));
        for (F1Video video : fromCsvVideos) {
            F1Video got = model.getByPk(video);
            if(got == null) {
                got = video;
                model.add(got);
            } else {
                JkReflection.copyFields(video, got, "marks");
            }
        }
        display(model.toStringRepo(true));

    }

    @Test
    public void renameVideos() {
//        Path baseFolder = Paths.get("video-files").toAbsolutePath();
//        Path fileToRead = Paths.get("src/test/resources/tempFile.txt");
//
//        List<Path> files = JkFiles.findFiles(baseFolder, false, p -> p.getFileName().toString().contains("TT-time-trial"));
//        files.sort(Comparator.comparing(p -> p.getFileName().toString()));
//
////        files.forEach(JkConsole::display);
//
////        JkFiles.readLines(fileToRead).forEach(JkConsole::display);
//        List<String> lines = JkFiles.readLines(fileToRead);
//        List<String> names = JkStreams.map(lines, l -> strf("2019_{}_Mercedes_setup_TT-time-trial.mp4", l));
//
//        for(int i = 0; i < files.size(); i++) {
//            Path outPath = baseFolder.resolve(names.get(i));
////            display(files.get(i));
//            if(!JkFiles.areEquals(files.get(i), outPath)) {
//                JkFiles.move(files.get(i), outPath);
//            }
//        }

        display("END");
//        JkWeb.downloadResource("https://youtu.be/XCs6zHTBFBE", Paths.get("data/we.mp4"));
    }


}
