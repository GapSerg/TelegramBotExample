package com.godeltech.springgodelbot.mapper;

import com.godeltech.springgodelbot.model.entity.*;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface TransferItemMapper {
    ChangeOfferRequest mapToChangeOfferRequest(TransferItem transferItem);

    TransferItem mapToTransferItem(Request request, User user, List<City> cities, ActivityType activityType);
}
