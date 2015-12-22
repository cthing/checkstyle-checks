/*
 * Copyright 2012 C Thing Software
 * All rights reserved.
 */
package checkstyle;

public class TestMethodDeclaration {

    public int getValue() {
        return 5;
    }

    @Test
    public void testGoodTypical() {
    }

    @Test(enabled="false")
    @SuppressWarnings("unused")
    public void testGoodWithNoise() {
    }

    @Test
    public int testBadReturn() {
        return 4;
    }

    @Test
    private void testBadVisibility1() {
    }

    @Test
    void testBadVisibility2() {
    }

    @Test
    public static void testBadInstance() {
    }
}
