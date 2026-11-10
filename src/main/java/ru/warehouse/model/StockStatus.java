package ru.warehouse.model;

public enum StockStatus {
    ALL("Все"),
    IN_STOCK("На складе"),
    SHIPPED("Вывезена");

    private final String label;

    StockStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
