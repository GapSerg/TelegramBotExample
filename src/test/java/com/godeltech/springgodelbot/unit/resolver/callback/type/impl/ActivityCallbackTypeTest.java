//package com.godeltech.springgodelbot.unit.resolver.callback.type.impl;
//
//import com.godeltech.springgodelbot.model.entity.Activity;
//import com.godeltech.springgodelbot.model.entity.Route;
//import com.godeltech.springgodelbot.model.request.ConsumerRequest;
//import com.godeltech.springgodelbot.model.request.SupplierRequest;
//import com.godeltech.springgodelbot.resolver.callback.Callbacks;
//import com.godeltech.springgodelbot.resolver.callback.type.impl.ActivityCallbackType;
//import com.godeltech.springgodelbot.service.RequestService;
//import com.godeltech.springgodelbot.service.RouteService;
//import com.godeltech.springgodelbot.util.CallbackUtil;
//import com.godeltech.springgodelbot.util.TestUtil;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
//import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
//
//import java.util.List;
//
//import static com.godeltech.springgodelbot.util.CallbackUtil.SPLITTER;
//import static com.godeltech.springgodelbot.util.TestUtil.CHAT_ID;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ActivityCallbackTypeTest {
//    @Mock
//    private RequestService requestService;
//    @Mock
//    private RouteService routeService;
//    CallbackQuery callbackQuery;
//    private List<Route> expectedRoutes;
//    private SupplierRequest supplierRequest;
//    private ConsumerRequest consumerRequest;
//    @BeforeEach
//    void setUp() {
//        callbackQuery= TestUtil.getCallbackQuery();
//        supplierRequest=SupplierRequest.builder()
//                .userId(callbackQuery.getFrom().getId())
//                .chatId(CHAT_ID)
//                .build();
//        consumerRequest= ConsumerRequest.builder()
//                .chatId(CHAT_ID)
//                .build();
//        expectedRoutes=TestUtil.getRoutes();
//    }
//
//    @AfterEach
//    void tearDown() {
//        callbackQuery=null;
//        supplierRequest=null;
//        expectedRoutes=null;
//    }
//
//    @InjectMocks
//    private ActivityCallbackType activityCallbackType;
//
//    @Test
//    public void createSendMessageSupplier() {
//        callbackQuery.setData(Callbacks.ACTIVITY+SPLITTER+ Activity.SUPPLIER);
//        when(routeService.findAll()).thenReturn(expectedRoutes);
//        doNothing().when(requestService).saveSupplierRequest(supplierRequest);
//        BotApiMethod actual = activityCallbackType.createSendMessage(callbackQuery);
//        assertEquals(actual, CallbackUtil.RouteUtil.createRouteSendMessage(expectedRoutes,Activity.SUPPLIER,CHAT_ID));
//        verify(requestService).saveSupplierRequest(supplierRequest);
//        verify(routeService).findAll();
//    }
//
//    @Test
//    public void createSendMessageConsumer() {
//        callbackQuery.setData(Callbacks.ACTIVITY+SPLITTER+ Activity.CONSUMER);
//        when(routeService.findAll()).thenReturn(expectedRoutes);
//        doNothing().when(requestService).saveConsumerRequest(consumerRequest);
//        BotApiMethod actual = activityCallbackType.createSendMessage(callbackQuery);
//        assertEquals(actual, CallbackUtil.RouteUtil.createRouteSendMessage(expectedRoutes,Activity.CONSUMER,CHAT_ID));
//        verify(requestService).saveConsumerRequest(consumerRequest);
//        verify(routeService).findAll();
//    }
//
//    @Test
//    public void createSendMessageFailed() {
//        callbackQuery.setData(Callbacks.ACTIVITY+SPLITTER+"sdadasda");
//        var actual=assertThrows(IllegalArgumentException.class,
//                ()->activityCallbackType.createSendMessage(callbackQuery));
//        verify(routeService,never()).findAll();
//    }
//    }
