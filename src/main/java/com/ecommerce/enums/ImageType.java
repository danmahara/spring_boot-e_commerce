package com.ecommerce.enums;

public enum ImageType {
    FEATURE("feature"),
    COVER("cover"),
    THUMBNAIL("thumbnail"),
    GALLERY("gallery"),
    BANNER("banner"),
    ICON("icon");

    private final String type;

    ImageType(String value) {
        this.type = value;
    }

    public String getValue() {
        return type;
    }

    public String getImageTypeName() {
        return type;
    }

    public static ImageType fromValue(String value) {
        for (ImageType t : ImageType.values()) {
            if (t.type.equals(value)) {
                return t;
            }
        }
        return GALLERY; // default
    }
}