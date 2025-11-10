package com.cafepos.state;

import com.cafepos.state.OrderFSM;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderFSMTest {

    @Test
    void order_fsm_happy_path() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());

        fsm.pay();
        assertEquals("PREPARING", fsm.status());

        fsm.markReady();
        assertEquals("READY", fsm.status());

        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
    }

    @Test
    void order_fsm_rejects_invalid_transitions() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());

        fsm.prepare(); // Should not change state
        assertEquals("NEW", fsm.status());

        fsm.cancel();
        assertEquals("CANCELLED", fsm.status());

        fsm.pay(); // Should not change state
        assertEquals("CANCELLED", fsm.status());
    }
}

