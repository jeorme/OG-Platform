/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Iterator;
import java.util.Set;

import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.greeks.GreekResultCollection;
import com.opengamma.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.financial.model.option.definition.AmericanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.BinomialOptionModelDefinition;
import com.opengamma.financial.model.option.definition.BoyleTrinomialOptionModelDefinition;
import com.opengamma.financial.model.option.definition.CoxRossRubinsteinBinomialOptionModelDefinition;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.LeisenReimerBinomialOptionModelDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.RendlemanBartterBinomialOptionModelDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.option.definition.TrinomialOptionModelDefinition;
import com.opengamma.financial.model.option.definition.TrisgeorgisBinomialOptionModelDefinition;
import com.opengamma.financial.model.option.pricing.analytic.AnalyticOptionModel;
import com.opengamma.financial.model.option.pricing.analytic.BjerksundStenslandModel;
import com.opengamma.financial.model.option.pricing.analytic.BlackScholesMertonModel;
import com.opengamma.financial.model.option.pricing.tree.BinomialOptionModel;
import com.opengamma.financial.model.option.pricing.tree.TreeOptionModel;
import com.opengamma.financial.model.option.pricing.tree.TrinomialOptionModel;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.math.surface.ConstantDoublesSurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

public class VanillaOptionCrossModelPricingTest {
  private static final double SPOT = 10;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2009, 1, 1);
  private static final Expiry EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5));
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(new YieldCurve(ConstantDoublesCurve.from(0.08)), 0.0, new VolatilitySurface(ConstantDoublesSurface.from(0.2)),
      SPOT, DATE);
  private static final BinomialOptionModelDefinition<OptionDefinition, StandardOptionDataBundle> CRR = new CoxRossRubinsteinBinomialOptionModelDefinition();
  private static final BinomialOptionModelDefinition<OptionDefinition, StandardOptionDataBundle> LR = new LeisenReimerBinomialOptionModelDefinition();
  private static final BinomialOptionModelDefinition<OptionDefinition, StandardOptionDataBundle> RB = new RendlemanBartterBinomialOptionModelDefinition();
  private static final BinomialOptionModelDefinition<OptionDefinition, StandardOptionDataBundle> TRISGEORGIS = new TrisgeorgisBinomialOptionModelDefinition();
  private static final TrinomialOptionModelDefinition<OptionDefinition, StandardOptionDataBundle> BOYLE = new BoyleTrinomialOptionModelDefinition();
  private static final Set<Greek> REQUIRED_GREEKS = Sets.newHashSet(Greek.FAIR_PRICE, Greek.DELTA, Greek.GAMMA);
  private static final double EPS = 2e-2;

  @Test
  public void testEuropeanOption() {
    final int n = 1001;
    final OptionDefinition call1 = new EuropeanVanillaOptionDefinition(SPOT * .9, EXPIRY, true);
    final OptionDefinition put1 = new EuropeanVanillaOptionDefinition(SPOT * .9, EXPIRY, false);
    final OptionDefinition call2 = new EuropeanVanillaOptionDefinition(SPOT * 1.1, EXPIRY, true);
    final OptionDefinition put2 = new EuropeanVanillaOptionDefinition(SPOT * 1.1, EXPIRY, false);
    final AnalyticOptionModel<OptionDefinition, StandardOptionDataBundle> bsm = new BlackScholesMertonModel();
    TreeOptionModel<OptionDefinition, StandardOptionDataBundle> treeModel = new BinomialOptionModel<StandardOptionDataBundle>(CRR, n, 5);
    assertGreeks(call1, treeModel, bsm);
    assertGreeks(put1, treeModel, bsm);
    assertGreeks(call2, treeModel, bsm);
    assertGreeks(put2, treeModel, bsm);
    treeModel = new BinomialOptionModel<StandardOptionDataBundle>(LR, n, 5);
    assertGreeks(call1, treeModel, bsm);
    assertGreeks(put1, treeModel, bsm);
    assertGreeks(call2, treeModel, bsm);
    assertGreeks(put2, treeModel, bsm);
    treeModel = new BinomialOptionModel<StandardOptionDataBundle>(RB, n, 5);
    assertGreeks(call1, treeModel, bsm);
    assertGreeks(put1, treeModel, bsm);
    assertGreeks(call2, treeModel, bsm);
    assertGreeks(put2, treeModel, bsm);
    treeModel = new BinomialOptionModel<StandardOptionDataBundle>(TRISGEORGIS, n, 5);
    assertGreeks(call1, treeModel, bsm);
    assertGreeks(put1, treeModel, bsm);
    assertGreeks(call2, treeModel, bsm);
    assertGreeks(put2, treeModel, bsm);
    treeModel = new TrinomialOptionModel<StandardOptionDataBundle>(BOYLE, n, 5);
    assertGreeks(call1, treeModel, bsm);
    assertGreeks(put1, treeModel, bsm);
    assertGreeks(call2, treeModel, bsm);
    assertGreeks(put2, treeModel, bsm);
  }

  @Test
  public void testAmericanOption() {
    final int n = 1001;
    final AmericanVanillaOptionDefinition call1 = new AmericanVanillaOptionDefinition(SPOT * .9, EXPIRY, true);
    final AmericanVanillaOptionDefinition put1 = new AmericanVanillaOptionDefinition(SPOT * .9, EXPIRY, false);
    final AmericanVanillaOptionDefinition call2 = new AmericanVanillaOptionDefinition(SPOT * 1.1, EXPIRY, true);
    final AmericanVanillaOptionDefinition put2 = new AmericanVanillaOptionDefinition(SPOT * 1.1, EXPIRY, false);
    final AnalyticOptionModel<AmericanVanillaOptionDefinition, StandardOptionDataBundle> bs = new BjerksundStenslandModel();
    TreeOptionModel<OptionDefinition, StandardOptionDataBundle> treeModel = new BinomialOptionModel<StandardOptionDataBundle>(CRR, n, 5);
    assertGreeks(call1, treeModel, bs);
    assertGreeks(put1, treeModel, bs);
    assertGreeks(call2, treeModel, bs);
    assertGreeks(put2, treeModel, bs);
    treeModel = new BinomialOptionModel<StandardOptionDataBundle>(LR, n, 5);
    assertGreeks(call1, treeModel, bs);
    assertGreeks(put1, treeModel, bs);
    assertGreeks(call2, treeModel, bs);
    assertGreeks(put2, treeModel, bs);
    treeModel = new BinomialOptionModel<StandardOptionDataBundle>(RB, n, 5);
    assertGreeks(call1, treeModel, bs);
    assertGreeks(put1, treeModel, bs);
    assertGreeks(call2, treeModel, bs);
    assertGreeks(put2, treeModel, bs);
    treeModel = new BinomialOptionModel<StandardOptionDataBundle>(TRISGEORGIS, n, 5);
    assertGreeks(call1, treeModel, bs);
    assertGreeks(put1, treeModel, bs);
    assertGreeks(call2, treeModel, bs);
    assertGreeks(put2, treeModel, bs);
    treeModel = new TrinomialOptionModel<StandardOptionDataBundle>(BOYLE, n, 5);
    assertGreeks(call1, treeModel, bs);
    assertGreeks(put1, treeModel, bs);
    assertGreeks(call2, treeModel, bs);
    // testGreeks(put2, treeModel, bs);
  }

  @SuppressWarnings("unchecked")
  private <T extends OptionDefinition, U extends T> void assertGreeks(final T definition, final OptionModel<T, StandardOptionDataBundle> first, final OptionModel<U, StandardOptionDataBundle> second) {
    final GreekResultCollection firstResult = first.getGreeks(definition, DATA, REQUIRED_GREEKS);
    final GreekResultCollection secondResult = second.getGreeks((U) definition, DATA, REQUIRED_GREEKS);
    if (first instanceof TrinomialOptionModel) {
      assertEquals(firstResult.get(Greek.FAIR_PRICE), secondResult.get(Greek.FAIR_PRICE), EPS);
      return;
    }
    assertEquals(firstResult.size(), secondResult.size());
    final Iterator<Greek> iter = firstResult.keySet().iterator();
    while (iter.hasNext()) {
      final Greek greek = iter.next();
      final Double result = firstResult.get(greek);
      assertTrue(secondResult.contains(greek));
      assertEquals(result, secondResult.get(greek), EPS);
    }
  }
}
