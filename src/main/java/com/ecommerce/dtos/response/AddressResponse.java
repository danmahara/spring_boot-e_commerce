package com.ecommerce.dtos.response;

import com.ecommerce.models.UserAddress;
import com.ecommerce.models.UserAddress.AddressType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;
    private AddressType addressType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private Boolean isDefault;
    private Boolean isActive;
    private String fullAddress;

    public static AddressResponse fromEntity(UserAddress address) {
        return AddressResponse.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .isDefault(address.getIsDefault())
                .isActive(address.getIsActive())
                .fullAddress(address.getFullAddress())
                .build();
    }
}