package com.training.social_media.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.training.social_media.model.enums.Status;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_request")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 3906771677381811334L;

    @Id
    @SequenceGenerator(name = "friendRequestIdSeq", sequenceName = "friend_request_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friendRequestIdSeq")
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
}
