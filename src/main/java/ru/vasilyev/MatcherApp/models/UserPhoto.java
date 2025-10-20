package ru.vasilyev.MatcherApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users_photo")
public class UserPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotEmpty
    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    public UserPhoto() {
    }

    public UserPhoto(String photoUrl, LocalDateTime uploadedAt, User user, Boolean isPrimary) {
        this.photoUrl = photoUrl;
        this.uploadedAt = uploadedAt;
        this.user = user;
        this.isPrimary = isPrimary;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    @Override
    public String toString() {
        return "UserPhoto{" +
                "id=" + id +
                ", photoUrl='" + photoUrl + '\'' +
                ", uploadedAt=" + uploadedAt +
                ", user=" + user +
                ", isPrimary=" + isPrimary +
                '}';
    }
}
