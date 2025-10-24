package ru.vasilyev.MatcherApp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vasilyev.MatcherApp.dto.UserRegistrationDTO;
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

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "country")
    private String country;

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

    public User(UserRegistrationDTO userRegistrationDTO) {
        this.username = userRegistrationDTO.getUsername();
        this.password = userRegistrationDTO.getPassword();
        this.dateOfBirth = userRegistrationDTO.getDateOfBirth();
        this.gender = userRegistrationDTO.getGender();
        this.email = userRegistrationDTO.getEmail();
        this.country = userRegistrationDTO.getCountry();
        this.city = userRegistrationDTO.getCity();
        this.role = "ROLE_USER";
        LocalDateTime creationTime = LocalDateTime.now();
        this.createdAt = creationTime;
        this.updatedAt = creationTime;
    }

    public void addPhoto(UserPhoto userPhoto) {
        photos.add(userPhoto);
        userPhoto.setUser(this);
    }
}
