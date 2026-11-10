package ru.cinema.web;

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
import ru.cinema.model.FilmSession;
import ru.cinema.service.DayStat;
import ru.cinema.service.FilmSessionSearchCriteria;
import ru.cinema.service.FilmSessionService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class FilmSessionController {

    private static final String LIST_URL = "/sessions";

    private final FilmSessionService sessionService;

    public FilmSessionController(FilmSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:" + LIST_URL;
    }

    @GetMapping("/sessions")
    public String list(@ModelAttribute("criteria") FilmSessionSearchCriteria criteria, Model model,
                       Principal principal) {
        List<FilmSession> sessions = sessionService.search(criteria);
        List<DayStat> histogram = sessionService.dailySessions(criteria.getHistogramDays());

        // После ошибки в форме сеанс с ошибками приходит через flash-атрибуты
        // это надо, чтобы, например, при названии из пробелов все остальные поля из формы не стёрлись и можно было поправить
        if (!model.containsAttribute("filmSession")) {
            model.addAttribute("filmSession", new FilmSession());
        }
        model.addAttribute("sessions", sessions);
        model.addAttribute("sessionCount", sessions.size());
        model.addAttribute("stats", sessionService.statistics());
        model.addAttribute("histogram", histogram);
        model.addAttribute("histogramTotal", histogram.stream().mapToLong(DayStat::getCount).sum());
        model.addAttribute("defaultTime", LocalDate.now().atTime(LocalTime.of(18, 0)));
        model.addAttribute("username", principal.getName());
        return "sessions";
    }

    @PostMapping("/sessions")
    public String create(@Valid @ModelAttribute("filmSession") FilmSession session, BindingResult result,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return reopenForm(session, result, returnUrl, redirect);
        }
        FilmSession saved = sessionService.create(session);
        redirect.addFlashAttribute("message", "Сеанс \"" + saved.getFilmTitle() + "\" добавлен (ID " + saved.getId() + ")");
        return redirectTo(returnUrl);
    }

    @PostMapping("/sessions/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("filmSession") FilmSession session,
                         BindingResult result,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        session.setId(id);
        if (result.hasErrors()) {
            return reopenForm(session, result, returnUrl, redirect);
        }
        FilmSession saved = sessionService.update(id, session);
        redirect.addFlashAttribute("message", "Сеанс \"" + saved.getFilmTitle() + "\" (ID " + id + ") изменён");
        return redirectTo(returnUrl);
    }

    @PostMapping("/sessions/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        FilmSession session = sessionService.findById(id);
        sessionService.delete(id);
        redirect.addFlashAttribute("message", "Сеанс \"" + session.getFilmTitle() + "\" (ID " + id + ") удалён");
        return redirectTo(returnUrl);
    }

    private static String reopenForm(FilmSession session, BindingResult result, String returnUrl,
                                     RedirectAttributes redirect) {
        redirect.addFlashAttribute("filmSession", session);
        redirect.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "filmSession", result);
        redirect.addFlashAttribute("formOpen", true);
        return redirectTo(returnUrl);
    }

    // вот тут - возврат к списку с теми же поиском и сортировкой
    private static String redirectTo(String returnUrl) {
        return "redirect:" + (returnUrl.startsWith(LIST_URL + "?") ? returnUrl : LIST_URL);
    }
}
