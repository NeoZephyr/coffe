package com.jerry;

import org.junit.Assert;
import org.junit.Test;

public class IntensitySegmentsTest {

    @Test
    public void testAddCase1() {
        IntensitySegments segments = new IntensitySegments();
        Assert.assertEquals("[]", segments.toString());
        segments.add(10, 30, 1);
        Assert.assertEquals("[[10,1],[30,0]]", segments.toString());
        segments.add(20, 40, 1);
        Assert.assertEquals("[[10,1],[20,2],[30,1],[40,0]]", segments.toString());
        segments.add(10, 40, -2);
        Assert.assertEquals(segments.toString(), "[[10,-1],[20,0],[30,-1],[40,0]]");
    }

    @Test
    public void testAddCase2() {
        IntensitySegments segments = new IntensitySegments();
        Assert.assertEquals("[]", segments.toString());
        segments.add(10, 30, 1);
        Assert.assertEquals("[[10,1],[30,0]]", segments.toString());
        segments.add(20, 40, 1);
        Assert.assertEquals("[[10,1],[20,2],[30,1],[40,0]]", segments.toString());
        segments.add(10, 40, -1);
        Assert.assertEquals("[[20,1],[30,0]]", segments.toString());
        segments.add(10, 40, -1);
        Assert.assertEquals("[[10,-1],[20,0],[30,-1],[40,0]]", segments.toString());
    }

    @Test
    public void testAddCase3() {
        IntensitySegments segments = new IntensitySegments();
        Assert.assertEquals("[]", segments.toString());

        segments.add(10, 30, 0);
        Assert.assertEquals("[]", segments.toString());

        segments.add(10, 30, 1);
        Assert.assertEquals("[[10,1],[30,0]]", segments.toString());

        segments.add(15, 25, 1);
        Assert.assertEquals("[[10,1],[15,2],[25,1],[30,0]]", segments.toString());

        segments.add(15, 25, -2);
        Assert.assertEquals("[[10,1],[15,0],[25,1],[30,0]]", segments.toString());

        segments.add(10, 15, -1);
        Assert.assertEquals("[[25,1],[30,0]]", segments.toString());

        segments.add(25,30, -1);
        Assert.assertEquals("[]", segments.toString());
    }

    @Test
    public void testSetCase1() {
        IntensitySegments segments = new IntensitySegments();
        Assert.assertEquals("[]", segments.toString());

        segments.set(10, 30, 1);
        Assert.assertEquals("[[10,1],[30,0]]", segments.toString());

        segments.set(20, 40, 2);
        Assert.assertEquals("[[10,1],[20,2],[40,0]]", segments.toString());

        segments.set(15, 25, 3);
        Assert.assertEquals("[[10,1],[15,3],[25,2],[40,0]]", segments.toString());

        segments.set(5, 20, 0);
        Assert.assertEquals("[[20,3],[25,2],[40,0]]", segments.toString());

        segments.set(10, 40, 0);
        Assert.assertEquals("[]", segments.toString());
    }

    @Test
    public void testSetCase2() {
        IntensitySegments segments = new IntensitySegments();
        Assert.assertEquals("[]", segments.toString());

        segments.set(10, 20, 1);
        Assert.assertEquals("[[10,1],[20,0]]", segments.toString());

        segments.set(30, 40, 2);
        Assert.assertEquals("[[10,1],[20,0],[30,2],[40,0]]", segments.toString());

        segments.set(5, 35, 0);
        Assert.assertEquals("[[35,2],[40,0]]", segments.toString());
    }
}