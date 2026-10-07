package ru.library.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.library.model.Book;
import ru.library.model.LoanStatus;
import ru.library.service.BookSearchCriteria;
import ru.library.service.BookService;
import ru.library.service.DayStat;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookController {

    private static final String LIST_URL = "/books";

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:" + LIST_URL;
    }

    @GetMapping("/books")
    public String list(@ModelAttribute("criteria") BookSearchCriteria criteria, Model model) {
        List<Book> books = bookService.search(criteria);
        List<DayStat> histogram = bookService.dailyIssues(criteria.getHistogramDays());

        // После ошибки в форме книга с ошибками приходит через flash-атрибуты
        // это надо, чтобы, например, при названии из пробелов все остальные поля из формы создания страницы не стёрлись и можно было поправить
        if (!model.containsAttribute("book")) {
            model.addAttribute("book", new Book());
        }
        model.addAttribute("books", books);
        model.addAttribute("bookCount", books.size());
        model.addAttribute("stats", bookService.statistics());
        model.addAttribute("histogram", histogram);
        model.addAttribute("histogramTotal", histogram.stream().mapToLong(DayStat::getCount).sum());
        model.addAttribute("statuses", LoanStatus.values());
        return "books";
    }

    @PostMapping("/books")
    public String create(@Valid @ModelAttribute("book") Book book, BindingResult result,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        checkDates(book, result);
        if (result.hasErrors()) {
            return reopenForm(book, result, returnUrl, redirect);
        }
        Book saved = bookService.create(book);
        redirect.addFlashAttribute("message", "Книга \"" + saved.getTitle() + "\" добавлена (ID " + saved.getId() + ")");
        return redirectTo(returnUrl);
    }

    @PostMapping("/books/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("book") Book book, BindingResult result,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        book.setId(id);
        checkDates(book, result);
        if (result.hasErrors()) {
            return reopenForm(book, result, returnUrl, redirect);
        }
        Book saved = bookService.update(id, book);
        redirect.addFlashAttribute("message", "Книга \"" + saved.getTitle() + "\" изменена");
        return redirectTo(returnUrl);
    }

    @PostMapping("/books/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        Book book = bookService.findById(id);
        bookService.delete(id);
        redirect.addFlashAttribute("message", "Книга \"" + book.getTitle() + "\" удалена");
        return redirectTo(returnUrl);
    }

    private static String reopenForm(Book book, BindingResult result, String returnUrl,
                                     RedirectAttributes redirect) {
        redirect.addFlashAttribute("book", book);
        redirect.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "book", result);
        redirect.addFlashAttribute("formOpen", true);
        return redirectTo(returnUrl);
    }

    // вот тут - возврат к списку с теми же поиском и сортировкой
    private static String redirectTo(String returnUrl) {
        return "redirect:" + (returnUrl.startsWith(LIST_URL + "?") ? returnUrl : LIST_URL);
    }

    private static void checkDates(Book book, BindingResult result) {
        LocalDate today = LocalDate.now();
        if (book.getIssueDate() != null && book.getIssueDate().isAfter(today)) {
            result.rejectValue("issueDate", "future", "Дата выдачи не может быть в будущем");
        }
        if (book.getReturnDate() != null) {
            if (book.getIssueDate() != null && book.getReturnDate().isBefore(book.getIssueDate())) {
                result.rejectValue("returnDate", "beforeIssue", "Дата сдачи не может быть раньше даты выдачи");
            } else if (book.getReturnDate().isAfter(today)) {
                result.rejectValue("returnDate", "future", "Дата сдачи не может быть в будущем");
            }
        }
    }
}
