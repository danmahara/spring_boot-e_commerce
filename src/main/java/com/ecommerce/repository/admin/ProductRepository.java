package com.ecommerce.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.Page;

public interface ProductRepository extends JpaRepository<Page, Long> {

    public List<Page> findByType(String type);


}
