package com.ecommerce.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.admin.SiteSetting;

public interface SettingRepository extends JpaRepository<SiteSetting, Long> {

}
