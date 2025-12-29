package com.ecommerce.controllers.admin.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.response.AddressResponse;
import com.ecommerce.dtos.response.ApiResponse;
import com.ecommerce.models.User;
import com.ecommerce.models.UserAddress;
import com.ecommerce.requests.AddressRequest;
import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.services.AddressService;
import com.ecommerce.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressRestController {

    private final AddressService addressService;

    /**
     * Get all addresses for the authenticated user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAllAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<AddressResponse> addresses = addressService.getAddressesByUserId(userDetails.getId());
            return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve addresses", e.getMessage()));
        }
    }

    /**
     * Get a specific address by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            AddressResponse address = addressService.getAddressById(id, userDetails.getId());
            return ResponseEntity.ok(ApiResponse.success("Address retrieved successfully", address));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve address", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddressByUserId(@PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user) {

        try {
            List<AddressResponse> userAddresses = addressService.getAddressesByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Address Fethed successfully", userAddresses));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch address", e.getMessage()));

        }

    }

    /**
     * Get default address for the user
     */
    @GetMapping("/default")
    public ResponseEntity<ApiResponse<AddressResponse>> getDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            AddressResponse address = addressService.getDefaultAddress(userDetails.getId());
            if (address == null) {
                return ResponseEntity.ok(ApiResponse.success("No default address found", null));
            }
            return ResponseEntity.ok(ApiResponse.success("Default address retrieved successfully", address));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve default address", e.getMessage()));
        }
    }

    /**
     * Create a new address
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            AddressResponse address = addressService.createAddress(userDetails.getId(), request);
            return ResponseEntity.ok(ApiResponse.success("Address created successfully", address));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create address", e.getMessage()));
        }
    }

    /**
     * Update an existing address
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            AddressResponse address = addressService.updateAddress(id, userDetails.getId(), request);
            return ResponseEntity.ok(ApiResponse.success("Address updated successfully", address));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update address", e.getMessage()));
        }
    }

    /**
     * Delete an address
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            addressService.deleteAddress(id, userDetails.getId());
            return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete address", e.getMessage()));
        }
    }

    /**
     * Set an address as default
     */
    @PutMapping("/{id}/set-default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            AddressResponse address = addressService.setDefaultAddress(id, userDetails.getId());
            return ResponseEntity.ok(ApiResponse.success("Default address updated successfully", address));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to set default address", e.getMessage()));
        }
    }
}