package com.bits.foodie.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlatformActivityDTO {
    private int activeUsersCount;
    private int ongoingDeliveriesCount;
    private int totalOrdersCount;
    private int pendingOrdersCount;
    private int completedOrdersCount;
}
