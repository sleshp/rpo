package ru.bmstu.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "museums")
public class Museum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String location;

    @JsonIgnore
    @OneToMany
    private List<Painting> paintings = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "usersmuseums", joinColumns = @JoinColumn(name = "museumid"), inverseJoinColumns = @JoinColumn(name = "userid"))
    private Set<User> users = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Painting> getPaintings() {
        return paintings;
    }

    public void setPaintings(List<Painting> paintings) {
        this.paintings = paintings;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Museum museum = (Museum) o;
        return id == museum.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
