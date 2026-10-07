package ru.library.service;

public record LibraryStats(long total, long onHand) {

    public long getReturned() {
        return total - onHand;
    }
}
