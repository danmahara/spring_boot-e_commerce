package com.ecommerce.enums;

public enum PageType {
    PAGE("page"),
    PRODUCT("product");

    private final String pageName;

    PageType(String pageName) {
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }

    // Optional: find enum by template string
    public static PageType fromString(String pageName) {
        for (PageType t : PageType.values()) {
            if (t.getPageName().equalsIgnoreCase(pageName)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown template: " + pageName);
    }
}
