package com.ecommerce.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.enums.PageTemplate;
import com.ecommerce.models.Page;

public interface DashboardRepository extends JpaRepository<Page, Long> {

    long countByTemplateNameNot(String templateName);

}
