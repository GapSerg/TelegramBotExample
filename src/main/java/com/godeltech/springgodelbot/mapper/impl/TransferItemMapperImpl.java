package com.godeltech.springgodelbot.mapper.impl;

import com.godeltech.springgodelbot.mapper.TransferItemMapper;
import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.TransferItem;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
@Component
public class TransferItemMapperImpl implements TransferItemMapper {
    @Override
    public ChangeOfferRequest mapToChangeOfferRequest(TransferItem transferItem) {
        return null;
    }

    @Override
    public TransferItem mapToTransferItem(PassengerRequest passengerRequest, User user, List<City> cities) {
        return null;
    }
}
