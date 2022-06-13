package com.epam.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.util.Date;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String text;
    private Date creationDate;
    @ManyToOne
    @JoinColumn(name = "sender")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private User receiver;
}
