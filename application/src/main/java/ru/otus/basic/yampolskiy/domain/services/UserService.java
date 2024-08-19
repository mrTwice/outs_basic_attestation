package ru.otus.basic.yampolskiy.domain.services;

import ru.otus.basic.yampolskiy.domain.entities.User;
import ru.otus.basic.yampolskiy.domain.entities.UserRegistrationDTO;
import ru.otus.basic.yampolskiy.domain.exceptions.UserAlreadyExistException;
import ru.otus.basic.yampolskiy.domain.exceptions.UserNotFoundException;
import ru.otus.basic.yampolskiy.domain.repositories.UserRepository;

import java.util.List;

public class UserService {
    private static UserService userService;
    private final UserRepository userRepository = UserRepository.getUserRepository();

    private UserService( ) {
    }

    public List<User> getAllUsers () {
        return userRepository.getAllUsers();
    }

    public static UserService getUserService() {
        if (userService == null)
            userService = new UserService();
        return userService;
    }

    public User createNewUser(UserRegistrationDTO user) {
        User existUser = userRepository.getUserByLogin(user.getLogin());
        if(existUser != null){
            throw new UserAlreadyExistException("Пользователь с таким именем существует");
        }
        User newUser = userRepository.addNewUser(User.createUser(user.getLogin(), user.getPassword()));
        return newUser;
    }

    public User getUserById(Long id) {
        User user = userRepository.readUser(id);
        if(user == null) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не существует");
        }
        return user;
    }

    public void updateUserById(User user) {
        User existUser = userRepository.readUser(user.getId());
        if(existUser == null){
            throw new UserNotFoundException("Пользователь с таким id не существует");
        }
        userRepository.updateUser(user);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.readUser(id);
        if(user == null) {
            throw new UserNotFoundException("Пользователь с id = " + id + " не существует");
        }
        userRepository.deleteUser(id);
    }

    public User getUserByLogin(String login) {
        User user = userRepository.getUserByLogin(login);
        if(user == null)
            throw new UserNotFoundException("Пользователя с таким именем не существует");
        return user;
    }
}
