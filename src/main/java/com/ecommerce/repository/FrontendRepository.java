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

    // List<Page> findByStatusTrueTemplateNameInAndType(List<String> templateName,
    // String type);

    // List<Page> findByTemplateNameInAndStatus(List<String> templateName, byte
    // status);

    @Query("SELECT p FROM Page p WHERE p.status = true")
    List<Page> findAllActivePages();

}
