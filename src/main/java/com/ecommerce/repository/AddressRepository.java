package com.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.UserAddress;

@Repository
public interface AddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findByUserIdAndIsActiveTrueOrderByIsDefaultDescCreatedDateDesc(Long userId);

    Optional<UserAddress> findByIdAndUserId(Long id, Long userId);

    Optional<UserAddress> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);

    Optional<UserAddress> findByUserIdAndIsDefaultTrueAndIsActiveTrue(Long userId);

    @Modifying
    @Query("UPDATE UserAddress a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultAddressForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM UserAddress a WHERE a.user.id = :userId AND a.isActive = true")
    long countByUserId(@Param("userId") Long userId);

    List<UserAddress> findByUserIdAndAddressTypeAndIsActiveTrue(Long userId, UserAddress.AddressType addressType);
}