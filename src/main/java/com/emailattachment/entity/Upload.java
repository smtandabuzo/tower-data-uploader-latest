package com.emailattachment.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="uploads")
public class Upload
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String firstName;

    @Column(nullable=false, unique=true)
    private String lastName;

    @Column(nullable=false, unique=true)
    private int accountNumber;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String address;

}
