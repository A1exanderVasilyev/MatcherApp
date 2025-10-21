package ru.vasilyev.MatcherApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vasilyev.MatcherApp.enums.Gender;
import ru.vasilyev.MatcherApp.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 100, message = "Username should be between 2 and 100 characters")
    @Column(name = "username", unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(name = "password")
    private String password;

    @NotNull(message = "Date of birth is required")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Country is required")
    @Column(name = "country")
    private String country;

    @NotBlank(message = "City is required")
    @Column(name = "city")
    private String city;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    @Column(name = "latitude")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPhoto> photos = new ArrayList<>();

    public User(String username, String password, LocalDate dateOfBirth, Gender gender, String email, String country, String city, Double latitude, Double longitude) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.country = country;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.role = UserRole.USER;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addPhoto(UserPhoto userPhoto) {
        photos.add(userPhoto);
        userPhoto.setUser(this);
    }
}
