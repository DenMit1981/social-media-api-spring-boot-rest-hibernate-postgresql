package com.training.social_media.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.training.social_media.model.enums.Role;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User implements Serializable {

    private static final long serialVersionUID = 3906771677381811334L;

    @Id
    @SequenceGenerator(name = "usersIdSeq", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersIdSeq")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "password", nullable = false, unique = true)
    @ToString.Exclude
    @JsonIgnore
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "friendships",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "friends", fetch = FetchType.LAZY)
    private Set<User> subscribers = new HashSet<>();
}
