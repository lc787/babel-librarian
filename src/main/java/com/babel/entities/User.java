package com.babel.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements Serializable {
    //-----------------
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private final List<Item> itemList = new ArrayList<>();
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "ID_SEQ")
    private Integer id;
    @Column(name = "username")
    private String username;
    /**
     * Salt. Boosts encryption.
     * https://en.wikipedia.org/wiki/Salt_(cryptography)
     */
    @Column(name = "salt")
    private String salt;
    @Column(name = "password")
    private String password;

    //-----------------
    public User() {
    }

    public User(String username, String salt, String password) {
        this.username = username;
        this.salt = salt;
        this.password = password;
    }


    //--------------------------
    public Integer getId() {
        return id;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
