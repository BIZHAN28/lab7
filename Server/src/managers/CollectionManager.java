package managers;

import models.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {

    private Set<HumanBeing> humanBeings;
    private final LocalDateTime creationDate;

    public CollectionManager() {
        humanBeings = Collections.synchronizedSet(new LinkedHashSet<>());
        creationDate = LocalDateTime.now();
    }

    public void setHumanBeings(LinkedHashSet<HumanBeing> humanBeings) {
        this.humanBeings = Collections.synchronizedSet(humanBeings);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Set<HumanBeing> getHumanBeings() {
        return Collections.unmodifiableSet(humanBeings);
    }

    public LinkedHashSet<HumanBeing> getSortedByLocationHumanBeings() {
        return humanBeings.stream()
                .sorted(Comparator.comparing(HumanBeing::getCoordinates))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void add(HumanBeing humanBeing) {
        humanBeings.add(humanBeing);
    }

    public synchronized void update(HumanBeing humanBeing, int id) {
        humanBeing.setId(id);
        LinkedHashSet<HumanBeing> newHumanBeings = new LinkedHashSet<>();
        for (HumanBeing humanBeingIterator : humanBeings) {
            if (humanBeingIterator.getId() == id) {
                newHumanBeings.add(humanBeing);
            } else {
                newHumanBeings.add(humanBeingIterator);
            }
        }
        humanBeings = newHumanBeings;
    }

    public synchronized void removeById(int id) {
        HumanBeing humanBeing = this.getById(id);
        if (humanBeing != null) {
            humanBeings.remove(humanBeing);
        }
    }

    public HumanBeing getById(int id) {
        for (HumanBeing humanBeing : humanBeings) {
            if (humanBeing.getId() == id) {
                return humanBeing;
            }
        }
        return null;
    }

    public synchronized void clear(User user) {
        humanBeings.removeIf(humanBeing -> Objects.equals(humanBeing.getUsername(), user.getUsername()));
    }

    public LinkedHashSet<HumanBeing> filterByCar(Car car) {
        LinkedHashSet<HumanBeing> filteredHumanBeings = new LinkedHashSet<>(humanBeings);
        filteredHumanBeings.removeIf(humanBeing -> !(humanBeing.getCar().equals(car)));
        return filteredHumanBeings;
    }

    public String printUniqueCar() {
        Set<Car> uniqueCars = getHumanBeings()
                .stream()
                .map(HumanBeing::getCar)
                .collect(Collectors.toSet());

        if (uniqueCars.isEmpty()) {
            return "Collection is empty";
        } else {
            return uniqueCars.toString();
        }
    }

    public LinkedHashSet<HumanBeing> sortByImpactSpeed() {
        return humanBeings.stream()
                .sorted(Comparator.comparing(HumanBeing::getImpactSpeed).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public String printFieldDescendingMood() {
        LinkedHashSet<HumanBeing> sortedByImpactSpeed = sortByImpactSpeed();

        if (sortedByImpactSpeed.isEmpty()) {
            return "Collection is empty";
        }

        StringBuilder message = new StringBuilder();
        sortedByImpactSpeed.stream()
                .map(HumanBeing::getMood)
                .sorted(Collections.reverseOrder())
                .forEach(mood -> message.append(mood));

        return message.toString();
    }
}
