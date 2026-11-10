package ru.warehouse.web;

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
import ru.warehouse.model.Equipment;
import ru.warehouse.model.StockStatus;
import ru.warehouse.service.DayStat;
import ru.warehouse.service.EquipmentSearchCriteria;
import ru.warehouse.service.EquipmentService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class EquipmentController {

    private static final String LIST_URL = "/equipment";

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:" + LIST_URL;
    }

    @GetMapping("/equipment")
    public String list(@ModelAttribute("criteria") EquipmentSearchCriteria criteria, Model model,
                       Principal principal) {
        List<Equipment> items = equipmentService.search(criteria);
        List<DayStat> histogram = equipmentService.dailyShipments(criteria.getHistogramDays());

        // После ошибки в форме техника с ошибками приходит через flash-атрибуты
        // это надо, чтобы, например, при виде техники из пробелов все остальные поля из формы не стёрлись и можно было поправить
        if (!model.containsAttribute("equipment")) {
            model.addAttribute("equipment", new Equipment());
        }
        model.addAttribute("items", items);
        model.addAttribute("itemCount", items.size());
        model.addAttribute("stats", equipmentService.statistics());
        model.addAttribute("histogram", histogram);
        model.addAttribute("histogramTotal", histogram.stream().mapToLong(DayStat::getCount).sum());
        model.addAttribute("statuses", StockStatus.values());
        model.addAttribute("username", principal.getName());
        return "equipment";
    }

    @PostMapping("/equipment")
    public String create(@Valid @ModelAttribute("equipment") Equipment equipment, BindingResult result,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        checkDates(equipment, result);
        if (result.hasErrors()) {
            return reopenForm(equipment, result, returnUrl, redirect);
        }
        Equipment saved = equipmentService.create(equipment);
        redirect.addFlashAttribute("message", "Техника \"" + saved.getType() + "\" добавлена (ID " + saved.getId() + ")");
        return redirectTo(returnUrl);
    }

    @PostMapping("/equipment/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("equipment") Equipment equipment,
                         BindingResult result,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        equipment.setId(id);
        checkDates(equipment, result);
        if (result.hasErrors()) {
            return reopenForm(equipment, result, returnUrl, redirect);
        }
        Equipment saved = equipmentService.update(id, equipment);
        redirect.addFlashAttribute("message", "Техника \"" + saved.getType() + "\" (ID " + id + ") изменена");
        return redirectTo(returnUrl);
    }

    @PostMapping("/equipment/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(defaultValue = LIST_URL) String returnUrl,
                         RedirectAttributes redirect) {
        Equipment equipment = equipmentService.findById(id);
        equipmentService.delete(id);
        redirect.addFlashAttribute("message", "Техника \"" + equipment.getType() + "\" (ID " + id + ") удалена");
        return redirectTo(returnUrl);
    }

    private static String reopenForm(Equipment equipment, BindingResult result, String returnUrl,
                                     RedirectAttributes redirect) {
        redirect.addFlashAttribute("equipment", equipment);
        redirect.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "equipment", result);
        redirect.addFlashAttribute("formOpen", true);
        return redirectTo(returnUrl);
    }

    // вот тут - возврат к списку с теми же поиском и сортировкой
    private static String redirectTo(String returnUrl) {
        return "redirect:" + (returnUrl.startsWith(LIST_URL + "?") ? returnUrl : LIST_URL);
    }

    private static void checkDates(Equipment equipment, BindingResult result) {
        LocalDate today = LocalDate.now();
        if (equipment.getArrivalDate() != null && equipment.getArrivalDate().isAfter(today)) {
            result.rejectValue("arrivalDate", "future", "Дата ввоза не может быть в будущем");
        }
        if (equipment.getDepartureDate() != null) {
            if (equipment.getArrivalDate() != null && equipment.getDepartureDate().isBefore(equipment.getArrivalDate())) {
                result.rejectValue("departureDate", "beforeArrival", "Дата вывоза не может быть раньше даты ввоза");
            } else if (equipment.getDepartureDate().isAfter(today)) {
                result.rejectValue("departureDate", "future", "Дата вывоза не может быть в будущем");
            }
        }
    }
}
