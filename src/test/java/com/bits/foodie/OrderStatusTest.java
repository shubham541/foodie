package com.bits.foodie;

import com.bits.foodie.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import static com.bits.foodie.domain.OrderStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void happyPathIsLinear() {
        assertThat(PLACED.next()).isEqualTo(ACCEPTED);
        assertThat(ACCEPTED.next()).isEqualTo(PREPARING);
        assertThat(PREPARING.next()).isEqualTo(READY_FOR_PICKUP);
        assertThat(READY_FOR_PICKUP.next()).isEqualTo(COLLECTED);
    }

    @Test
    void terminalStatesHaveNoNext() {
        for (OrderStatus s : new OrderStatus[]{COLLECTED, CANCELLED}) {
            assertThat(s.isTerminal()).isTrue();
            assertThat(s.next()).isNull();
        }
        assertThat(PLACED.isTerminal()).isFalse();
        assertThat(READY_FOR_PICKUP.isTerminal()).isFalse();
    }
}
