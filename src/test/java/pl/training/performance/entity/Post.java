package pl.training.performance.entity;

import org.hibernate.annotations.*;
import sun.dc.pr.PRError;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@org.hibernate.annotations.Cache(region = "training", usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Post {

    @GeneratedValue
    @Id
    private Long id;
    private String title;
    @Lob
    private String text;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "post")
    private List<PostComment> comments = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "post")
    private PostDetails postDetails;
    @ManyToMany
    private List<Tag> tags = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<PostComment> getComments() {
        return comments;
    }

    public void setComments(List<PostComment> comments) {
        this.comments = comments;
    }

    public PostDetails getPostDetails() {
        return postDetails;
    }

    public void setPostDetails(PostDetails postDetails) {
        this.postDetails = postDetails;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
