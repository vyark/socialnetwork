package com.epam.model;

import javax.persistence.*;

@Entity
public class AudioTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String genre;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;
}
