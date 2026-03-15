package com.SE.final_project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_item_relations")
public class UserItemRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Enumerated(EnumType.STRING)
    private RelationType relationType;

    public UserItemRelation() {}

    public UserItemRelation(User user, Item item, RelationType relationType) {
        this.user = user;
        this.item = item;
        this.relationType = relationType;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Item getItem() {
        return item;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }
}