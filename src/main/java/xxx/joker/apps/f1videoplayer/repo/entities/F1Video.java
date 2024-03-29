package xxx.joker.apps.f1videoplayer.repo.entities;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.datalayer.design.EntityPK;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.design.EntityField;

import java.util.Set;
import java.util.TreeSet;

public class F1Video extends RepoEntity {

    @EntityPK
    @EntityField
    private String md5;
    @EntityField
    private JkDuration length;
    @EntityField
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

    public JkDuration getLength() {
        return length;
    }

    public void setLength(JkDuration length) {
        this.length = length;
    }

    public void setMarks(Set<JkDuration> marks) {
        this.marks = marks;
    }

}
