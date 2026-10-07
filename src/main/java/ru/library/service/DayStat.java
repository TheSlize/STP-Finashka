package ru.library.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DayStat {

    private static final DateTimeFormatter LABEL = DateTimeFormatter.ofPattern("dd.MM");
    private static final Locale RU = Locale.forLanguageTag("ru");

    private final LocalDate date;
    private final long count;
    private final int heightPercent;

    public DayStat(LocalDate date, long count, int heightPercent) {
        this.date = date;
        this.count = count;
        this.heightPercent = heightPercent;
    }

    public long getCount() {
        return count;
    }

    public int getHeightPercent() {
        return heightPercent;
    }

    public String getLabel() {
        return date.format(LABEL);
    }

    public String getWeekday() {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, RU);
    }

    public boolean isToday() {
        return date.equals(LocalDate.now());
    }
}
