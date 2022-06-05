package com.babel.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author implements Serializable {
    //----
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private final List<AuthorItemRelationship> authorItemRelationshipList = new ArrayList<>();
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "ID_SEQ")
    private Integer id; //Restrict set
    @Column(name = "name")
    private String name;

    //------


    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
