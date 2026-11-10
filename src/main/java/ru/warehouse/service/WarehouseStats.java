package ru.warehouse.service;

public record WarehouseStats(long total, long inStock) {

    public long getShipped() {
        return total - inStock;
    }
}
