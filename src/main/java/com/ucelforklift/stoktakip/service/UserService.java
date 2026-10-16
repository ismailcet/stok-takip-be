package com.ucelforklift.stoktakip.service;

import com.ucelforklift.stoktakip.config.CacheNames;
import com.ucelforklift.stoktakip.dto.*;
import com.ucelforklift.stoktakip.entity.User;
import com.ucelforklift.stoktakip.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Yalnizca aktif kullanicilar (silinenler hicbir yerde gorunmez). Giris ekrani her acilista cagirir. */
    @Cacheable(cacheNames = CacheNames.USERS)
    public List<UserSummary> list() {
        return userRepository.findByActiveTrue().stream()
                .map(this::toSummary)
                .toList();
    }

    @CacheEvict(cacheNames = CacheNames.USERS, allEntries = true)
    public UserSummary create(CreateUserRequest request) {
        String name = request.fullName().trim();
        // Isim benzersizligi yalnizca aktif kullanicilar arasinda; silinmis birinin adi tekrar kullanilabilir
        if (userRepository.existsByFullNameIgnoreCaseAndActiveTrue(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu isimde bir kullanici zaten var");
        }

        User user = new User();
        user.setFullName(name);
        user.setRole(request.role());
        user.setPinHash(passwordEncoder.encode(request.pin()));
        return toSummary(userRepository.save(user));
    }

    // Rol degisince JWT filtresindeki dogrulama kaydi da (o kullanici icin) temizlenir.
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.USERS, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.AUTH_USERS, key = "#id")
    })
    public UserSummary update(Long currentUserId, Long id, UpdateUserRequest request) {
        User user = findActiveOrThrow(id);
        String name = request.fullName().trim();

        if (userRepository.existsByFullNameIgnoreCaseAndIdNotAndActiveTrue(name, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu isimde bir kullanici zaten var");
        }
        if (id.equals(currentUserId) && request.role() != user.getRole()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kendi rolunuzu degistiremezsiniz");
        }

        user.setFullName(name);
        user.setRole(request.role());
        return toSummary(userRepository.save(user));
    }

    /** Silme = pasife cekme (soft delete). Bakim kaydi olsun olmasin kayit korunur. */
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.USERS, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.AUTH_USERS, key = "#id")
    })
    public void delete(Long currentUserId, Long id) {
        if (id.equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kendinizi silemezsiniz");
        }
        User user = findActiveOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public void changeOwnPin(Long userId, ChangeOwnPinRequest request) {
        User user = findActiveOrThrow(userId);
        if (!passwordEncoder.matches(request.currentPin(), user.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mevcut PIN hatali");
        }
        user.setPinHash(passwordEncoder.encode(request.newPin()));
        userRepository.save(user);
    }

    public void resetPin(Long userId, ResetPinRequest request) {
        User user = findActiveOrThrow(userId);
        user.setPinHash(passwordEncoder.encode(request.newPin()));
        userRepository.save(user);
    }

    private UserSummary toSummary(User u) {
        return new UserSummary(u.getId(), u.getFullName(), u.getRole());
    }

    private User findActiveOrThrow(Long id) {
        return userRepository.findById(id)
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanici bulunamadi"));
    }
}