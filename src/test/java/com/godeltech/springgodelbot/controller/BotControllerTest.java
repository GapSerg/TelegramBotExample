//package com.godeltech.springgodelbot.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.database.rider.core.api.configuration.DBUnit;
//import com.github.database.rider.core.api.configuration.Orthography;
//import com.github.database.rider.core.api.dataset.DataSet;
//import com.github.database.rider.core.api.dataset.ExpectedDataSet;
//import com.github.database.rider.junit5.api.DBRider;
//import com.godeltech.springgodelbot.SpringGodelBotApplication;
//import com.godeltech.springgodelbot.initializer.DatabaseContainerInitializer;
//import com.godeltech.springgodelbot.model.entity.Activity;
//import com.godeltech.springgodelbot.model.request.ChangeSupplierRequest;
//import com.godeltech.springgodelbot.model.request.ConsumerRequest;
//import com.godeltech.springgodelbot.model.request.SupplierRequest;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.MediaType;
//import org.springframework.http.RequestEntity;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//
//import java.net.URI;
//import java.time.LocalDate;
//import java.util.Map;
//
//import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
//import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
//import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.makeSendMessageForActivities;
//import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createSendMessageForFirstDate;
//import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createSendMessageForSecondDate;
//import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.*;
//import static com.godeltech.springgodelbot.util.CallbackUtil.SPLITTER;
//import static com.godeltech.springgodelbot.util.CallbackUtil.createSendMessageWithValidSecondDate;
//import static com.godeltech.springgodelbot.util.ConstantUtil.*;
//import static com.godeltech.springgodelbot.util.TestUtil.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(classes = SpringGodelBotApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DBRider
//@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
//@ActiveProfiles("test")
//class BotControllerTest extends DatabaseContainerInitializer {
//
//    @LocalServerPort
//    private int port;
//    @Autowired
//    private TestRestTemplate restTemplate;
//    @Autowired
//    private Map<Long, ChangeSupplierRequest> changeSupplierRequests;
//    @Autowired
//    private Map<Long, SupplierRequest> supplierRequests;
//    @Autowired
//    private Map<Long, ConsumerRequest> consumerRequests;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp(){
//        supplierRequests.remove(CHAT_ID);
//        consumerRequests.remove(CHAT_ID);
//        changeSupplierRequests.remove(CHAT_ID);
//    }
//
//    @AfterEach
//    public void tearDown(){
//        supplierRequests.remove(CHAT_ID);
//        consumerRequests.remove(CHAT_ID);
//        changeSupplierRequests.remove(CHAT_ID);
//    }
//
//    @Test
//    @SneakyThrows
//    @DataSet(value = {"dataset/init/user/init.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanUser.sql"})
//    @ExpectedDataSet(value = {"dataset/expected/user/save.yml"}, ignoreCols = {"registered_at"})
//    void onUpdateReceivedStartCommand() {
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(getUpdateForMessageWithEntities(), port, objectMapper),
//                String.class);
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(getStartMenu(getUpdateForMessageWithEntities().getMessage().getChatId(), "Hello")));
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedMakeRequestCallback() {
//        var update = getUpdateForCallback(MAKE_REQUEST.name());
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(makeSendMessageForActivities(update.getCallbackQuery())));
//    }
//
//
//    @Test
//    @DataSet(value = {"dataset/init/route/initRoute.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedActivitySupplierCallback() {
//        var routes = getRoutes();
//        var update = getUpdateForCallback(ACTIVITY + SPLITTER + Activity.SUPPLIER);
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createRouteSendMessage
//                (routes, Activity.SUPPLIER, update.getCallbackQuery().getMessage().getChatId())));
//        assertTrue(supplierRequests.containsKey(update.getCallbackQuery().getMessage().getChatId()));
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/route/initRoute.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedActivityConsumerCallback() {
//        var routes = getRoutes();
//        var update = getUpdateForCallback(ACTIVITY + SPLITTER + Activity.CONSUMER);
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createRouteSendMessage
//                (routes, Activity.CONSUMER, update.getCallbackQuery().getMessage().getChatId())));
//        assertTrue(consumerRequests.containsKey(update.getCallbackQuery().getMessage().getChatId()));
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/route/initRoute.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedActivitySupplierPlaceOfDepartureCallback() {
//        var routes = getRoutes();
//
//        var expectedRouteId = 1;
//        var update = getUpdateForCallback(PLACE_OF_DEPARTURE + SPLITTER + Activity.SUPPLIER + SPLITTER + expectedRouteId);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .build());
//
//
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createEditReplyKeyboardForArrival
//                (update.getCallbackQuery(), routes, 1, PLACE_OF_DEPARTURE + SPLITTER + Activity.SUPPLIER,
//                        PLACE_OF_ARRIVAL + SPLITTER + Activity.SUPPLIER)));
//        assertEquals(expectedRouteId, supplierRequests.get(update.getCallbackQuery().getMessage().getChatId()).getPlaceOfDeparture());
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/route/initRoute.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedActivityConsumerPlaceOfDepartureCallback() {
//        var routes = getRoutes();
//
//        var expectedRouteId = 1;
//        var update = getUpdateForCallback(PLACE_OF_DEPARTURE + SPLITTER + Activity.CONSUMER + SPLITTER + expectedRouteId);
//        consumerRequests.put(update.getCallbackQuery().getMessage().getChatId(), ConsumerRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .build());
//
//
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createEditReplyKeyboardForArrival
//                (update.getCallbackQuery(), routes, 1, PLACE_OF_DEPARTURE + SPLITTER + Activity.CONSUMER,
//                        PLACE_OF_ARRIVAL + SPLITTER + Activity.CONSUMER)));
//        assertEquals(expectedRouteId, consumerRequests.get(update.getCallbackQuery().getMessage().getChatId()).getPlaceOfDeparture());
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedActivitySupplierPlaceOfDepartureWithPlaceOfArrivalCallback() {
//        var expectedRouteId = 1;
//        var update = getUpdateForCallback(PLACE_OF_DEPARTURE + SPLITTER + Activity.SUPPLIER + SPLITTER + expectedRouteId);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfArrival(2)
//                .build());
//
//
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createSendMessageForFirstDate(update.getCallbackQuery().getMessage().getChatId(),
//                FIRST_DATE_SUPPLIER.name(), CHOOSE_FIRST_DATE_CALLBACK_TEXT)));
//        assertEquals(expectedRouteId, supplierRequests.get(update.getCallbackQuery().getMessage().getChatId()).getPlaceOfDeparture());
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedActivityConsumerPlaceOfDepartureWithPlaceOfArrivalCallback() {
//        var expectedRouteId = 1;
//        var update = getUpdateForCallback(PLACE_OF_DEPARTURE + SPLITTER + Activity.CONSUMER + SPLITTER + expectedRouteId);
//        consumerRequests.put(update.getCallbackQuery().getMessage().getChatId(), ConsumerRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfArrival(2)
//                .build());
//
//
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createSendMessageForFirstDate(update.getCallbackQuery().getMessage().getChatId(),
//                FIRST_DATE_CONSUMER.name(), CHOOSE_FIRST_DATE_CALLBACK_TEXT)));
//        assertEquals(expectedRouteId, consumerRequests.get(update.getCallbackQuery().getMessage().getChatId()).getPlaceOfDeparture());
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/route/initRoute.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanRoute.sql"})
//    @SneakyThrows
//    void onUpdateReceivedActivitySupplierPlaceOfArrivalCallback() {
//        var routes = getRoutes();
//
//        var expectedRouteId = 1;
//        var update = getUpdateForCallback(PLACE_OF_ARRIVAL + SPLITTER + Activity.SUPPLIER + SPLITTER + expectedRouteId);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(null)
//                .build());
//
//
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createEditReplyKeyboardForDeparture
//                (update.getCallbackQuery(), routes, 1, PLACE_OF_DEPARTURE + SPLITTER + Activity.SUPPLIER,
//                        PLACE_OF_ARRIVAL + SPLITTER + Activity.SUPPLIER)));
//        assertEquals(expectedRouteId, supplierRequests.get(update.getCallbackQuery().getMessage().getChatId()).getPlaceOfArrival());
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/route/initRoute.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanRoute.sql"})
//    @SneakyThrows
//    void onUpdateReceivedActivityConsumerPlaceOfArrivalCallback() {
//        var routes = getRoutes();
//
//        var expectedRouteId = 1;
//        var update = getUpdateForCallback(PLACE_OF_ARRIVAL + SPLITTER + Activity.CONSUMER + SPLITTER + expectedRouteId);
//        consumerRequests.put(update.getCallbackQuery().getMessage().getChatId(), ConsumerRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(null)
//                .build());
//
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createEditReplyKeyboardForDeparture
//                (update.getCallbackQuery(), routes, 1, PLACE_OF_DEPARTURE + SPLITTER + Activity.CONSUMER,
//                        PLACE_OF_ARRIVAL + SPLITTER + Activity.CONSUMER)));
//        assertEquals(expectedRouteId, consumerRequests.get(update.getCallbackQuery().getMessage().getChatId()).getPlaceOfArrival());
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedActivitySupplierPlaceOfArrivalWithPlaceOfDepartureCallback() {
//        var expectedRouteId = 1;
//        var update = getUpdateForCallback(PLACE_OF_ARRIVAL + SPLITTER + Activity.SUPPLIER + SPLITTER + expectedRouteId);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(2)
//                .placeOfArrival(null)
//                .build());
//
//
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createSendMessageForFirstDate(update.getCallbackQuery().getMessage().getChatId(),
//                FIRST_DATE_SUPPLIER.name(), CHOOSE_FIRST_DATE_CALLBACK_TEXT)));
//        assertEquals(expectedRouteId, supplierRequests.get(update.getCallbackQuery().getMessage().getChatId()).getPlaceOfArrival());
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedActivityConsumerPlaceOfArrivalWithPlaceOfDepartureCallback() {
//        var expectedRouteId = 1;
//        var update = getUpdateForCallback(PLACE_OF_ARRIVAL + SPLITTER + Activity.CONSUMER + SPLITTER + expectedRouteId);
//        consumerRequests.put(update.getCallbackQuery().getMessage().getChatId(), ConsumerRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(2)
//                .placeOfArrival(null)
//                .build());
//
//
//        ResponseEntity<String> sendMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class);
//
//
//        assertEquals(sendMessage.getBody(), objectMapper.writeValueAsString(createSendMessageForFirstDate(update.getCallbackQuery().getMessage().getChatId(),
//                FIRST_DATE_CONSUMER.name(), CHOOSE_FIRST_DATE_CALLBACK_TEXT)));
//        assertEquals(expectedRouteId, consumerRequests.get(update.getCallbackQuery().getMessage().getChatId()).getPlaceOfArrival());
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedFirstDateSupplierCallbackWithValidDate() {
//        var expectedDate = LocalDate.now().plusDays(1);
//        var update = getUpdateForCallback(FIRST_DATE_SUPPLIER + SPLITTER + expectedDate);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(1)
//                .placeOfArrival(2)
//                .firstDate(null)
//                .build());
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//
//        assertEquals(expectedDate, supplierRequests.get(update.getCallbackQuery().getMessage().getChatId()).getFirstDate());
//        assertEquals(objectMapper.writeValueAsString(createSendMessageForSecondDate(update.getCallbackQuery().getMessage().getChatId()
//                        , CHOOSE_SECOND_DATE_CALLBACK_TEXT, SECOND_DATE_SUPPLIER.name()))
//                , actualMessage);
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedFirstDateSupplierCallbackWithInvalidDate() {
//        var invalidDate = LocalDate.now().minusDays(1);
//        var update = getUpdateForCallback(FIRST_DATE_SUPPLIER + SPLITTER + invalidDate);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(1)
//                .placeOfArrival(2)
//                .firstDate(null)
//                .build());
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//
//        assertNull(supplierRequests.get(update.getCallbackQuery().getMessage().getChatId()).getFirstDate());
//        assertEquals(objectMapper.writeValueAsString(createSendMessageForFirstDate(update.getCallbackQuery().getMessage().getChatId(), FIRST_DATE_SUPPLIER.name()
//                        , INVALID_DATE_CALLBACK_TEXT))
//                , actualMessage);
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedFirstDateConsumerCallbackWithValidDate() {
//        var expectedDate = LocalDate.now().plusDays(1);
//        var update = getUpdateForCallback(FIRST_DATE_CONSUMER + SPLITTER + expectedDate);
//        consumerRequests.put(update.getCallbackQuery().getMessage().getChatId(), ConsumerRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(1)
//                .placeOfArrival(2)
//                .firstDate(null)
//                .build());
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//
//        assertEquals(expectedDate, consumerRequests.get(update.getCallbackQuery().getMessage().getChatId()).getFirstDate());
//        assertEquals(objectMapper.writeValueAsString(createSendMessageForSecondDate(update.getCallbackQuery().getMessage().getChatId()
//                        , CHOOSE_SECOND_DATE_CALLBACK_TEXT, SECOND_DATE_CONSUMER.name()))
//                , actualMessage);
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedFirstDateConsumerCallbackWithInvalidDate() {
//        var invalidDate = LocalDate.now().minusDays(1);
//        var update = getUpdateForCallback(FIRST_DATE_CONSUMER + SPLITTER + invalidDate);
//        consumerRequests.put(update.getCallbackQuery().getMessage().getChatId(), ConsumerRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(1)
//                .placeOfArrival(2)
//                .firstDate(null)
//                .build());
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//
//        assertNull(consumerRequests.get(update.getCallbackQuery().getMessage().getChatId()).getFirstDate());
//        assertEquals(objectMapper.writeValueAsString(createSendMessageForFirstDate(update.getCallbackQuery().getMessage().getChatId(), FIRST_DATE_CONSUMER.name()
//                        , INVALID_DATE_CALLBACK_TEXT))
//                , actualMessage);
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedSecondDateSupplierCallbackWithValidDate() {
//        var expectedDate = LocalDate.now().plusDays(5);
//        var update = getUpdateForCallback(SECOND_DATE_SUPPLIER + SPLITTER + expectedDate);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(1)
//                .placeOfArrival(2)
//                .firstDate(LocalDate.now().plusDays(1))
//                .build());
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//
//        assertEquals(expectedDate, supplierRequests.get(update.getCallbackQuery().getMessage().getChatId()).getSecondDate());
//        assertEquals(objectMapper.writeValueAsString(createSendMessageWithValidSecondDate(update.getCallbackQuery()))
//                , actualMessage);
//    }
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedSecondDateSupplierCallbackWithInValidDate() {
//        var invalidDate = LocalDate.now().minusDays(5);
//        var update = getUpdateForCallback(SECOND_DATE_SUPPLIER + SPLITTER + invalidDate);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .placeOfDeparture(1)
//                .placeOfArrival(2)
//                .firstDate(LocalDate.now().plusDays(1))
//                .secondDate(null)
//                .build());
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//
//        assertNull(supplierRequests.get(update.getCallbackQuery().getMessage().getChatId()).getSecondDate());
//        assertEquals(objectMapper.writeValueAsString(createSendMessageForSecondDate(update.getCallbackQuery().getMessage().getChatId(), INVALID_DATE_CALLBACK_TEXT, SECOND_DATE_SUPPLIER.name()))
//                , actualMessage);
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @ExpectedDataSet(value = {"dataset/expected/supplier/save.yml"},
//            ignoreCols = {"first_date", "second_date"})
//    @SneakyThrows
//    void onUpdateReceivedSaveSupplierCallbackWithoutDescription() {
//        var update = getUpdateForCallback(SAVE_SUPPLIER + SPLITTER);
//        supplierRequests.put(update.getCallbackQuery().getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getCallbackQuery().getMessage().getChatId())
//                .userId(update.getCallbackQuery().getFrom().getId())
//                .placeOfDeparture(1)
//                .placeOfArrival(2)
//                .firstDate(LocalDate.now().plusDays(1))
//                .secondDate(LocalDate.now().plusDays(2))
//                .build());
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(getStartMenu(update.getCallbackQuery().getMessage().getChatId(), SUCCESSFUL_REQUEST_SAVING))
//                , actualMessage);
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @ExpectedDataSet(value = {"dataset/expected/supplier/description.yml"},
//            ignoreCols = {"first_date", "second_date"})
//    @SneakyThrows
//    void onUpdateReceivedSaveSupplierCallbackWithDescription() {
//        var update = getUpdateForMessageWithoutEntities("Ya yedu i mogu vziat' tebya");
//        supplierRequests.put(update.getMessage().getChatId(), SupplierRequest.builder()
//                .chatId(update.getMessage().getChatId())
//                .userId(update.getMessage().getFrom().getId())
//                .placeOfDeparture(1)
//                .placeOfArrival(2)
//                .firstDate(LocalDate.now().plusDays(1))
//                .secondDate(LocalDate.now().plusDays(2))
//                .isNeedForDescription(true)
//                .build());
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(getStartMenu(update.getMessage().getChatId(), SUCCESSFUL_REQUEST_SAVING))
//                , actualMessage);
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @ExpectedDataSet(value = "dataset/expected/supplier/delete.yml")
//    @SneakyThrows
//    void onUpdateReceivedDeleteSupplierCallback() {
//        var update = getUpdateForCallback(DELETE_SUPPLIER_OFFER.name()+SPLITTER+1);
//
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(getStartMenu(update.getCallbackQuery().getMessage().getChatId(), OFFER_WAS_DELETED))
//                , actualMessage);
//    }
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedChangeRouteOfferCallback() {
//        var expectedSupplierId = 1L;
//        var routes= getRoutes();
//        var update = getUpdateForCallback(CHANGE_ROUTE_OF_OFFER.name()+SPLITTER+expectedSupplierId);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(expectedSupplierId)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .build();
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(createRouteSendMessageForChanging(routes,CHAT_ID))
//                , actualMessage);
//        assertEquals(expectedChangeSupplierRequest,changeSupplierRequests.get(CHAT_ID));
//    }
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedChangePlaceOfArrivalWithoutPlaceOfDepartureOfferCallback() {
//        var expectedSupplierId = 1L;
//        var expectedRouteId = 3;
//        var routes= getRoutes();
//        var update = getUpdateForCallback(CHANGE_PLACE_OF_ARRIVAL.name()+SPLITTER+expectedRouteId);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(expectedSupplierId)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .build();
//        changeSupplierRequests.put(CHAT_ID,expectedChangeSupplierRequest);
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(createEditReplyKeyboardForArrival(update.getCallbackQuery(),
//                        routes,expectedRouteId,CHANGE_PLACE_OF_DEPARTURE.name(),CHANGE_PLACE_OF_ARRIVAL.name()))
//                , actualMessage);
//        assertEquals(expectedRouteId,changeSupplierRequests.get(CHAT_ID).getPlaceOfArrival());
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @ExpectedDataSet(value = {"dataset/expected/supplier/updateRoute.yml"},ignoreCols = {"first_date","second_date"})
//    @SneakyThrows
//    void onUpdateReceivedChangePlaceOfArrivalWithPlaceOfDepartureOfferCallback() {
//        var expectedSupplierId = 1L;
//        var expectedRouteId = 3;
//        var update = getUpdateForCallback(CHANGE_PLACE_OF_ARRIVAL.name()+SPLITTER+expectedRouteId);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(expectedSupplierId)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .placeOfDeparture(4)
//                .build();
//        changeSupplierRequests.put(CHAT_ID,expectedChangeSupplierRequest);
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(getStartMenu(CHAT_ID,ROUTE_WAS_CHANGED))
//                , actualMessage);
//        assertNull(changeSupplierRequests.get(CHAT_ID));
//    }
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @ExpectedDataSet(value = {"dataset/expected/supplier/updateRoute.yml"},ignoreCols = {"first_date","second_date"})
//    @SneakyThrows
//    void onUpdateReceivedChangePlaceOfDepartureWithPlaceOfArrivalOfferCallback() {
//        var expectedSupplierId = 1L;
//        var expectedRouteId = 4;
//        var update = getUpdateForCallback(CHANGE_PLACE_OF_DEPARTURE.name()+SPLITTER+expectedRouteId);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(expectedSupplierId)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .placeOfArrival(3)
//                .build();
//        changeSupplierRequests.put(CHAT_ID,expectedChangeSupplierRequest);
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(getStartMenu(CHAT_ID,ROUTE_WAS_CHANGED))
//                , actualMessage);
//        assertNull(changeSupplierRequests.get(CHAT_ID));
//    }
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedChangePlaceOfDepartureWithoutPlaceOfArrivalOfferCallback() {
//        var expectedSupplierId = 1L;
//        var expectedRouteId = 4;
//        var routes= getRoutes();
//        var update = getUpdateForCallback(CHANGE_PLACE_OF_DEPARTURE.name()+SPLITTER+expectedRouteId);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(expectedSupplierId)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .build();
//        changeSupplierRequests.put(CHAT_ID,expectedChangeSupplierRequest);
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(createEditReplyKeyboardForDeparture(update.getCallbackQuery(),
//                        routes,expectedRouteId,CHANGE_PLACE_OF_DEPARTURE.name(),CHANGE_PLACE_OF_ARRIVAL.name()))
//                , actualMessage);
//        assertEquals(expectedRouteId,changeSupplierRequests.get(CHAT_ID).getPlaceOfDeparture());
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedChangeDescriptionOfOfferCallback() {
//        var expectedSupplierId = 1L;
//        var update = getUpdateForCallback(CHANGE_DESCRIPTION_OF_OFFER.name()+SPLITTER+expectedSupplierId);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(expectedSupplierId)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .isNeedForDescription(true)
//                .build();
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(SendMessage.builder()
//                        .text(WRITE_ADDITIONAL_DESCRIPTION_FOR_CHANGE)
//                        .chatId(String.valueOf(CHAT_ID))
//                .build())
//                , actualMessage);
//        assertEquals(expectedChangeSupplierRequest,changeSupplierRequests.get(CHAT_ID));
//    }
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @ExpectedDataSet(value = {"dataset/expected/supplier/updateDescription.yml"},ignoreCols = {"first_date","second_date"})
//    @SneakyThrows
//    void onUpdateReceivedChangeDescriptionOfOfferMessage() {
//        var expectedSupplierId = 1L;
//        var update = getUpdateForMessageWithoutEntities("Ya uzhe izmenil description");
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(expectedSupplierId)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .isNeedForDescription(true)
//                .build();
//        changeSupplierRequests.put(CHAT_ID,expectedChangeSupplierRequest);
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(getStartMenu(CHAT_ID,DESCRIPTION_WAS_UPDATED))
//                , actualMessage);
//        assertNull(changeSupplierRequests.get(CHAT_ID));
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @SneakyThrows
//    void onUpdateReceivedChangeDateOfOfferMessage() {
//        var expectedSupplierId = 1L;
//        var update = getUpdateForCallback(CHANGE_DATE_OF_OFFER.name()+SPLITTER+expectedSupplierId);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(expectedSupplierId)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .build();
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(createSendMessageForFirstDate(CHAT_ID,CHANGE_FIRST_DATE_OF_OFFER.name(),CHOOSE_FIRST_DATE_CALLBACK_TEXT))
//                , actualMessage);
//        assertEquals(expectedChangeSupplierRequest,changeSupplierRequests.get(CHAT_ID));
//    }
//
//
//    @Test
//    @SneakyThrows
//    void onUpdateReceivedChangeFirstDateOfOfferMessage() {
//        var expectedFirstDate = LocalDate.now().plusDays(1);
//        var update = getUpdateForCallback(CHANGE_FIRST_DATE_OF_OFFER.name()+SPLITTER+expectedFirstDate);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(1L)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .build();
//        changeSupplierRequests.put(CHAT_ID,expectedChangeSupplierRequest);
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(createSendMessageForSecondDate(CHAT_ID,CHOOSE_SECOND_DATE_CALLBACK_TEXT,CHANGE_SECOND_DATE_OF_OFFER.name()))
//                , actualMessage);
//        assertEquals(expectedFirstDate,changeSupplierRequests.get(CHAT_ID).getFirstDate());
//    }
//
//    @Test
//    @DataSet(value = {"dataset/init/user/initUserForSave.yml", "dataset/init/route/initRoute.yml", "dataset/init/supplier/initSupplier.yml"},
//            useSequenceFiltering = false,
//            executeScriptsAfter = {"scripts/cleanSupplier.sql", "scripts/cleanRoute.sql", "scripts/cleanUser.sql"})
//    @ExpectedDataSet(value = {"dataset/expected/supplier/updateDate.yml"})
//    @SneakyThrows
//    void onUpdateReceivedChangeSecondDateOfOfferMessage() {
//        var expectedFirstDate = LocalDate.parse("2030-10-10");
//        var expectedSecondDate = expectedFirstDate.plusDays(5);
//        var update = getUpdateForCallback(CHANGE_SECOND_DATE_OF_OFFER.name()+SPLITTER+expectedSecondDate);
//        var expectedChangeSupplierRequest = ChangeSupplierRequest.builder()
//                .supplierId(1L)
//                .chatId(CHAT_ID)
//                .userId(USER_ID)
//                .firstDate(expectedFirstDate)
//                .build();
//        changeSupplierRequests.put(CHAT_ID,expectedChangeSupplierRequest);
//        var actualMessage = restTemplate.exchange(makePostRequestEntity(update, port, objectMapper),
//                String.class).getBody();
//        assertEquals(objectMapper.writeValueAsString(getStartMenu(CHAT_ID,DATES_WERE_CHANGED))
//                , actualMessage);
//        assertNull(changeSupplierRequests.get(CHAT_ID));
//    }
//    @Test
//    @SneakyThrows
//    void getMessageTest() {
//        ResponseEntity<String> sendMessage = restTemplate.exchange(RequestEntity
//                        .get(new URI("http://localhost:" + port))
//                        .accept(MediaType.APPLICATION_JSON)
//                        .build(),
//                String.class);
//        assertEquals(sendMessage.getBody(), "prishloopyat'");
//    }
//
//}
