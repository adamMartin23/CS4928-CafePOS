package com.cafepos.printing;



class LegacyPrinterAdapterTest {

    class FakeLegacy extends vendor.legacy.LegacyThermalPrinter {
        int lastLen = -1;
        @Override public void legacyPrint(byte[] payload) { lastLen =
                payload.length; }
    }

    @org.junit.jupiter.api.Test
    void adapter_converts_text_to_bytes() {
        var fake = new FakeLegacy();
        com.cafepos.printing.Printer p = new
                com.cafepos.printing.LegacyPrinterAdapter(fake);
        p.print("ABC");
        org.junit.jupiter.api.Assertions.assertTrue(fake.lastLen >= 3);
    }
}


