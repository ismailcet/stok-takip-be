package com.ucelforklift.stoktakip.repository;

import com.ucelforklift.stoktakip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByActiveTrue();

    boolean existsByFullNameIgnoreCaseAndActiveTrue(String fullName);

    boolean existsByFullNameIgnoreCaseAndIdNotAndActiveTrue(String fullName, Long id);
}