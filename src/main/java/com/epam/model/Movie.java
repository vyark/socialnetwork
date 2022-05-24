package com.epam.model;

import javax.persistence.*;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String year;
    private String description;
    private Boolean isWatched;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;
}
