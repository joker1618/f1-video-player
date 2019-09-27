package xxx.joker.apps.f1videoplayer.v2.dl.entities;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.design.RepoField;

import java.util.Set;
import java.util.TreeSet;

public class F1Video extends RepoEntity {

    @RepoField
    private String md5;
    @RepoField
    private Set<JkDuration> marks = new TreeSet<>();

    public F1Video() {
    }

    public F1Video(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Set<JkDuration> getMarks() {
        return marks;
    }

    @Override
    public String getPrimaryKey() {
        return md5;
    }
}
