/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.bondcurves;

import static com.opengamma.engine.value.ValueRequirementNames.ACCRUED_INTEREST;

import com.opengamma.analytics.financial.provider.calculator.issuer.AccruedInterestFromCurvesCalculator;
import com.opengamma.engine.value.ValueRequirementNames;

/**
 * Calculates the accrued interest of a bond from yield curves.
 */
public class BondPV01Function extends BondFromCurvesFunction<Double> {

  /**
   * Sets the value requirement name to {@link ValueRequirementNames#ACCRUED_INTEREST} and
   * sets the calculator to {@link AccruedInterestFromCurvesCalculator}
   */
  public BondPV01Function() {
    super(ACCRUED_INTEREST, AccruedInterestFromCurvesCalculator.getInstance());
  }

}
