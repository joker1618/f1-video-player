package vari;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.tests.JkTests;
import xxx.joker.libs.core.utils.JkConsole;
import xxx.joker.libs.core.web.JkWeb;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkConsole.displayColl;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class Vari {

    @Test
    public void in() {
//        JkWeb.downloadResource("https://youtu.be/XCs6zHTBFBE", Paths.get("data/we.mp4"));
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
