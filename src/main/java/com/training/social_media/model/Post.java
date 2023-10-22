package com.training.social_media.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Post implements Serializable {

    private static final long serialVersionUID = 3906771677381811334L;

    @Id
    @SequenceGenerator(name = "postIdSeq", sequenceName = "post_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postIdSeq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "header")
    private String header;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @JsonIgnore
    private User user;
}
