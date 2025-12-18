package com.ecommerce.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.admin.Page;

public interface DashboardRepository extends JpaRepository<Page, Long> {

    // long countByTemplateNameNot(String templateName);
    // long countByTemplateName(String templateName);
    long countByTemplateNameIn(List<String> templateName);

}
