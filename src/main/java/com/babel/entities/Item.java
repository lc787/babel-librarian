package com.babel.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
public class Item implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private final List<AuthorItemRelationship> authorItemRelationshipList = new ArrayList<>();
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "ID_SEQ")
    private Integer id; //Restrict set
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    @ManyToOne
    private ItemType type;
    /**
     * Relative path from working directory to item
     */
    @Column(name = "file_path")
    private String filePath;
    /**
     * Describes ownership of item
     */
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User user; //Restrict set
    //TODO DO i break this by not specifying the book at the constructor level..?
    //-------------------------
    @OneToOne(mappedBy = "item")
    private Book book; //Restrict set ?


    public Item() {
    }

    public Item(ItemType type, String filePath, User user) {
        this.type = type;
        this.filePath = filePath;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }


    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}