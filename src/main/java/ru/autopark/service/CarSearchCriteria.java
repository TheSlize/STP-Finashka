package ru.autopark.service;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Set;

public class CarSearchCriteria {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "model", "manufactureYear", "registrationDate", "ownerName");

    private static final String DEFAULT_FIELD = "manufactureYear";
    private static final String DEFAULT_DIR = Sort.Direction.DESC.name().toLowerCase();
    private static final int MAX_HISTOGRAM_DAYS = 31;

    private String model;
    private String ownerName;
    private Integer yearFrom;
    private Integer yearTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate registeredFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate registeredTo;

    private String sortField = DEFAULT_FIELD;
    private String sortDir = DEFAULT_DIR;

    private Integer histogramDays;

    public String getEffectiveSortField() {
        return sortField != null && SORTABLE_FIELDS.contains(sortField)
                ? sortField
                : DEFAULT_FIELD;
    }

    public Sort.Direction getEffectiveDirection() {
        return "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    public Sort toSort() {
        return Sort.by(getEffectiveDirection(), getEffectiveSortField())
                .and(Sort.by(Sort.Direction.ASC, "id"));
    }

    public String getSortDescription() {
        String field = switch (getEffectiveSortField()) {
            case "id" -> "ID";
            case "model" -> "марке";
            case "registrationDate" -> "дате постановки на учёт";
            case "ownerName" -> "ФИО владельца";
            default -> "году выпуска";
        };
        String dir = getEffectiveDirection() == Sort.Direction.ASC ? "по возрастанию" : "по убыванию";
        return "по " + field + ", " + dir;
    }

    public String sortUrl(String field) {
        boolean flip = field.equals(getEffectiveSortField()) && getEffectiveDirection() == Sort.Direction.ASC;
        return filterBuilder()
                .queryParam("sortField", field)
                .queryParam("sortDir", flip ? "desc" : "asc")
                .encode().build().toUriString();
    }

    // Адрес списка с текущими поиском и сортировкой, на него возвращают формы чтоб фильтры не сбросились
    public String getUrl() {
        UriComponentsBuilder b = filterBuilder();
        addParam(b, "sortField", sortField);
        addParam(b, "sortDir", sortDir);
        return b.encode().build().toUriString();
    }

    private UriComponentsBuilder filterBuilder() {
        UriComponentsBuilder b = UriComponentsBuilder.fromPath("/cars");
        addParam(b, "model", model);
        addParam(b, "ownerName", ownerName);
        if (yearFrom != null) {
            b.queryParam("yearFrom", yearFrom);
        }
        if (yearTo != null) {
            b.queryParam("yearTo", yearTo);
        }
        if (registeredFrom != null) {
            b.queryParam("registeredFrom", registeredFrom);
        }
        if (registeredTo != null) {
            b.queryParam("registeredTo", registeredTo);
        }
        b.queryParam("histogramDays", getHistogramDays());
        return b;
    }

    public String sortClass(String field) {
        if (!field.equals(getEffectiveSortField())) {
            return "";
        }
        return getEffectiveDirection() == Sort.Direction.ASC ? "sorted-asc" : "sorted-desc";
    }

    public boolean isFiltered() {
        return hasText(model) || hasText(ownerName)
                || yearFrom != null || yearTo != null
                || registeredFrom != null || registeredTo != null;
    }

    private static void addParam(UriComponentsBuilder b, String name, String value) {
        if (hasText(value)) {
            b.queryParam(name, value.trim());
        }
    }

    static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    public int getHistogramDays() {
        if (histogramDays == null) {
            return 14;
        }
        return Math.max(1, Math.min(MAX_HISTOGRAM_DAYS, histogramDays));
    }

    public void setHistogramDays(Integer histogramDays) {
        this.histogramDays = histogramDays;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }

    public LocalDate getRegisteredFrom() {
        return registeredFrom;
    }

    public void setRegisteredFrom(LocalDate registeredFrom) {
        this.registeredFrom = registeredFrom;
    }

    public LocalDate getRegisteredTo() {
        return registeredTo;
    }

    public void setRegisteredTo(LocalDate registeredTo) {
        this.registeredTo = registeredTo;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
