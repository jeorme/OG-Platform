/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.irs;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.threeten.bp.ZonedDateTime;

import com.google.common.collect.ImmutableMap;
import com.opengamma.analytics.financial.instrument.InstrumentDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapDefinition;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitor;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorAdapter;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.interestrate.swap.derivative.Swap;
import com.opengamma.analytics.financial.provider.calculator.discounting.CrossGammaMultiCurveCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.CrossGammaSingleCurveCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.PV01CurveParametersCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.ParRateDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.ParSpreadMarketQuoteDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.PresentValueCurveSensitivityDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.PresentValueDiscountingCalculator;
import com.opengamma.analytics.financial.provider.calculator.discounting.PresentValueDiscountingMultipleInstrumentsCalculator;
import com.opengamma.analytics.financial.provider.calculator.generic.MarketQuoteSensitivityBlockCalculator;
import com.opengamma.analytics.financial.provider.curve.CurveBuildingBlockBundle;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderDiscount;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderInterface;
import com.opengamma.analytics.financial.provider.description.interestrate.ParameterProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyMulticurveSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyParameterSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.parameter.ParameterSensitivityParameterCalculator;
import com.opengamma.analytics.math.matrix.CommonsMatrixAlgebra;
import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.analytics.util.amount.ReferenceAmount;
import com.opengamma.financial.analytics.DoubleLabelledMatrix1D;
import com.opengamma.financial.analytics.DoubleLabelledMatrix2D;
import com.opengamma.financial.analytics.conversion.FixedIncomeConverterDataProvider;
import com.opengamma.financial.analytics.conversion.InterestRateSwapSecurityConverter;
import com.opengamma.financial.analytics.conversion.TradeFeeConverter;
import com.opengamma.financial.analytics.model.fixedincome.BucketedCrossSensitivities;
import com.opengamma.financial.analytics.model.fixedincome.BucketedCurveSensitivities;
import com.opengamma.financial.analytics.model.fixedincome.CashFlowDetailsCalculator;
import com.opengamma.financial.analytics.model.fixedincome.CashFlowDetailsProvider;
import com.opengamma.financial.analytics.model.fixedincome.SwapLegCashFlows;
import com.opengamma.financial.analytics.timeseries.HistoricalTimeSeriesBundle;
import com.opengamma.financial.security.irs.InterestRateSwapSecurity;
import com.opengamma.financial.security.irs.PayReceiveType;
import com.opengamma.sesame.CurveMatrixLabeller;
import com.opengamma.sesame.trade.ImmutableTrade;
import com.opengamma.sesame.trade.InterestRateSwapTrade;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.result.Result;
import com.opengamma.util.tuple.Pair;
import com.opengamma.util.tuple.Pairs;

/**
 * Calculator for Discounting swaps.
 */
public class DiscountingInterestRateSwapCalculator implements InterestRateSwapCalculator {

  /**
   * Calculator for cash flows.
   */
  private static final CashFlowDetailsCalculator CFDC = new CashFlowDetailsCalculator();

  /**
   * Converter for fees defined in the Trade.
   */
  private static final TradeFeeConverter TFC = TradeFeeConverter.getInstance();

  /**
   * Calculator for present value.
   */
  private static final PresentValueDiscountingCalculator PVDC = PresentValueDiscountingCalculator.getInstance();

  /**
   * Calculator for present value for fees.
   */
  private static final PresentValueDiscountingMultipleInstrumentsCalculator PVDMIC =
      PresentValueDiscountingMultipleInstrumentsCalculator.getInstance();

  /**
   * Calculator for par rate.
   */
  private static final ParRateDiscountingCalculator PRDC = ParRateDiscountingCalculator.getInstance();

  /**
   * Calculator for PV01
   */
  private static final PV01CurveParametersCalculator<ParameterProviderInterface> PV01C =
      new PV01CurveParametersCalculator<>(PresentValueCurveSensitivityDiscountingCalculator.getInstance());

  /** The curve sensitivity calculator */
  private static final InstrumentDerivativeVisitor<ParameterProviderInterface, MultipleCurrencyMulticurveSensitivity> PVCSDC =
      PresentValueCurveSensitivityDiscountingCalculator.getInstance();
  /** The parameter sensitivity calculator */
  private static final ParameterSensitivityParameterCalculator<ParameterProviderInterface> PSC =
      new ParameterSensitivityParameterCalculator<>(PVCSDC);
  /** The market quote sensitivity calculator */
  private static final MarketQuoteSensitivityBlockCalculator<ParameterProviderInterface> BUCKETED_PV01_CALCULATOR =
      new MarketQuoteSensitivityBlockCalculator<>(PSC);
  /** The par spread market quote calculator */
  private static final ParSpreadMarketQuoteDiscountingCalculator PSMQC =
      ParSpreadMarketQuoteDiscountingCalculator.getInstance();
  /** The calculator which will compute intra-curve gammas */
  private static final CrossGammaMultiCurveCalculator CGC = new CrossGammaMultiCurveCalculator(PVCSDC);
  /** The calculator which will compute the sum of columns gammas */
  private static final CrossGammaSingleCurveCalculator SUM_OF_COLUMNS_GAMMA = new CrossGammaSingleCurveCalculator(PVCSDC);
  /** Matrix algebra tooling to permit matrix manipulation */
  private static final CommonsMatrixAlgebra MA = new CommonsMatrixAlgebra();

  /**
   * Provides scaling to/from basis points.
   */
  private static final double BASIS_POINT_FACTOR = 1.0E-4;

  /**
   * Derivative form of the security.
   */
  private final InstrumentDerivative _derivative;

  /**
   * The multicurve bundle.
   */
  private final MulticurveProviderInterface _bundle;

  /**
   * The curve building block bundle.
   */
  private final CurveBuildingBlockBundle _curveBuildingBlockBundle;

  /**
   * The swap definition.
   */
  private final SwapDefinition _definition;

  /**
   * The swap security.
   */
  private final InterestRateSwapSecurity _security;

  /**
   * The swap trade. Can be null, as it is possible to use the InterestRateSwapSecurity
   */

  private final ImmutableTrade _trade;

  /**
   * The ZonedDateTime valuation time.
   */
  private final ZonedDateTime _valuationTime;

  /**
   * The curve labellers.
   */
  private final Map<String, CurveMatrixLabeller> _curveLabellers;

  /**
   * Creates a calculator for a InterestRateSwapSecurity.
   *  @param security the swap to calculate values for
   * @param bundle the multicurve bundle, including the curves
   * @param curveBuildingBlockBundle the curve block building bundle
   * @param swapConverter the InterestRateSwapSecurityConverter
   * @param valuationTime the ZonedDateTime
   * @param definitionConverter the FixedIncomeConverterDataProvider
   * @param fixings the HistoricalTimeSeriesBundle, a collection of historical time-series objects
   * @param curveLabellers the curve labellers
   */
  public DiscountingInterestRateSwapCalculator(InterestRateSwapSecurity security,
                                               MulticurveProviderInterface bundle,
                                               CurveBuildingBlockBundle curveBuildingBlockBundle,
                                               InterestRateSwapSecurityConverter swapConverter,
                                               ZonedDateTime valuationTime,
                                               FixedIncomeConverterDataProvider definitionConverter,
                                               HistoricalTimeSeriesBundle fixings,
                                               Map<String, CurveMatrixLabeller> curveLabellers) {
    _security = ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(swapConverter, "swapConverter");
    _valuationTime = ArgumentChecker.notNull(valuationTime, "valuationTime");
    ArgumentChecker.notNull(definitionConverter, "definitionConverter");
    ArgumentChecker.notNull(fixings, "fixings");
    _trade = null;
    _definition = (SwapDefinition) security.accept(swapConverter);
    _derivative = createInstrumentDerivative(security, valuationTime, definitionConverter, fixings);
    _bundle = ArgumentChecker.notNull(bundle, "bundle");
    _curveBuildingBlockBundle = ArgumentChecker.notNull(curveBuildingBlockBundle, "curveBuildingBlockBundle");
    _curveLabellers = ArgumentChecker.notNull(curveLabellers, "curveLabellers");
    ArgumentChecker.isTrue(curveLabellers.size() == curveBuildingBlockBundle.getData().size(),
                           "Require same number of curve labellers & definitions");
    for (String curveName : curveBuildingBlockBundle.getData().keySet()) {
      ArgumentChecker.isTrue(curveLabellers.containsKey(curveName), "curve labellers not present {}", curveName);
    }

  }

  /**
   * Creates a calculator for a InterestRateSwapSecurity.
   *
   * @param interestRateSwapTrade the swap to calculate values for
   * @param bundle the multicurve bundle, including the curves
   * @param curveBuildingBlockBundle the curve block building bundle
   * @param valuationTime the ZonedDateTime
   * @param curveLabellers the curve labellers
   * @param definition the definition built from the swap
   * @param derivative the derivative built from the swap
   */
  public DiscountingInterestRateSwapCalculator(InterestRateSwapTrade interestRateSwapTrade,
                                               MulticurveProviderInterface bundle,
                                               CurveBuildingBlockBundle curveBuildingBlockBundle,
                                               ZonedDateTime valuationTime,
                                               Map<String, CurveMatrixLabeller> curveLabellers,
                                               SwapDefinition definition,
                                               InstrumentDerivative derivative) {
    ArgumentChecker.notNull(interestRateSwapTrade, "interestRateSwapTrade");
    _valuationTime = ArgumentChecker.notNull(valuationTime, "valuationTime");
    _trade = interestRateSwapTrade.getTrade();
    _security = interestRateSwapTrade.getSecurity();
    _definition = ArgumentChecker.notNull(definition, "definition");
    _derivative = ArgumentChecker.notNull(derivative, "derivative");
    _bundle = ArgumentChecker.notNull(bundle, "bundle");
    _curveBuildingBlockBundle = ArgumentChecker.notNull(curveBuildingBlockBundle, "curveBuildingBlockBundle");
    _curveLabellers = ArgumentChecker.notNull(curveLabellers, "curveLabellers");

    ArgumentChecker.isTrue(curveLabellers.size() == curveBuildingBlockBundle.getData().size(),
                           "Require same number of curve labellers & definitions");
    for (String curveName : curveBuildingBlockBundle.getData().keySet()) {
      ArgumentChecker.isTrue(curveLabellers.containsKey(curveName), "curve labellers not present {}", curveName);
    }
  }

  @Override
  public Result<MultipleCurrencyAmount> calculatePV() {

    if (_trade != null) {
      InstrumentDefinition<?> feeDefinition = TFC.convert(_trade);
      if (feeDefinition != null) {
        Pair<InstrumentDerivative[], MulticurveProviderInterface> data =
            Pairs.of(new InstrumentDerivative[] {feeDefinition.toDerivative(_valuationTime) }, _bundle);
        return Result.success(_derivative.accept(PVDMIC, data));
      }
    }

    return Result.success(calculateResult(PVDC));

  }

  @Override
  public Result<MultipleCurrencyAmount> calculatePv(MulticurveProviderInterface bundle) {
    ArgumentChecker.notNull(bundle, "curve bundle");
    return Result.success(_derivative.accept(PVDC, bundle));
  }

  @Override
  public Result<Double> calculateRate() {
    return Result.success(calculateResult(PRDC));
  }

  @Override
  public Result<Double> calculateParSpread() {
    return Result.success(calculateResult(PSMQC));
  }

  @Override
  public Result<ReferenceAmount<Pair<String, Currency>>> calculatePV01() {
    return Result.success(calculateResult(PV01C));
  }

  @Override
  public Result<BucketedCurveSensitivities> calculateBucketedPV01() {
    MultipleCurrencyParameterSensitivity sensitivity = BUCKETED_PV01_CALCULATOR
        .fromInstrument(_derivative, _bundle, _curveBuildingBlockBundle)
        .multipliedBy(BASIS_POINT_FACTOR);
    Map<Pair<String, Currency>, DoubleLabelledMatrix1D> labelledMatrix1DMap = new HashMap<>();
    for (Map.Entry<Pair<String, Currency>, DoubleMatrix1D> entry : sensitivity.getSensitivities().entrySet()) {
      CurveMatrixLabeller curveMatrixLabeller = _curveLabellers.get(entry.getKey().getFirst());
      DoubleLabelledMatrix1D matrix = curveMatrixLabeller.labelMatrix(entry.getValue());
      labelledMatrix1DMap.put(entry.getKey(), matrix);
    }
    return Result.success(BucketedCurveSensitivities.of(labelledMatrix1DMap));
  }
  
  @Override
  public Result<BucketedCrossSensitivities> calculateBucketedCrossGamma() {
    HashMap<String, DoubleMatrix2D> crossGammas = CGC.calculateCrossGammaIntraCurve(_derivative, (MulticurveProviderDiscount) _bundle);
    Map<String, DoubleLabelledMatrix2D> labelledMatrix2DMap = new HashMap<>();
    
    for (Map.Entry<String, DoubleMatrix2D> entry : crossGammas.entrySet()) {
      //Values returned from analytics need to be scaled appropriately: multiplied by 1 bp ^ 1bp
      DoubleMatrix2D scaledValues = (DoubleMatrix2D) MA.scale(entry.getValue(), BASIS_POINT_FACTOR * BASIS_POINT_FACTOR);
      CurveMatrixLabeller curveMatrixLabeller = _curveLabellers.get(entry.getKey());
      DoubleLabelledMatrix2D matrix = curveMatrixLabeller.labelMatrix(scaledValues);
      labelledMatrix2DMap.put(entry.getKey(), matrix);
    }
    return Result.success(BucketedCrossSensitivities.of(labelledMatrix2DMap));
  }

  @Override
  public Result<BucketedCurveSensitivities> calculateBucketedGamma() {
    MulticurveProviderDiscount singleCurveBundle = (MulticurveProviderDiscount) _bundle;
    Set<String> curveNames = singleCurveBundle.getAllCurveNames();
   
    if (curveNames.size() != 1) {
      return Result.failure(
          new InvalidParameterException("Only bucketed gamma on single-curve multicurve is currently supported"));
    }
    
    Set<Currency> currencies = singleCurveBundle.getCurrencies();
    double[] rawGamma = SUM_OF_COLUMNS_GAMMA.calculateSumOfColumnsGamma(_derivative, singleCurveBundle);
    
    for (int i = 0; i < rawGamma.length; ++i) {
      rawGamma[i] *= BASIS_POINT_FACTOR * BASIS_POINT_FACTOR;
    }
    
    DoubleMatrix1D gamma = new DoubleMatrix1D(rawGamma);
    Pair<String, Currency> sensitivityKey = Pairs.of(curveNames.iterator().next(), currencies.iterator().next());
    CurveMatrixLabeller curveMatrixLabeller = _curveLabellers.get(sensitivityKey.getFirst());
    DoubleLabelledMatrix1D matrix = curveMatrixLabeller.labelMatrix(gamma);
    Map<Pair<String, Currency>, DoubleLabelledMatrix1D> labelledMatrix1DMap = ImmutableMap.of(sensitivityKey, matrix);

    return Result.success(BucketedCurveSensitivities.of(labelledMatrix1DMap));
  }

  @Override
  public Result<SwapLegCashFlows> calculatePayLegCashFlows() {
    return Result.success(_derivative.accept(CFDC, createCashFlowDetailsProvider(PayReceiveType.PAY, false)));
  }

  @Override
  public Result<SwapLegCashFlows> calculateReceiveLegCashFlows() {
    return Result.success(_derivative.accept(CFDC, createCashFlowDetailsProvider(PayReceiveType.RECEIVE, false)));
  }

  @Override
  public Result<SwapLegCashFlows> calculateFullPayLegCashFlows() {
    return Result.success(_derivative.accept(CFDC, createCashFlowDetailsProvider(PayReceiveType.PAY, true)));
  }

  @Override
  public Result<SwapLegCashFlows> calculateFullReceiveLegCashFlows() {
    return Result.success(_derivative.accept(CFDC, createCashFlowDetailsProvider(PayReceiveType.RECEIVE, true)));
  }

  @Override
  public Result<MultipleCurrencyAmount> calculatePayLegPv() {
    /* We are not interested in fees here */
    Swap<? extends Payment, ? extends Payment> swap = (Swap<? extends Payment, ? extends Payment>) _derivative;
    InstrumentDerivative payLeg = isFirstLegPay() ?
        swap.getFirstLeg() :
        swap.getSecondLeg();
    return Result.success(payLeg.accept(PVDC, _bundle));
  }

  @Override
  public Result<MultipleCurrencyAmount> calculateReceiveLegPv() {
    /* We are not interested in fees here */
    Swap<? extends Payment, ? extends Payment> swap = (Swap<? extends Payment, ? extends Payment>) _derivative;
    InstrumentDerivative receiveLeg = isFirstLegPay() ?
        swap.getSecondLeg() :
        swap.getFirstLeg();
    return Result.success(receiveLeg.accept(PVDC, _bundle));
  }

  private <T> T calculateResult(InstrumentDerivativeVisitorAdapter<ParameterProviderInterface, T> calculator) {
    return _derivative.accept(calculator, _bundle);
  }

  private ReferenceAmount<Pair<String, Currency>> calculateResult(InstrumentDerivativeVisitor<ParameterProviderInterface, ReferenceAmount<Pair<String, Currency>>> calculator) {
    return _derivative.accept(calculator, _bundle);
  }

  private InstrumentDerivative createInstrumentDerivative(InterestRateSwapSecurity security,
                                                          ZonedDateTime valuationTime,
                                                          FixedIncomeConverterDataProvider definitionConverter,
                                                          HistoricalTimeSeriesBundle fixings) {
    return definitionConverter.convert(security, _definition, valuationTime, fixings);
  }

  private CashFlowDetailsProvider createCashFlowDetailsProvider(PayReceiveType type, boolean full) {
    return new CashFlowDetailsProvider(_bundle, _valuationTime, _definition, _security, type, full);
  }

  private boolean isFirstLegPay() {
    // There is no information on the definition or derivative to tell us if the leg is pay or
    // receive other than the sign of the notionals, but the first payment may be an exchange of notional,
    // so if there are more than one payments we should take the second payment.
    int paymentToCheck = _definition.getFirstLeg().getNumberOfPayments() > 1 ? 1 : 0;
    return _definition.getFirstLeg().getNthPayment(paymentToCheck).getReferenceAmount() < 0;
  }

}
