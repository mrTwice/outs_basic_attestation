package ru.otus.basic.yampolskiy.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.basic.yampolskiy.domain.entities.User;
import ru.otus.basic.yampolskiy.domain.services.UserService;
import ru.otus.basic.yampolskiy.servlets.HttpServlet;
import ru.otus.basic.yampolskiy.servlets.annotations.GetRoute;
import ru.otus.basic.yampolskiy.servlets.annotations.WebServlet;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/users")
public class UserController extends HttpServlet {
    private UserService userService = UserService.getUserService();
    private ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    @GetRoute()
    public HttpResponse getAllUsers(HttpRequest request) throws Exception {
        List<User> users = userService.getAllUsers();
        String responseBody = objectMapper.writeValueAsString(users);
        return new HttpResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .setBody(responseBody)
                .build();
    }

}
