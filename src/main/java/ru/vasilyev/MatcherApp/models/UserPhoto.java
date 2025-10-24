package ru.vasilyev.MatcherApp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users_photo")
@Data
@NoArgsConstructor
public class UserPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    public UserPhoto(String photoUrl, String fileName, Long fileSize, User user, Boolean isPrimary) {
        this.photoUrl = photoUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.uploadedAt = LocalDateTime.now();
        this.user = user;
        this.isPrimary = isPrimary;
    }
}
