package com.adrastel.niviel.assets;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AssetsTest {

    @Test
    public void getCubeId() throws Exception {
        assertEquals(Assets.getCubeId(0), "333");
        assertEquals(Assets.getCubeId(1), "444");
        assertEquals(Assets.getCubeId(2), "555");
        assertEquals(Assets.getCubeId(3), "222");
        assertEquals(Assets.getCubeId(4), "333bf");
        assertEquals(Assets.getCubeId(5), "333oh");
        assertEquals(Assets.getCubeId(6), "333fm");
        assertEquals(Assets.getCubeId(7), "333ft");
        assertEquals(Assets.getCubeId(8), "minx");
        assertEquals(Assets.getCubeId(9), "pyram");
        assertEquals(Assets.getCubeId(10), "sq1");
        assertEquals(Assets.getCubeId(11), "clock");
        assertEquals(Assets.getCubeId(12), "skewb");
        assertEquals(Assets.getCubeId(13), "666");
        assertEquals(Assets.getCubeId(14), "777");
        assertEquals(Assets.getCubeId(15), "444bf");
        assertEquals(Assets.getCubeId(16), "555bf");
        assertEquals(Assets.getCubeId(17), "333mbf");
    }

}