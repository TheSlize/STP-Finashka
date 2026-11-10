package ru.autopark.web;

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
import ru.autopark.model.Car;
import ru.autopark.service.CarSearchCriteria;
import ru.autopark.service.CarService;
import ru.autopark.service.DayStat;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CarController {

    private static final String LIST_URL = "/cars";

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:" + LIST_URL;
    }

    @GetMapping("/cars")
    public String list(@ModelAttribute("criteria") CarSearchCriteria criteria, Model model) {
        List<Car> cars = carService.search(criteria);
        List<DayStat> histogram = carService.dailyRegistrations(criteria.getHistogramDays());

        // После ошибки в форме машина с ошибками приходит через flash-атрибуты
        // это надо, чтобы, например, при марке из пробелов все остальные поля из формы не стёрлись и можно было поправить
        if (!model.containsAttribute("car")) {
            model.addAttribute("car", new Car());
        }
        model.addAttribute("cars", cars);
        model.addAttribute("carCount", cars.size());
        model.addAttribute("stats", carService.statistics());
        model.addAttribute("histogram", histogram);
        model.addAttribute("histogramTotal", histogram.stream().mapToLong(DayStat::getCount).sum());
        model.addAttribute("currentYear", LocalDate.now().getYear());
        return "cars";
    }

    @PostMapping("/cars")
    public String create(@Valid @ModelAttribute("car") Car car, BindingResult result,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        checkDates(car, result);
        if (result.hasErrors()) {
            return reopenForm(car, result, returnUrl, redirect);
        }
        Car saved = carService.create(car);
        redirect.addFlashAttribute("message", "Машина \"" + saved.getModel() + "\" добавлена (ID " + saved.getId() + ")");
        return redirectTo(returnUrl);
    }

    @PostMapping("/cars/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("car") Car car, BindingResult result,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        car.setId(id);
        checkDates(car, result);
        if (result.hasErrors()) {
            return reopenForm(car, result, returnUrl, redirect);
        }
        Car saved = carService.update(id, car);
        redirect.addFlashAttribute("message", "Машина \"" + saved.getModel() + "\" (ID " + id + ") изменена");
        return redirectTo(returnUrl);
    }

    @PostMapping("/cars/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        Car car = carService.findById(id);
        carService.delete(id);
        redirect.addFlashAttribute("message", "Машина \"" + car.getModel() + "\" (ID " + id + ") удалена");
        return redirectTo(returnUrl);
    }

    private static String reopenForm(Car car, BindingResult result, String returnUrl,
                                     RedirectAttributes redirect) {
        redirect.addFlashAttribute("car", car);
        redirect.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "car", result);
        redirect.addFlashAttribute("formOpen", true);
        return redirectTo(returnUrl);
    }

    // вот тут - возврат к списку с теми же поиском и сортировкой
    private static String redirectTo(String returnUrl) {
        return "redirect:" + (returnUrl.startsWith(LIST_URL + "?") ? returnUrl : LIST_URL);
    }

    private static void checkDates(Car car, BindingResult result) {
        LocalDate today = LocalDate.now();
        Integer year = car.getManufactureYear();
        if (year != null && !result.hasFieldErrors("manufactureYear") && year > today.getYear()) {
            result.rejectValue("manufactureYear", "future", "Год выпуска не может быть в будущем");
        }
        LocalDate date = car.getRegistrationDate();
        if (date != null) {
            if (date.isAfter(today)) {
                result.rejectValue("registrationDate", "future", "Дата постановки на учёт не может быть в будущем");
            } else if (year != null && date.getYear() < year) {
                result.rejectValue("registrationDate", "beforeYear", "Машину нельзя поставить на учёт раньше года выпуска");
            }
        }
    }
}
