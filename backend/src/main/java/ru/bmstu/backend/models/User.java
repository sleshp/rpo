package ru.bmstu.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private int id;
    @Column(unique = true, nullable = false)
    private String login;
    @JsonIgnore
    private String password;
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String salt;
    @JsonIgnore
    private String token;
    private LocalDateTime activity;

    @ManyToMany(mappedBy = "users")
    private Set<Museum> museums = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getActivity() {
        return activity;
    }

    public void setActivity(LocalDateTime activity) {
        this.activity = activity;
    }

    public Set<Museum> getMuseums() {
        return museums;
    }

    public void setMuseums(Set<Museum> museums) {
        this.museums = museums;
    }

    public void addMuseum(Museum museum) {
        this.museums.add(museum);
        museum.getUsers().add(this);
    }
    public void removeMuseum(Museum museum) {
        this.museums.remove(museum);
        museum.getUsers().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
