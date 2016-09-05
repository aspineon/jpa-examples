package pl.training.performance.entity;

import javax.persistence.*;

@Table(name = "post_comment")
@Entity
public class PostComment {

    @GeneratedValue
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @Lob
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
