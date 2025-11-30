package com.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.repository.FrontendRepository;

@Service
public class FrontendService {

    @Autowired
    FrontendRepository frontendRepository;

    

}
