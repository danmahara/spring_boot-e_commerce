package com.ecommerce.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dtos.response.AddressResponse;
import com.ecommerce.models.User;
import com.ecommerce.models.UserAddress;
import com.ecommerce.models.UserAddress.AddressType;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.requests.AddressRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    /**
     * Get all active addresses for a user
     */
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserIdAndIsActiveTrueOrderByIsDefaultDescCreatedDateDesc(userId)
                .stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get address by ID for a specific user
     */
    public AddressResponse getAddressById(Long addressId, Long userId) {
        UserAddress address = addressRepository.findByIdAndUserIdAndIsActiveTrue(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return AddressResponse.fromEntity(address);
    }

    /**
     * Get default address for a user
     */
    public AddressResponse getDefaultAddress(Long userId) {
        return addressRepository.findByUserIdAndIsDefaultTrueAndIsActiveTrue(userId)
                .map(AddressResponse::fromEntity)
                .orElse(null);
    }

    /**
     * Create a new address
     */
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If this is the first address or marked as default, clear other defaults
        if (request.getIsDefault() != null && request.getIsDefault()) {
            addressRepository.clearDefaultAddressForUser(userId);
        }

        // If this is the first address, make it default
        boolean isFirstAddress = addressRepository.countByUserId(userId) == 0;

        UserAddress address = UserAddress.builder()
                .user(user)
                .addressType(request.getAddressType() != null ? request.getAddressType() : AddressType.SHIPPING)
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null ? request.getCountry() : "USA")
                .phone(request.getPhone())
                .isDefault(isFirstAddress || (request.getIsDefault() != null && request.getIsDefault()))
                .isActive(true)
                .build();

        UserAddress savedAddress = addressRepository.save(address);
        return AddressResponse.fromEntity(savedAddress);
    }

    /**
     * Update an existing address
     */
    @Transactional
    public AddressResponse updateAddress(Long addressId, Long userId, AddressRequest request) {
        UserAddress address = addressRepository.findByIdAndUserIdAndIsActiveTrue(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // If setting as default, clear other defaults first
        if (request.getIsDefault() != null && request.getIsDefault() && !address.getIsDefault()) {
            addressRepository.clearDefaultAddressForUser(userId);
        }

        if (request.getAddressType() != null) {
            address.setAddressType(request.getAddressType());
        }
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }
        address.setPhone(request.getPhone());

        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }

        UserAddress updatedAddress = addressRepository.save(address);
        return AddressResponse.fromEntity(updatedAddress);
    }

    /**
     * Delete an address (soft delete)
     */
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        UserAddress address = addressRepository.findByIdAndUserIdAndIsActiveTrue(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        boolean wasDefault = address.getIsDefault();

        // Soft delete
        address.setIsActive(false);
        address.setIsDefault(false);
        addressRepository.save(address);

        // If deleted address was default, set another one as default
        if (wasDefault) {
            List<UserAddress> remainingAddresses = addressRepository
                    .findByUserIdAndIsActiveTrueOrderByIsDefaultDescCreatedDateDesc(userId);
            if (!remainingAddresses.isEmpty()) {
                UserAddress newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    /**
     * Set an address as default
     */
    @Transactional
    public AddressResponse setDefaultAddress(Long addressId, Long userId) {
        UserAddress address = addressRepository.findByIdAndUserIdAndIsActiveTrue(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressRepository.clearDefaultAddressForUser(userId);
        address.setIsDefault(true);

        UserAddress updatedAddress = addressRepository.save(address);
        return AddressResponse.fromEntity(updatedAddress);
    }

    /**
     * Get address entity by ID (for internal use)
     */
    public UserAddress getAddressEntityById(Long addressId, Long userId) {
        return addressRepository.findByIdAndUserIdAndIsActiveTrue(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }
}