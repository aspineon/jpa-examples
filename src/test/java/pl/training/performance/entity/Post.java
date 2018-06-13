package pl.training.performance.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@org.hibernate.annotations.Cache(region = "training", usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Data
public class Post {

    @GeneratedValue
    @Id
    private Long id;
    @NonNull
    private String title;
    @NonNull
    private String text;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "post")
    private List<PostComment> comments = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "post")
    private PostDetails postDetails;
    @ManyToMany
    private List<Tag> tags = new ArrayList<>();
    //@Version
    //private long version;

    @PostPersist
    public void onPersist() {
        System.out.println("Post persisted");
    }

}
