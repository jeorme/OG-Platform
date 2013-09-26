/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.swaption.method;

import java.util.HashMap;
import java.util.Map;

import com.opengamma.analytics.financial.instrument.index.GeneratorAttributeIR;
import com.opengamma.analytics.financial.instrument.index.GeneratorInstrument;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedCompoundedONCompounded;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.InterestRateCurveSensitivity;
import com.opengamma.analytics.financial.interestrate.PresentValueBlackSwaptionSensitivity;
import com.opengamma.analytics.financial.interestrate.PresentValueCalculator;
import com.opengamma.analytics.financial.interestrate.PresentValueCurveSensitivityCalculator;
import com.opengamma.analytics.financial.interestrate.YieldCurveBundle;
import com.opengamma.analytics.financial.interestrate.method.PricingMethod;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponFixedAccruedCompounding;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponONCompounded;
import com.opengamma.analytics.financial.interestrate.swap.derivative.Swap;
import com.opengamma.analytics.financial.interestrate.swap.method.SwapFixedCompoundingONCompoundingDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.swaption.derivative.SwaptionPhysicalFixedCompoundedONCompounded;
import com.opengamma.analytics.financial.interestrate.swaption.derivative.SwaptionPhysicalFixedIbor;
import com.opengamma.analytics.financial.model.option.definition.YieldCurveWithBlackSwaptionBundle;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.BlackFunctionData;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.BlackPriceFunction;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.analytics.math.function.Function1D;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.CurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;

/**
 *  Class used to compute the price and sensitivity of a physical delivery swaption with Black model.
 *  The implied Black volatilities are expiry and underlying maturity dependent.
 *  The swap underlying the swaption should be a Fixed for Ibor (without spread) swap.
 *  @deprecated Use {@link SwaptionPhysicalFixedCompoundedONCompoundedBlackMethod}
 */
@Deprecated
public final class SwaptionPhysicalFixedCompoundedONCompoundedBlackMethod implements PricingMethod {

  /**
   * The method unique instance.
   */
  private static final SwaptionPhysicalFixedCompoundedONCompoundedBlackMethod INSTANCE = new SwaptionPhysicalFixedCompoundedONCompoundedBlackMethod();

  /**
   * Return the unique instance of the class.
   * @return The instance.
   */
  public static SwaptionPhysicalFixedCompoundedONCompoundedBlackMethod getInstance() {
    return INSTANCE;
  }

  /**
   * Private constructor.
   */
  private SwaptionPhysicalFixedCompoundedONCompoundedBlackMethod() {
  }

  /**
   * The present value curve sensitivity calculator.
   */
  private static final PresentValueCurveSensitivityCalculator PV_SENSITIVITY_CALCULATOR = PresentValueCurveSensitivityCalculator.getInstance();
  /**
   * The present value calculator.
   */
  private static final PresentValueCalculator PVC = PresentValueCalculator.getInstance();
  /**
   * The swap method.
   */
  private static final SwapFixedCompoundingONCompoundingDiscountingMethod METHOD_SWAP = SwapFixedCompoundingONCompoundingDiscountingMethod.getInstance();

  /**
   * Computes the present value of a physical delivery European swaption in the Black model.
   * @param swaption The swaption.
   * @param curveBlack The curves with Black volatility data.
   * @return The present value.
   */
  public CurrencyAmount presentValue(final SwaptionPhysicalFixedCompoundedONCompounded swaption, final YieldCurveWithBlackSwaptionBundle curveBlack) {
    ArgumentChecker.notNull(swaption, "Swaption");
    ArgumentChecker.notNull(curveBlack, "Curves with Black volatility");
    final GeneratorInstrument<GeneratorAttributeIR> generatorSwap = curveBlack.getBlackParameters().getGeneratorSwap();
    final GeneratorSwapFixedCompoundedONCompounded fixedCompoundedON = (GeneratorSwapFixedCompoundedONCompounded) generatorSwap;
    final Calendar calendar = fixedCompoundedON.getOvernightCalendar();
    final DayCount fixedLegDayCount = fixedCompoundedON.getFixedLegDayCount();
    final double pvbpModified = METHOD_SWAP.presentValueBasisPoint(swaption.getUnderlyingSwap(), fixedLegDayCount,
        calendar, curveBlack);
    final Swap<CouponFixedAccruedCompounding, CouponONCompounded> swap = swaption.getUnderlyingSwap();
    final double pvbp = METHOD_SWAP.presentValueBasisPoint(swaption.getUnderlyingSwap(), fixedLegDayCount, calendar, curveBlack);
    final double pvSecond = -swap.getSecondLeg().accept(PVC, curveBlack) * Math.signum(swap.getSecondLeg().getNthPayment(0).getNotional());
    final double forwardModified = -pvSecond / pvbp;
    final double strikeModified = METHOD_SWAP.couponEquivalent(swaption.getUnderlyingSwap(), pvbpModified, curveBlack);
    final double maturity = swaption.getMaturityTime();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(strikeModified, swaption.getTimeToExpiry(), swaption.isCall());
    // Implementation note: option required to pass the strike (in case the swap has non-constant coupon).
    final BlackPriceFunction blackFunction = new BlackPriceFunction();
    final double volatility = curveBlack.getBlackParameters().getVolatility(swaption.getTimeToExpiry(), maturity);
    final BlackFunctionData dataBlack = new BlackFunctionData(forwardModified, pvbpModified, volatility);
    final Function1D<BlackFunctionData, Double> func = blackFunction.getPriceFunction(option);
    final double pv = func.evaluate(dataBlack) * (swaption.isLong() ? 1.0 : -1.0);
    return CurrencyAmount.of(swaption.getCurrency(), pv);
  }

  @Override
  public CurrencyAmount presentValue(final InstrumentDerivative instrument, final YieldCurveBundle curves) {
    ArgumentChecker.isTrue(instrument instanceof SwaptionPhysicalFixedIbor, "Physical delivery swaption");
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackSwaptionBundle, "Bundle should contain Black Swaption data");
    return presentValue(instrument, curves);
  }

  /**
   * Computes the implied Black volatility of the vanilla swaption.
   * @param swaption The swaption.
   * @param curves The yield curve bundle.
   * @return The implied volatility.
   */
  public double impliedVolatility(final SwaptionPhysicalFixedCompoundedONCompounded swaption, final YieldCurveBundle curves) {
    ArgumentChecker.notNull(curves, "Curves");
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackSwaptionBundle, "Yield curve bundle should contain Black swaption data");
    final YieldCurveWithBlackSwaptionBundle curvesBlack = (YieldCurveWithBlackSwaptionBundle) curves;
    ArgumentChecker.notNull(swaption, "Forex option");
    final double tenor = swaption.getMaturityTime();
    final double volatility = curvesBlack.getBlackParameters().getVolatility(swaption.getTimeToExpiry(), tenor);
    return volatility;
  }

  /**
   * Computes the present value rate sensitivity to rates of a physical delivery European swaption in the SABR model.
   * @param swaption The swaption.
   * @param curveBlack The curves with Black volatility data.
   * @return The present value curve sensitivity.
   */
  public InterestRateCurveSensitivity presentValueCurveSensitivity(final SwaptionPhysicalFixedCompoundedONCompounded swaption, final YieldCurveWithBlackSwaptionBundle curveBlack) {
    ArgumentChecker.notNull(swaption, "Swaption");
    ArgumentChecker.notNull(curveBlack, "Curves with Black volatility");
    final GeneratorInstrument<GeneratorAttributeIR> generatorSwap = curveBlack.getBlackParameters().getGeneratorSwap();
    final GeneratorSwapFixedCompoundedONCompounded fixedCompoundedON = (GeneratorSwapFixedCompoundedONCompounded) generatorSwap;
    final Calendar calendar = fixedCompoundedON.getOvernightCalendar();
    final DayCount dayCountModification = fixedCompoundedON.getFixedLegDayCount();
    final double pvbpModified = METHOD_SWAP.presentValueBasisPoint(swaption.getUnderlyingSwap(), dayCountModification, calendar, curveBlack);
    final Swap<CouponFixedAccruedCompounding, CouponONCompounded> swap = swaption.getUnderlyingSwap();
    final double pvbp = METHOD_SWAP.presentValueBasisPoint(swaption.getUnderlyingSwap(), dayCountModification, calendar, curveBlack);
    final double pvSecond = swap.getSecondLeg().accept(PVC, curveBlack) * Math.signum(swap.getSecondLeg().getNthPayment(0).getNotional());
    final double forwardModified = pvSecond / pvbp;
    final double strikeModified = METHOD_SWAP.couponEquivalent(swaption.getUnderlyingSwap(), pvbpModified, curveBlack);
    final double maturity = swaption.getMaturityTime();
    // Derivative of the forward and pvbp with respect to the rates.
    final InterestRateCurveSensitivity pvbpModifiedDr = METHOD_SWAP.presentValueBasisPointCurveSensitivity(swaption.getUnderlyingSwap(), dayCountModification,
        calendar, curveBlack);
    final InterestRateCurveSensitivity pvbpDr = METHOD_SWAP.presentValueBasisPointCurveSensitivity(swap, dayCountModification, curveBlack);
    final InterestRateCurveSensitivity pvSecondDr = new InterestRateCurveSensitivity(swap.getSecondLeg().accept(PV_SENSITIVITY_CALCULATOR, curveBlack)).multipliedBy(Math
        .signum(swap.getSecondLeg().getNthPayment(0).getNotional()));
    final InterestRateCurveSensitivity forwardModifiedDr = pvSecondDr.multipliedBy(1.0 / pvbp).plus(pvbpDr.multipliedBy(-pvSecond / (pvbp * pvbp)));
    // Implementation note: strictly speaking, the strike equivalent is curve dependent; that dependency is ignored.
    final EuropeanVanillaOption option = new EuropeanVanillaOption(strikeModified, swaption.getTimeToExpiry(), swaption.isCall());
    // Implementation note: option required to pass the strike (in case the swap has non-constant coupon).
    final BlackPriceFunction blackFunction = new BlackPriceFunction();
    final double volatility = curveBlack.getBlackParameters().getVolatility(swaption.getTimeToExpiry(), maturity);
    final BlackFunctionData dataBlack = new BlackFunctionData(forwardModified, 1.0, volatility);
    final double[] bsAdjoint = blackFunction.getPriceAdjoint(option, dataBlack);
    InterestRateCurveSensitivity result = pvbpModifiedDr.multipliedBy(bsAdjoint[0]);
    result = result.plus(forwardModifiedDr.multipliedBy(pvbpModified * bsAdjoint[1]));
    if (!swaption.isLong()) {
      result = result.multipliedBy(-1);
    }
    return result;
  }

  /**
   * Computes the present value sensitivity to the Black volatility (also called vega) of a physical delivery European swaption in the Black swaption model.
   * @param swaption The swaption.
   * @param curveBlack The curves with Black volatility data.
   * @return The present value Black sensitivity.
   */
  public PresentValueBlackSwaptionSensitivity presentValueBlackSensitivity(final SwaptionPhysicalFixedCompoundedONCompounded swaption, final YieldCurveWithBlackSwaptionBundle curveBlack) {
    ArgumentChecker.notNull(swaption, "Swaption");
    ArgumentChecker.notNull(curveBlack, "Curves with Black volatility");
    final GeneratorInstrument<GeneratorAttributeIR> generatorSwap = curveBlack.getBlackParameters().getGeneratorSwap();
    final GeneratorSwapFixedCompoundedONCompounded fixedCompoundedON = (GeneratorSwapFixedCompoundedONCompounded) generatorSwap;
    final Calendar calendar = fixedCompoundedON.getOvernightCalendar();
    final DayCount dayCountModification = fixedCompoundedON.getFixedLegDayCount();
    final double pvbpModified = METHOD_SWAP.presentValueBasisPoint(swaption.getUnderlyingSwap(), dayCountModification, calendar,
        curveBlack);
    final Swap<CouponFixedAccruedCompounding, CouponONCompounded> swap = swaption.getUnderlyingSwap();
    final double pvbp = METHOD_SWAP.presentValueBasisPoint(swaption.getUnderlyingSwap(), dayCountModification, calendar, curveBlack);
    final double pvSecond = -swap.getSecondLeg().accept(PVC, curveBlack) * Math.signum(swap.getSecondLeg().getNthPayment(0).getNotional());
    final double forwardModified = -pvSecond / pvbp;
    final double strikeModified = METHOD_SWAP.couponEquivalent(swaption.getUnderlyingSwap(), pvbpModified, curveBlack);
    final double maturity = swaption.getMaturityTime();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(strikeModified, swaption.getTimeToExpiry(), swaption.isCall());
    // Implementation note: option required to pass the strike (in case the swap has non-constant coupon).
    final DoublesPair point = new DoublesPair(swaption.getTimeToExpiry(), maturity);
    final BlackPriceFunction blackFunction = new BlackPriceFunction();
    final double volatility = curveBlack.getBlackParameters().getVolatility(point);
    final BlackFunctionData dataBlack = new BlackFunctionData(forwardModified, 1.0, volatility);
    final double[] bsAdjoint = blackFunction.getPriceAdjoint(option, dataBlack);
    final Map<DoublesPair, Double> sensitivity = new HashMap<>();
    sensitivity.put(point, bsAdjoint[2] * pvbpModified * (swaption.isLong() ? 1.0 : -1.0));
    return new PresentValueBlackSwaptionSensitivity(sensitivity, curveBlack.getBlackParameters().getGeneratorSwap());
  }

}