package ru.library.model;

public enum LoanStatus {
    ALL("Все"),
    ON_HAND("На руках"),
    RETURNED("Сданы");

    private final String label;

    LoanStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
