package com.ecommerce.enums;

public enum PageTemplate {
    ABOUT_US("about_us"),
    CONTACT_US("contact_us"),
    TERMS("terms"),
    PRIVACY("privacy"),
    LOGIN("login"),
    REGISTER("register"),
    PRODUCT_LIST("product_list"),
    PRODUCT("product");

    private final String templateName;

    PageTemplate(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    // Optional: find enum by template string
    public static PageTemplate fromString(String templateName) {
        for (PageTemplate t : PageTemplate.values()) {
            if (t.getTemplateName().equalsIgnoreCase(templateName)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown template: " + templateName);
    }
}
