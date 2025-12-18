package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.ecommerce.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
