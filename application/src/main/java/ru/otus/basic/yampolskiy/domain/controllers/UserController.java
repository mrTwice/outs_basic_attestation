package ru.otus.basic.yampolskiy.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.domain.entities.User;
import ru.otus.basic.yampolskiy.domain.entities.UserUpdateDTO;
import ru.otus.basic.yampolskiy.domain.services.UserService;
import ru.otus.basic.yampolskiy.servlets.annotations.*;
import ru.otus.basic.yampolskiy.servlets.models.HttpServlet;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletResponse;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/users")
public class UserController extends HttpServlet {
    private final UserService userService = UserService.getUserService();
    private final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    private final Logger logger = LogManager.getLogger(this);

    @GetRoute()
    public HttpServletResponse getAllUsers(HttpServletRequest request) throws Exception {
        List<User> users = userService.getAllUsers();
        String responseBody = objectMapper.writeValueAsString(users);
        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .setContentType("application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .setBody(responseBody)
                .build();

    }

    @GetRoute("/{id}")
    public HttpServletResponse getUserById(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        logger.debug("getUserById: Получен HttpServletRequest: " + request);
        logger.debug("getUserById: Получен id: " + id);
        String responseBody = objectMapper.writeValueAsString(userService.getUserById(id));
        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .setContentType("application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .addHeader(HttpHeader.CONNECTION, "close")
                .setBody(responseBody)
                .build();
    }

    @DeleteRoute("/{id}")
    public HttpServletResponse deleteUserById(HttpServletRequest request, @PathVariable("id") Long id) throws Exception {
        logger.debug("getUserById: Получен HttpServletRequest: " + request);
        logger.debug("getUserById: Получен id: " + id);
        userService.deleteUserById(id);
        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .setContentType("application/octet-stream")
                .addHeader(HttpHeader.CONNECTION, "close")
                .setBody("")
                .build();
    }

    @PutRoute("/{id}")
    public HttpServletResponse updateUserById(HttpServletRequest request, @PathVariable("id") Long id, @RequestBody UserUpdateDTO userUpdateDTO) throws Exception {
        User user = userService.getUserById(id);
        user.setPassword(userUpdateDTO.getPassword());
        userService.updateUserById(user);
        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .setContentType("application/json")
                .setContentType("application/octet-stream")
                .addHeader(HttpHeader.CONNECTION, "close")
                .setBody("")
                .build();
    }

    @GetRoute("/search")
    public HttpServletResponse searchUserByUsername(HttpServletRequest request, @RequestParam("username") String username) throws Exception {
        User users = userService.getUserByLogin(username);
        String responseBody = objectMapper.writeValueAsString(users);
        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .setContentType("application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .addHeader(HttpHeader.CONNECTION, "close")
                .setBody(responseBody)
                .build();
    }
}
