package com.scm.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private int rating;

    @Column(length = 2000)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id") 
    private User user;
}
