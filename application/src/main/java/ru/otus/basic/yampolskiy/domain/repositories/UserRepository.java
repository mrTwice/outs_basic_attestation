package ru.otus.basic.yampolskiy.domain.repositories;


import ru.otus.basic.yampolskiy.domain.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserRepository {
    private static UserRepository userRepository;
    private final ConnectionsManager dataBaseManager = ConnectionsManager.getConnectionsManager();

    private UserRepository() {
    }

    public static UserRepository getUserRepository() {
        if(userRepository == null)
            userRepository = new UserRepository();
        return userRepository;
    }

    public User addNewUser(User user) {
        Connection connection = null;
        try {
            connection = dataBaseManager.getConnection();
            connection.setAutoCommit(false); // Включение транзакции

            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getLogin());
                statement.setString(2, user.getPassword());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1)); // Установка ID пользователя
                    }
                }
            }

            connection.commit(); // Подтверждение транзакции
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Откат транзакции в случае ошибки
                } catch (SQLException rollbackEx) {
                    System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            System.err.println("Произошла ошибка при создании пользователя: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Возврат автофиксации изменений
                    connection.close(); // Закрытие соединения
                } catch (SQLException closeEx) {
                    System.err.println("Ошибка при закрытии соединения: " + closeEx.getMessage());
                    closeEx.printStackTrace();
                }
            }
        }
        return user;
    }


    public User readUser(Long id) {
        User user = null;
        try (Connection connection = dataBaseManager.getConnection()) {
            String query = "SELECT * FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        user = User.createUser(resultSet.getString("username"), resultSet.getString("password"));
                        user.setId(resultSet.getLong("id"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Произошла ошибка при чтении пользователя: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }


    public void updateUser(User user) {
        Connection connection = null;
        try {
            connection = dataBaseManager.getConnection();
            connection.setAutoCommit(false); // Включение транзакции

            String query = "UPDATE users SET username = ?, password = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, user.getLogin());
                statement.setString(2, user.getPassword());
                statement.setLong(3, user.getId());
                statement.executeUpdate();
            }

            connection.commit(); // Подтверждение транзакции
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Откат транзакции в случае ошибки
                } catch (SQLException rollbackEx) {
                    System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            System.err.println("Произошла ошибка при обновлении пользователя: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Возврат автофиксации изменений
                    connection.close(); // Закрытие соединения
                } catch (SQLException closeEx) {
                    System.err.println("Ошибка при закрытии соединения: " + closeEx.getMessage());
                    closeEx.printStackTrace();
                }
            }
        }
    }


    public void deleteUser(Long id) {
        Connection connection = null;
        try {
            connection = dataBaseManager.getConnection();
            connection.setAutoCommit(false); // Включение транзакции

            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }

            connection.commit(); // Подтверждение транзакции
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Откат транзакции в случае ошибки
                } catch (SQLException rollbackEx) {
                    System.err.println("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            System.err.println("Произошла ошибка при удалении пользователя: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Возврат автофиксации изменений
                    connection.close(); // Закрытие соединения
                } catch (SQLException closeEx) {
                    System.err.println("Ошибка при закрытии соединения: " + closeEx.getMessage());
                    closeEx.printStackTrace();
                }
            }
        }
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataBaseManager.getConnection()) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {
                while (resultSet.next()) {
                    User user = User.createUser(resultSet.getString("username"), resultSet.getString("password"));
                    user.setId(resultSet.getLong("id"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Произошла ошибка при получении всех пользователей: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }


    public User getUserByUsername(String username) {
        User user = null;
        try (Connection connection = dataBaseManager.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        user = User.createUser(resultSet.getString("username"), resultSet.getString("password"));
                        user.setId(resultSet.getLong("id"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Произошла ошибка при получении пользователя по имени: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

}

