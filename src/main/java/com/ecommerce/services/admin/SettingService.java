package com.ecommerce.services.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.models.admin.Image;
import com.ecommerce.models.admin.SiteSetting;
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.repository.admin.SettingRepository;
import com.ecommerce.requests.admin.SettingRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    public SiteSetting findFirst() {
        return settingRepository.findAll()
                .stream()
                .findFirst()
                .map(setting -> {
                    List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("site_setting",
                            setting.getId());
                    setting.setImages(imgs);
                    return setting; // Must return the setting
                })
                .orElseThrow(() -> new RuntimeException("Site setting not found"));
    }

    public SiteSetting save(SettingRequest request) {

        SiteSetting setting = new SiteSetting();
        setting.setTitle(request.getTitle());
        setting.setEmail(request.getEmail());
        setting.setFbLink(request.getFbLink());
        setting.setInstaLink(request.getInstaLink());
        setting.setLinkedinLink(request.getLinkedinLink());
        setting.setPhone(request.getPhone());

        SiteSetting savedSetting = settingRepository.save(setting);
        try {

            if (!request.getImage().isEmpty() && request.getImage() != null) {
                savedSetting.addFeatureImage(imageService, request.getImage());
            }

        } catch (Exception e) {
            System.out.println("Failed to store site setting image: " + e.getMessage());
            e.printStackTrace();

        }
        return savedSetting;

    }

    public SiteSetting update(Long id, SettingRequest request) {
        SiteSetting setting = settingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setting not found"));

        setting.setTitle(request.getTitle());
        setting.setEmail(request.getEmail());
        setting.setFbLink(request.getFbLink());
        setting.setInstaLink(request.getInstaLink());
        setting.setLinkedinLink(request.getLinkedinLink());
        setting.setPhone(request.getPhone());

        SiteSetting savedSetting = settingRepository.save(setting);
        try {

            if (!request.getImage().isEmpty() && request.getImage() != null) {
                savedSetting.updateFeatureImage(imageService, request.getImage());
            }

        } catch (Exception e) {
            System.out.println("Failed to update site setting image: " + e.getMessage());
            e.printStackTrace();

        }
        return savedSetting;

    }

}
