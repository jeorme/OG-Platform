/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.integration;

import static org.junit.Assert.assertArrayEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;

/**
 * 
 */
public class GaussianQuadratureDataTest {
  private static final double[] X = new double[] {1, 2, 3, 4};
  private static final double[] W = new double[] {6, 7, 8, 9};
  private static final GaussianQuadratureData F = new GaussianQuadratureData(X, W);

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullAbscissas() {
    new GaussianQuadratureData(null, W);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullWeights() {
    new GaussianQuadratureData(X, null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testWrongLength() {
    new GaussianQuadratureData(X, new double[] {1, 2, 3});
  }

  @Test
  public void test() {
    GaussianQuadratureData other = new GaussianQuadratureData(X, W);
    assertEquals(F, other);
    assertEquals(F.hashCode(), other.hashCode());
    other = new GaussianQuadratureData(W, W);
    assertFalse(F.equals(other));
    other = new GaussianQuadratureData(X, X);
    assertFalse(F.equals(other));
    assertArrayEquals(F.getAbscissas(), X, 0);
    assertArrayEquals(F.getWeights(), W, 0);
  }
}
