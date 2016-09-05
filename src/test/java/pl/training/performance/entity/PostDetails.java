package pl.training.performance.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class PostDetails {

    @GeneratedValue
    @Id
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date created = new Date();
    @Column(name = "created_by")
    private String createdBy;
    @OneToOne(fetch = FetchType.LAZY)
    private Post post;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
