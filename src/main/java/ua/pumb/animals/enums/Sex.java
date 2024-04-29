package ua.pumb.animals.enums;

public enum Sex {
    MALE("MALE"),
    FEMALE("FEMALE");

    private String sex;

    Sex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }
}
