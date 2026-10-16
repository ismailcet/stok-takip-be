package com.ucelforklift.stoktakip.security;

import com.ucelforklift.stoktakip.config.CacheNames;
import com.ucelforklift.stoktakip.entity.Role;
import com.ucelforklift.stoktakip.entity.User;
import com.ucelforklift.stoktakip.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * JWT filtresinin her istekte kullandigi kullanici dogrulamasi (onbellekli).
 * Yalnizca aktif kullanicilar doner; yoksa/pasifse null (null onbellege yazilmaz).
 * Kullanici duzenlendiginde/silindiginde UserService bu kaydi temizler.
 */
@Service
public class AuthUserService {

    public record AuthUser(Long id, Role role) {
    }

    private final UserRepository userRepository;

    public AuthUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(cacheNames = CacheNames.AUTH_USERS, key = "#userId", unless = "#result == null")
    public AuthUser findActive(Long userId) {
        return userRepository.findById(userId)
                .filter(User::isActive)
                .map(u -> new AuthUser(u.getId(), u.getRole()))
                .orElse(null);
    }
}