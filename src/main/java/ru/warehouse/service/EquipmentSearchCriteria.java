package ru.warehouse.service;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import ru.warehouse.model.StockStatus;

import java.time.LocalDate;
import java.util.Set;

public class EquipmentSearchCriteria {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "type", "groupName", "arrivalDate", "departureDate", "driverName");

    private static final String DEFAULT_FIELD = "arrivalDate";
    private static final String DEFAULT_DIR = Sort.Direction.DESC.name().toLowerCase();
    private static final int MAX_HISTOGRAM_DAYS = 31;

    private String type;
    private String groupName;
    private String driverName;
    private StockStatus status = StockStatus.ALL;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate arrivalFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate arrivalTo;

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
            case "type" -> "виду техники";
            case "groupName" -> "группе техники";
            case "departureDate" -> "дате вывоза";
            case "driverName" -> "ФИО водителя";
            default -> "дате ввоза";
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
        UriComponentsBuilder b = UriComponentsBuilder.fromPath("/equipment");
        addParam(b, "type", type);
        addParam(b, "groupName", groupName);
        addParam(b, "driverName", driverName);
        if (status != null && status != StockStatus.ALL) {
            b.queryParam("status", status.name());
        }
        if (arrivalFrom != null) {
            b.queryParam("arrivalFrom", arrivalFrom);
        }
        if (arrivalTo != null) {
            b.queryParam("arrivalTo", arrivalTo);
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
        return hasText(type) || hasText(groupName) || hasText(driverName)
                || arrivalFrom != null || arrivalTo != null
                || (status != null && status != StockStatus.ALL);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public StockStatus getStatus() {
        return status;
    }

    public void setStatus(StockStatus status) {
        this.status = status;
    }

    public LocalDate getArrivalFrom() {
        return arrivalFrom;
    }

    public void setArrivalFrom(LocalDate arrivalFrom) {
        this.arrivalFrom = arrivalFrom;
    }

    public LocalDate getArrivalTo() {
        return arrivalTo;
    }

    public void setArrivalTo(LocalDate arrivalTo) {
        this.arrivalTo = arrivalTo;
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
