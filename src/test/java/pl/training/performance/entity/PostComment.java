package pl.training.performance.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Table(name = "post_comment")
@Entity
@EqualsAndHashCode(exclude = "post")
@ToString(exclude = "post")
@Data
public class PostComment {

    @GeneratedValue
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
    @Lob
    private String text;

}
