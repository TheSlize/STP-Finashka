package ru.library.service;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.util.UriComponentsBuilder;
import ru.library.model.LoanStatus;

import java.time.LocalDate;
import java.util.Set;

public class BookSearchCriteria {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "title", "publishingHouse", "issueDate", "studentName", "returnDate");

    private static final String DEFAULT_DIR = Sort.Direction.DESC.name().toLowerCase();
    private static final int MAX_HISTOGRAM_DAYS = 31;

    private String title;
    private String publishingHouse;
    private String studentName;
    private LoanStatus status = LoanStatus.ALL;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issueFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issueTo;

    private String sortField = "issueDate";
    private String sortDir = DEFAULT_DIR;

    private Integer histogramDays;

    public String getEffectiveSortField() {
        return sortField != null && SORTABLE_FIELDS.contains(sortField)
                ? sortField
                : "issueDate";
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
            case "title" -> "названию";
            case "publishingHouse" -> "издательству";
            case "studentName" -> "ФИО студента";
            case "returnDate" -> "дате сдачи";
            default -> "дате выдачи";
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
        UriComponentsBuilder b = UriComponentsBuilder.fromPath("/books");
        addParam(b, "title", title);
        addParam(b, "publishingHouse", publishingHouse);
        addParam(b, "studentName", studentName);
        if (status != null && status != LoanStatus.ALL) {
            b.queryParam("status", status.name());
        }
        if (issueFrom != null) {
            b.queryParam("issueFrom", issueFrom);
        }
        if (issueTo != null) {
            b.queryParam("issueTo", issueTo);
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
        return hasText(title) || hasText(publishingHouse) || hasText(studentName)
                || issueFrom != null || issueTo != null
                || (status != null && status != LoanStatus.ALL);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishingHouse() {
        return publishingHouse;
    }

    public void setPublishingHouse(String publishingHouse) {
        this.publishingHouse = publishingHouse;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LocalDate getIssueFrom() {
        return issueFrom;
    }

    public void setIssueFrom(LocalDate issueFrom) {
        this.issueFrom = issueFrom;
    }

    public LocalDate getIssueTo() {
        return issueTo;
    }

    public void setIssueTo(LocalDate issueTo) {
        this.issueTo = issueTo;
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
