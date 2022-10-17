package com.godeltech.springgodelbot.mapper;

import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.TransferItem;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface TransferItemMapper {
    ChangeOfferRequest mapToChangeOfferRequest(TransferItem transferItem);

    TransferItem mapToTransferItem(PassengerRequest passengerRequest, User user, List<City> cities);
}
