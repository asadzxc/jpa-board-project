package com.example.start.entity;

import com.example.start.enums.ReactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter @Setter
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    @Enumerated(EnumType.STRING)
    private ReactionType type;


}
