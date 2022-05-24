package com.epam.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_sender")
    private User userSender;

    @ManyToOne
    @JoinColumn(name = "user_receiver")
    private User userReceiver;

    private Date creationDate;
}
