package ua.pumb.animals.enums;

public enum Category {
    LOW_PRICE("LOW_PRICE"),
    MEDIUM_PRICE("MEDIUM_PRICE"),
    HIGH_PRICE("HIGH_PRICE"),
    PREMIUM_PRICE("PREMIUM_PRICE");

    private String category;

    Category(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
