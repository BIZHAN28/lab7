package managers;

import models.*;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataBaseManager {
    private static final String host = "pg:5432";
    private static final String databaseName = "studs";
    private static final String username = "s367099";
    private static final String password = "6M7K7AEG1TLKgqQA";
    private static final String tableName = "human_being";
    private static final String userTableName = "users";
    private static final String jdbcUrl = String.format("jdbc:postgresql://%s/%s", host, databaseName);

    private Connection connection;
    public DataBaseManager(){
        try {
            this.connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Успешное подключение к базе данных PostgresSQL!");
        }catch (SQLException e) {
            handleSQLException("Ошибка при подключении к базе данных:", e);
        }
    }
    public boolean removeById(int id, User user) {
            // SQL-запрос для удаления записи по ID

        String sql = "DELETE FROM " + tableName + " WHERE id = ? AND user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2,getUserIdByUsername( user.getUsername()));
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Запись с ID " + id + " успешно удалена.");
                //resetSequence();
                return true;
            } else {
                System.out.println("Запись с ID " + id + " не найдена.");
                return false;
            }
        } catch (SQLException e){
            handleSQLException("Ошибка при удалении записи по ID:", e);
            return false;
        }

    }


    public boolean clearDB(User user) {
        // SQL-запрос для очистки данных из базы данных

        String sql = "DELETE FROM " + tableName + " WHERE user_id = "+ getUserIdByUsername(user.getUsername());

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            System.out.println("База данных успешно очищена.");
            return true;
        }catch (SQLException e) {
            handleSQLException("Ошибка при очистке базы данных:", e);
            return false;
        }
        //return resetSequence();

    }
    private boolean resetSequence(){
        String sql = "SELECT setval(pg_get_serial_sequence('" + tableName + "', 'id'), COALESCE(MAX(id)+1, 1), false) FROM " + tableName;

        try (PreparedStatement resetSequenceStatement = connection.prepareStatement(sql)) {
            resetSequenceStatement.execute();
            System.out.println("Serial sequence успешно сброшен.");
            return true;
        } catch (SQLException e) {
            handleSQLException("Ошибка при очистке базы данных:", e);
            return false;
        }
    }

    public boolean updateByIdDB(HumanBeing humanBeing, int id, User user) {
        if (getUserIdByUsername(humanBeing.getUsername()) == getUserIdByUsername(user.getUsername())) {
            String sql = "UPDATE " + tableName +
                    " SET name=?, coordinates_x=?, coordinates_y=?, creation_date=?, real_hero=?, " +
                    "has_toothpick=?, impact_speed=?, weapon_type=?, mood=?, car_cool=? WHERE id=?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                fillPreparedStatement(preparedStatement, humanBeing, user);
                preparedStatement.setInt(11, id);
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                handleSQLException("Ошибка при обновлении в базу данных:", e);
                return false;
            }
        }
        return false;
    }

    public int insertIntoDB(HumanBeing humanBeing, User user) {
        int id = -1;

        String sql = "INSERT INTO " + tableName +
                " (name, coordinates_x, coordinates_y, creation_date, real_hero, has_toothpick, " +
                "impact_speed, weapon_type, mood, car_cool, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
                "RETURNING id;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            fillPreparedStatement(preparedStatement, humanBeing, user);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка при сохранении в базу данных:", e);
        }
        return id;
    }
    public boolean addUser(String username, byte[] password){
        String sql = "INSERT INTO " + userTableName +
                " (username, password) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setBytes(2, password);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            //handleSQLException("Ошибка при сохранении в базу данных:", e);
            return false;
        }
    }

    private void fillPreparedStatement(PreparedStatement preparedStatement, HumanBeing humanBeing, User user) throws SQLException {
        preparedStatement.setString(1, humanBeing.getName());
        preparedStatement.setDouble(2, humanBeing.getCoordinates().getX());
        preparedStatement.setInt(3, humanBeing.getCoordinates().getY());
        preparedStatement.setDate(4, Date.valueOf(humanBeing.getCreationDate()));
        preparedStatement.setBoolean(5, humanBeing.isRealHero());
        preparedStatement.setBoolean(6, humanBeing.isHasToothpick());
        preparedStatement.setInt(7, humanBeing.getImpactSpeed());
        preparedStatement.setString(8, humanBeing.getWeaponType().toString());
        preparedStatement.setString(9, humanBeing.getMood().toString());
        preparedStatement.setBoolean(10, humanBeing.getCar().getCool());
        preparedStatement.setInt(11, getUserIdByUsername(user.getUsername()));
    }


    public LinkedHashSet<HumanBeing> loadFromDB() {
        LinkedHashSet<HumanBeing> humanBeings = new LinkedHashSet<>();

        System.out.println(jdbcUrl);

        createTableIfNotExists(connection, tableName);

        // SQL-запрос для получения данных из базы данных
        String sql = "SELECT "+ tableName + ".id, name, coordinates_x, coordinates_y, creation_date, " +
                "real_hero, has_toothpick, impact_speed, weapon_type, mood, car_cool, username " +
                "FROM " + tableName + " JOIN " + userTableName + " ON " + tableName+".user_id = "+userTableName+".id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                HumanBeing humanBeing = new HumanBeing();
                mapResultSetToHumanBeing(resultSet, humanBeing);
                humanBeings.add(humanBeing);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка подключения к базе данных PostgresSQL:", e);
        }

        return humanBeings;
    }
    private void mapResultSetToHumanBeing(ResultSet resultSet, HumanBeing humanBeing) throws SQLException {
        humanBeing.setId(resultSet.getInt("id"));
        humanBeing.setName(resultSet.getString("name"));
        Coordinates coordinates = new Coordinates();
        coordinates.setX(resultSet.getDouble("coordinates_x"));
        coordinates.setY(resultSet.getInt("coordinates_y"));
        humanBeing.setCoordinates(coordinates);
        humanBeing.setCreationDate(resultSet.getString("creation_date"));
        humanBeing.setRealHero(resultSet.getBoolean("real_hero"));
        humanBeing.setHasToothpick(resultSet.getBoolean("has_toothpick"));
        humanBeing.setImpactSpeed(resultSet.getInt("impact_speed"));
        humanBeing.setWeaponType(WeaponType.valueOf(resultSet.getString("weapon_type")));
        humanBeing.setMood(Mood.valueOf(resultSet.getString("mood")));
        Car car = new Car();
        car.setCool(resultSet.getBoolean("car_cool"));
        humanBeing.setCar(car);
        humanBeing.setUsername(resultSet.getString("username"));
    }

    public Set<User> loadUsersFromDB(){
        Set<User> users = new HashSet<>();
        createUserTableIfNotExists(connection, userTableName);
        String sql = "SELECT id, username, password " +
                "FROM " + userTableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getBytes("password"));
                users.add(user);
            }
        } catch (SQLException e) {
            handleSQLException("Ошибка подключения к базе данных PostgresSQL:", e);
        }
        return users;
    }

    // Метод для получения id по username
    public int getUserIdByUsername(String username) {

        // SQL-запрос
        String sql = "SELECT id FROM " + userTableName + " WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Устанавливаем значение параметра в запросе
            statement.setString(1, username);
            // Выполняем запрос и получаем результат
           ResultSet resultSet = statement.executeQuery();
                // Если есть результат, возвращаем id
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Если не удалось найти пользователя, возвращаем -1 или другое значение по умолчанию
        return -1;
    }


    private static void createTableIfNotExists(Connection connection, String tableName) {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "coordinates_x DOUBLE PRECISION NOT NULL," +
                    "coordinates_y INT NOT NULL," +
                    "creation_date DATE NOT NULL," +
                    "real_hero BOOLEAN NOT NULL," +
                    "has_toothpick BOOLEAN," +
                    "impact_speed INT," +
                    "weapon_type VARCHAR(255)," +
                    "mood VARCHAR(255) NOT NULL," +
                    "car_cool BOOLEAN NOT NULL," +
                    "user_id INT REFERENCES "+userTableName+"(id)" +
                    ")";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            handleSQLException("Error creating table:", e);
        }
    }
    private static void createUserTableIfNotExists(Connection connection, String userTableName) {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + userTableName + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "username VARCHAR(255) NOT NULL UNIQUE," +
                    "password bytea NOT NULL " +
                    ")";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            handleSQLException("Error creating user table:", e);
        }
    }
    private static void handleSQLException(String message, SQLException e) {
        System.err.println(message);
        e.printStackTrace();
    }

}
