package ru.warehouse.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.warehouse.model.Equipment;
import ru.warehouse.repository.EquipmentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EquipmentService {

    private final EquipmentRepository repository;

    public EquipmentService(EquipmentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Equipment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Техника с ID " + id + " не найдена"));
    }

    @Transactional
    public Equipment create(Equipment equipment) {
        equipment.setId(null);
        normalize(equipment);
        return repository.save(equipment);
    }

    @Transactional
    public Equipment update(Long id, Equipment data) {
        Equipment equipment = findById(id);
        equipment.setType(data.getType());
        equipment.setGroupName(data.getGroupName());
        equipment.setArrivalDate(data.getArrivalDate());
        equipment.setDepartureDate(data.getDepartureDate());
        equipment.setDriverName(data.getDriverName());
        normalize(equipment);
        return repository.save(equipment);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public List<Equipment> search(EquipmentSearchCriteria criteria) {
        return repository.findAll(EquipmentSpecifications.fromCriteria(criteria), criteria.toSort());
    }

    @Transactional(readOnly = true)
    public WarehouseStats statistics() {
        return new WarehouseStats(repository.count(), repository.countByDepartureDateIsNull());
    }

    @Transactional(readOnly = true)
    public List<DayStat> dailyShipments(int days) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(days - 1L);

        Map<LocalDate, Long> counts = new TreeMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            counts.put(d, 0L);
        }
        for (Equipment equipment : repository.findByDepartureDateBetween(from, to)) {
            counts.merge(equipment.getDepartureDate(), 1L, Long::sum);
        }

        long max = 0;
        for (long c : counts.values()) {
            max = Math.max(max, c);
        }

        List<DayStat> result = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> e : counts.entrySet()) {
            int height = max == 0 ? 0 : (int) Math.round(e.getValue() * 100.0 / max);
            result.add(new DayStat(e.getKey(), e.getValue(), height));
        }
        return result;
    }

    private static void normalize(Equipment equipment) {
        equipment.setType(trim(equipment.getType()));
        equipment.setGroupName(trim(equipment.getGroupName()));
        equipment.setDriverName(trim(equipment.getDriverName()));
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
