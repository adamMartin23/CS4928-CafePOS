package com.cafepos.Observer;

import com.cafepos.domain.Order;

public interface OrderObserver {
    void updated(Order order, String eventType);
}
