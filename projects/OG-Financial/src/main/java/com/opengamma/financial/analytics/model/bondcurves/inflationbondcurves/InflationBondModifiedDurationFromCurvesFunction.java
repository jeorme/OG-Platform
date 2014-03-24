/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.bondcurves.inflationbondcurves;

import static com.opengamma.engine.value.ValueRequirementNames.MODIFIED_DURATION;

import com.opengamma.analytics.financial.provider.calculator.inflation.ModifiedDurationFromCurvesCalculator;
import com.opengamma.analytics.financial.provider.calculator.inflation.YieldFromCurvesCalculator;
import com.opengamma.analytics.financial.provider.description.inflation.InflationProviderInterface;
import com.opengamma.engine.value.ValueRequirementNames;

/**
 * 
 */
public class InflationBondModifiedDurationFromCurvesFunction extends InflationBondFromCurvesFunction<InflationProviderInterface, Double> {

  /**
   * Sets the value requirement name to {@link ValueRequirementNames#MODIFIED_DURATION} and
   * the calculator to {@link YieldFromCurvesCalculator}.
   */
  public InflationBondModifiedDurationFromCurvesFunction() {
    super(MODIFIED_DURATION, ModifiedDurationFromCurvesCalculator.getInstance());
  }
}
