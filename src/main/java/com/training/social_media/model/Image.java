package com.training.social_media.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "image")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Image implements Serializable {

    private static final long serialVersionUID = 3906771677381811334L;

    @Id
    @SequenceGenerator(name = "imageIdSeq", sequenceName = "image_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "imageIdSeq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "file")
    @Type(type = "org.hibernate.type.BinaryType")
    @ToString.Exclude
    @JsonIgnore
    private byte[] file;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    @ToString.Exclude
    @JsonIgnore
    private Post post;

    public Image(String name, byte[] file) {
        this.name = name;
        this.file = file;
    }
}
