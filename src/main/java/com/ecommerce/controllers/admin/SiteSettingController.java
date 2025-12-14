package com.ecommerce.controllers.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.requests.admin.SettingRequest;
import com.ecommerce.services.admin.SettingService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("admin/settings")
public class SiteSettingController {

    @Autowired
    SettingService settingService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("settings", settingService.findFirst());
        System.out.println("site_setting: " + settingService.findFirst());
        return "admin/setting/index";
    }

    @GetMapping("create")
    public String create() {
        return "admin/setting/create";
    }

    @GetMapping("edit")
    public String edit(Model model) {

        model.addAttribute("settings", settingService.findFirst());
        System.out.println("site_setting: " + settingService.findFirst());
        return "admin/setting/edit";
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@Valid @ModelAttribute SettingRequest request, BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // Collect field validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(),
                    error.getDefaultMessage()));
        }

        // Add manual validation for file uploads
        if (request.getImage() == null || request.getImage().isEmpty()) {
            errors.put("image", "Thumbnail image is required");
        }

        // if (request.getCoverImage() == null || request.getCoverImage().isEmpty()) {
        // errors.put("coverImage", "Cover image is required");
        // }

        // Return all errors together if any exist
        if (!errors.isEmpty()) {
            response.put("success", false);
            response.put("message", "Validation failed");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {

            settingService.save(request);
            response.put("success", true);
            response.put("message", "Setting created successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create page: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@Valid @ModelAttribute SettingRequest request, @PathVariable Long id,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // Collect field validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(),
                    error.getDefaultMessage()));
        }

        // Add manual validation for file uploads
        // if (request.getImage() == null || request.getImage().isEmpty()) {
        // errors.put("image", "Thumbnail image is required");
        // }

        // Return all errors together if any exist
        if (!errors.isEmpty()) {
            response.put("success", false);
            response.put("message", "Validation failed");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {

            settingService.update(id, request);
            response.put("success", true);
            response.put("message", "Setting Updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update setting: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
