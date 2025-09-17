package com.cafepos.domain;

import java.util.concurrent.ThreadLocalRandom;

public class OrderIds {
    public static long next(){
        return ThreadLocalRandom.current().nextLong(1000, 10000);
    }
}
