package pl.training.performance.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@EqualsAndHashCode(exclude = "post")
@ToString(exclude = "post")
@Data
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

}
