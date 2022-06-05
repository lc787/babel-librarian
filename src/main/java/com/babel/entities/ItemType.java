package com.babel.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item_types")
public class ItemType implements Serializable {
    //-------------
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type")
    private final List<Item> items = new ArrayList<>();
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "ID_SEQ")
    private Integer id; //Restrict set
    @Column(name = "type")
    private String type;
    //-------------


    public ItemType() {
    }

    public ItemType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
