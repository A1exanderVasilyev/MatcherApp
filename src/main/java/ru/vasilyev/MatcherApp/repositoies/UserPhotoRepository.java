package ru.vasilyev.MatcherApp.repositoies;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vasilyev.MatcherApp.models.UserPhoto;

import java.util.List;

public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    List<UserPhoto> findByUserIdOrderByUploadedAtDesc(long userId);

    UserPhoto findByUserIdAndIsPrimaryTrue(long userId);
}
