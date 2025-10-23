package ru.vasilyev.MatcherApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vasilyev.MatcherApp.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 100, message = "Username should be between 2 and 100 characters")
    @Column(name = "username")
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


    @Column(name = "latitude")
    private Double latitude;


    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "role")
    private String role;

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
        this.role = "ROLE_USER";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addPhoto(UserPhoto userPhoto) {
        photos.add(userPhoto);
        userPhoto.setUser(this);
    }
}
