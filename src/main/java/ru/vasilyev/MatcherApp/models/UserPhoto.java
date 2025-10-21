package ru.vasilyev.MatcherApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users_photo")
@Data
@NoArgsConstructor
public class UserPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotBlank(message = "Photo is required")
    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    public UserPhoto(String photoUrl, LocalDateTime uploadedAt, User user, Boolean isPrimary) {
        this.photoUrl = photoUrl;
        this.uploadedAt = uploadedAt;
        this.user = user;
        this.isPrimary = isPrimary;
    }
}
