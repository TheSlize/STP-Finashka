package ru.autopark.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.autopark.model.Car;
import ru.autopark.repository.CarRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class CarService {

    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Car findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Машина с ID " + id + " не найдена"));
    }

    @Transactional
    public Car create(Car car) {
        car.setId(null);
        normalize(car);
        return repository.save(car);
    }

    @Transactional
    public Car update(Long id, Car data) {
        Car car = findById(id);
        car.setModel(data.getModel());
        car.setManufactureYear(data.getManufactureYear());
        car.setRegistrationDate(data.getRegistrationDate());
        car.setOwnerName(data.getOwnerName());
        normalize(car);
        return repository.save(car);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public List<Car> search(CarSearchCriteria criteria) {
        return repository.findAll(CarSpecifications.fromCriteria(criteria), criteria.toSort());
    }

    @Transactional(readOnly = true)
    public FleetStats statistics() {
        return new FleetStats(repository.count(), repository.countModels());
    }

    @Transactional(readOnly = true)
    public List<DayStat> dailyRegistrations(int days) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(days - 1L);

        Map<LocalDate, Long> counts = new TreeMap<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            counts.put(d, 0L);
        }
        for (Car car : repository.findByRegistrationDateBetween(from, to)) {
            counts.merge(car.getRegistrationDate(), 1L, Long::sum);
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

    private static void normalize(Car car) {
        car.setModel(trim(car.getModel()));
        car.setOwnerName(trim(car.getOwnerName()));
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
