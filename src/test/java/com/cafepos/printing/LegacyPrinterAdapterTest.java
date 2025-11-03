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
        com.cafepos.printing.Printer p = new com.cafepos.printing.LegacyPrinterAdapter(fake);
        p.print("ABC");
        org.junit.jupiter.api.Assertions.assertTrue(fake.lastLen >= 3);
    }

    @org.junit.jupiter.api.Test
    void adapter_handles_empty_string() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        p.print("");
        org.junit.jupiter.api.Assertions.assertEquals(0, fake.lastLen);
    }

    @org.junit.jupiter.api.Test
    void adapter_encodes_unicode_correctly() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        p.print("Café ☕");
        org.junit.jupiter.api.Assertions.assertTrue(fake.lastLen >= 7); // UTF-8 bytes
    }

    @org.junit.jupiter.api.Test
    void adapter_handles_null_input_gracefully() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            p.print(null);
        });
    }

    @org.junit.jupiter.api.Test
    void adapter_encodes_special_characters() {
        var fake = new FakeLegacy();
        Printer p = new LegacyPrinterAdapter(fake);
        p.print("Item\tQty\nLatte\nTotal: €5.00");
        org.junit.jupiter.api.Assertions.assertTrue(fake.lastLen > 10);
    }
}


