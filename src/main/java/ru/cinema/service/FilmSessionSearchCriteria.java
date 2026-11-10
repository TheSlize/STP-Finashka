package ru.cinema.service;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Set;

public class FilmSessionSearchCriteria {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "filmTitle", "filmStudio", "sessionTime", "ticketCount");

    private static final String DEFAULT_FIELD = "sessionTime";
    private static final String DEFAULT_DIR = Sort.Direction.DESC.name().toLowerCase();
    private static final int MAX_HISTOGRAM_DAYS = 31;

    private String filmTitle;
    private String filmStudio;
    private Integer ticketsFrom;
    private Integer ticketsTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

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
            case "filmTitle" -> "названию фильма";
            case "filmStudio" -> "киностудии";
            case "ticketCount" -> "количеству билетов";
            default -> "дате сеанса";
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
        UriComponentsBuilder b = UriComponentsBuilder.fromPath("/sessions");
        addParam(b, "filmTitle", filmTitle);
        addParam(b, "filmStudio", filmStudio);
        if (dateFrom != null) {
            b.queryParam("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            b.queryParam("dateTo", dateTo);
        }
        if (ticketsFrom != null) {
            b.queryParam("ticketsFrom", ticketsFrom);
        }
        if (ticketsTo != null) {
            b.queryParam("ticketsTo", ticketsTo);
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
        return hasText(filmTitle) || hasText(filmStudio)
                || dateFrom != null || dateTo != null
                || ticketsFrom != null || ticketsTo != null;
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

    public String getFilmTitle() {
        return filmTitle;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
    }

    public String getFilmStudio() {
        return filmStudio;
    }

    public void setFilmStudio(String filmStudio) {
        this.filmStudio = filmStudio;
    }

    public Integer getTicketsFrom() {
        return ticketsFrom;
    }

    public void setTicketsFrom(Integer ticketsFrom) {
        this.ticketsFrom = ticketsFrom;
    }

    public Integer getTicketsTo() {
        return ticketsTo;
    }

    public void setTicketsTo(Integer ticketsTo) {
        this.ticketsTo = ticketsTo;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
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
