package com.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.models.Page;

public interface FrontendRepository extends JpaRepository<Page, Long> {

    Optional<Page> findBySlug(String slug);

    List<Page> findByStatus(boolean status);

    List<Page> findByStatusTrueOrderByOrderAsc();

    List<Page> findByTemplateNameInAndStatusTrue(List<String> templateNames);

    List<Page> findByTemplateNameAndStatusTrue(String templateName);

    @Query("SELECT p FROM Page p WHERE p.status = true")
    List<Page> findAllActivePages();

}
