package com.ucelforklift.stoktakip.repository;

import com.ucelforklift.stoktakip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
