package com.godeltech.springgodelbot.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godeltech.springgodelbot.model.entity.City;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.telegram.telegrambots.meta.api.objects.*;

import java.net.URI;
import java.util.List;

public class TestUtil {

    public static final long CHAT_ID = 848575L;
    public static final long USER_ID = 84562L;

    public static User getUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setIsBot(false);
        user.setUserName("Myachin99");
        user.setLastName("Myachin");
        user.setFirstName("Artem");
        user.setLanguageCode("en");
        return user;
    }

    public static Chat getChat() {
        Chat chat = new Chat();
        User user = getUser();
        chat.setFirstName(user.getFirstName());
        chat.setLastName(user.getLastName());
        chat.setUserName(user.getUserName());
        chat.setType("private");
        chat.setId(CHAT_ID);
        return chat;
    }

    public static List<MessageEntity> getEntities() {
        return List.of(MessageEntity.builder()
                .type("bot_command")
                .text("/start")
                .offset(0)
                .length("/start".length())
                .build());
    }

    public static Message getMessageWithEntities() {
        Message message = new Message();
        message.setChat(getChat());
        message.setFrom(getUser());
        message.setEntities(getEntities());
        message.setMessageId(1084);
        message.setText("/start");
        return message;
    }

    public static Message getMessageWithoutEntities(String text) {
        Message message = new Message();
        message.setChat(getChat());
        message.setFrom(getUser());
        message.setMessageId(1084);
        message.setText(text);
        return message;
    }

    public static Message getMessageForCallback() {
        Message message = new Message();
        message.setChat(getChat());
        message.setFrom(getUser());
        message.setMessageId(1084);
        return message;
    }

    public static Update getUpdateForMessageWithEntities() {
        Update update = new Update();
        update.setMessage(getMessageWithEntities());
        update.setUpdateId(816546);
        return update;
    }

    public static Update getUpdateForMessageWithoutEntities(String message) {
        Update update = new Update();
        update.setMessage(getMessageWithoutEntities(message));
        update.setUpdateId(816546);
        return update;
    }

    public static CallbackQuery getCallbackQuery(String data) {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setId("19283120312");
        callbackQuery.setFrom(getUser());
        callbackQuery.setMessage(getMessageForCallback());
        callbackQuery.setData(data);
        return callbackQuery;
    }

    public static CallbackQuery getCallbackQuery() {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setId("19283120312");
        callbackQuery.setFrom(getUser());
        callbackQuery.setMessage(getMessageForCallback());
        return callbackQuery;
    }

    public static Update getUpdateForCallback(String data) {
        Update update = new Update();
        update.setCallbackQuery(getCallbackQuery(data));
        update.setUpdateId(816546);
        return update;
    }

    public static List<City> getRoutes() {
        return List.of(City.builder()
                        .id(1)
                        .name("MINSK")
                        .build(),
                City.builder()
                        .id(2)
                        .name("GRODNO")
                        .build(),
                City.builder()
                        .id(3)
                        .name("GDANSK")
                        .build(),
                City.builder()
                        .id(4)
                        .name("WROCLAW")
                        .build());
    }

    @SneakyThrows
    public static RequestEntity<String> makePostRequestEntity(Update update, int port, ObjectMapper objectMapper) {
        return RequestEntity
                .post(new URI("http://localhost:" + port))
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(update), Update.class);
    }
}
