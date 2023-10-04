package managers;

import commands.ScriptStack;
import commands.network.Response;
import exceptions.NoneValueArgumentException;
import exceptions.RecursiveException;
import models.Car;
import models.HumanBeing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class Receiver {
    private final CollectionManager collectionManager;
    private final DataBaseManager dataBaseManager;

    public Receiver(CollectionManager collectionManager, DataBaseManager dataBaseManager) {
        this.collectionManager = collectionManager;
        this.dataBaseManager = dataBaseManager;
    }

    public Response help(String[] args){
        String message = "help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "logout : выйти из аккаунта\n" +
                "add {element} : добавить новый элемент в коллекцию\n" +
                "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                "remove_by_id id : удалить элемент из коллекции по его id\n" +
                "clear : очистить коллекцию\n" +
                "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit : завершить программу (без сохранения в файл)\n" +
                "add_if_min {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                "filter_by_car car : вывести элементы, значение поля car которых равно заданному\n" +
                "print_unique_car : вывести уникальные значения поля car всех элементов в коллекции\n" +
                "print_field_descending_mood : вывести значения поля mood всех элементов в порядке убывания";
        return new Response(message);
    }

    public Response info(String[] args){
        LocalDateTime initTime = collectionManager.getCreationDate();
        String message = "Collection info:" +
                "\nType: " + collectionManager.getHumanBeings().getClass().getName() +
                "\nSize: " + collectionManager.getHumanBeings().size() +
                "\nTime: " + initTime;
        return new Response(message);
    }
    public Response show(String[] args){
        if (collectionManager.getHumanBeings().size() == 0){
            return new Response("Collection is empty");
        }
        StringBuilder message = new StringBuilder();
        collectionManager.getSortedByLocationHumanBeings().forEach((value) -> message.append(value.toString()).append("\n"));
        return new Response(message.toString());
    }
    public Response login(String[] args, Object obj, User user){
        Set<User> users = dataBaseManager.loadUsersFromDB();
        for (User userI: users) {
            if (Objects.equals(userI.getUsername(), user.getUsername()) && Arrays.equals(userI.getPassword(), user.getPassword())){
                return new Response("signed");
            }
        }
        return new Response("Incorrect username or password. Try again.");
    }
    public Response register(String[] args, Object obj, User user){
        boolean success = dataBaseManager.addUser(user.getUsername(),user.getPassword());
        if (success){
            return new Response("registered");
        } else {
            return new Response("This username is already exists. Try again.");
        }
    }

    public Response add(String[] args, Object obj, User user){
        HumanBeing humanBeing = (HumanBeing) obj;
        humanBeing.setCreationDate(java.time.LocalDate.now());
        int id = dataBaseManager.insertIntoDB(humanBeing, user);
        humanBeing.setId(id);
        humanBeing.setUsername(user.getUsername());
        collectionManager.add((HumanBeing) obj);
        return new Response("Element was added");
    }
    public Response update(String[] args, Object obj, User user){
        try {
            if (args.length == 0) {
                throw new NoneValueArgumentException();
            }
            int id = Integer.parseInt(args[0]);
            HumanBeing humanBeing = (HumanBeing) obj;
            humanBeing.setCreationDate(java.time.LocalDate.now());
            humanBeing.setUsername(collectionManager.getById(id).getUsername());
            if (dataBaseManager.updateByIdDB((HumanBeing) obj,id, user)){
                collectionManager.update((HumanBeing) obj,id);
                return new Response("Element was updated");
            } else {
                return new Response("You have no rights to update this object");
            }
        } catch (NoneValueArgumentException e){
            return new Response(e.getMessage());
        } catch (NumberFormatException e) {
            return new Response("Argument should be int");
        }
    }
    public Response removeById(String[] args, User user){
        try {
            if (args.length == 0) {
                throw new NoneValueArgumentException();
            }
            int id = Integer.parseInt(args[0]);
            if (collectionManager.getById(id) != null) {
                if (dataBaseManager.removeById(id, user)) {
                    collectionManager.removeById(id);
                    return new Response("Element was removed");
                } else {
                    return new Response("You have no rights to update this object");
                }
            } else {
                return new Response("Object with that id does not exist");
            }
        } catch (NoneValueArgumentException e){
            return new Response(e.getMessage());
        } catch (NumberFormatException e){
            return new Response("Argument should be int");
        }
    }
    public Response clear(String[] args, User user){
        if (dataBaseManager.clearDB(user)){
            collectionManager.clear(user);
        }
        return new Response("Collection was cleared");
    }

    public Response addIfMin(String[] args, Object obj, User user) {
        HumanBeing humanBeing = (HumanBeing) obj;
        humanBeing.setCreationDate(java.time.LocalDate.now());

        boolean added = collectionManager.getHumanBeings()
                .stream()
                .noneMatch(existing -> existing.compareTo(humanBeing) <= 0);

        if (added) {
            int id = dataBaseManager.insertIntoDB(humanBeing, user);
            if (id != -1) {
                humanBeing.setId(id);
                humanBeing.setUsername(user.getUsername());
            }
            collectionManager.add(humanBeing);
            return new Response("Element was added");
        }

        return new Response("Element wasn't added");
    }


    public Response removeGreater(String[] args, Object obj, User user) {
        int lastSize;
        LinkedHashSet<HumanBeing> humanBeings;

        synchronized (collectionManager) {
            lastSize = collectionManager.getHumanBeings().size();

            HumanBeing humanBeing = (HumanBeing) obj;
            humanBeings = new LinkedHashSet<>(collectionManager.getHumanBeings());

            humanBeings.removeIf(existing -> existing.compareTo(humanBeing) > 0);

            collectionManager.setHumanBeings(humanBeings);
        }

        return checkDifference(lastSize, humanBeings, user);
    }

    public Response removeLower(String[] args, Object obj, User user) {
        int lastSize;
        LinkedHashSet<HumanBeing> humanBeings;

        synchronized (collectionManager) {
            lastSize = collectionManager.getHumanBeings().size();

            HumanBeing humanBeing = (HumanBeing) obj;
            humanBeings = new LinkedHashSet<>(collectionManager.getHumanBeings());
            humanBeings.removeIf(existing -> existing.compareTo(humanBeing) < 0);

            collectionManager.setHumanBeings(humanBeings);
        }

        return checkDifference(lastSize, humanBeings, user);
    }

    private Response checkDifference(int lastSize, Set<HumanBeing> updatedHumanBeings, User user) {
        int newSize = updatedHumanBeings.size();
        int difference = lastSize - newSize;

        if (difference > 0) {
            return new Response("Successfully removed " + difference + " elements.");
        } else {
            return new Response("No elements were removed.");
        }
    }


    public Response filterByCar(String[] args, Object obj) {
        List<HumanBeing> filteredList = collectionManager.getSortedByLocationHumanBeings()
                .stream()
                .filter(humanBeing -> humanBeing.getCar() != null && humanBeing.getCar().equals((Car) obj))
                .toList();

        if (filteredList.isEmpty()) {
            return new Response("No matching elements found");
        } else {
            StringBuilder message = new StringBuilder();
            filteredList.forEach(humanBeing -> message.append(humanBeing.toString()).append("\n"));
            System.out.println(message);
            return new Response(message.toString());
        }
    }

    public Response printUniqueCar(String[] args){
        return new Response(collectionManager.printUniqueCar());
    }
    public Response printFieldDescendingMood(String[] args){
        return new Response(collectionManager.printFieldDescendingMood());
    }

}
