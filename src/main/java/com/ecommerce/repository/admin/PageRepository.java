package com.ecommerce.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.admin.Page;

public interface PageRepository extends JpaRepository<Page, Long> {

    public List<Page> findByStatus(byte status);

    List<Page> findByTemplateNameIn(List<String> templateNames);

    List<Page> findByTemplateNameInAndType(List<String> templateNames, String type);

    List<Page> findByTemplateNameAndType(String template, String type);

}
