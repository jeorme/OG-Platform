/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.marketdata;

import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.financial.currency.CurrencyMatrix;
import com.opengamma.financial.currency.CurrencyMatrixValue;
import com.opengamma.financial.currency.CurrencyMatrixValueVisitor;
import com.opengamma.financial.currency.CurrencyPair;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.LocalDateRange;

/**
 * A market data requirement for a currency pair spot rate.
 */
public class CurrencyPairMarketDataRequirement implements MarketDataRequirement {

  /**
   * The currency pair to get the spot rate for.
   */
  private final CurrencyPair _currencyPair;

  /* package */ CurrencyPairMarketDataRequirement(CurrencyPair currencyPair) {
    _currencyPair = currencyPair;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return _currencyPair.equals(((CurrencyPairMarketDataRequirement) o)._currencyPair);
  }

  @Override
  public int hashCode() {
    return _currencyPair.hashCode();
  }

  /* package */ Double getSpotRate(CurrencyMatrix currencyMatrix, RawMarketDataSource dataSource) {
    return getRate(currencyMatrix, dataSource, _currencyPair.getBase(), _currencyPair.getCounter());
  }

  // TODO does this logic belong in this class? maybe not. move it if there turns out to be a better place
  private Double getRate(final CurrencyMatrix currencyMatrix,
                         final RawMarketDataSource dataSource,
                         final Currency base,
                         final Currency counter) {
    CurrencyMatrixValue value = currencyMatrix.getConversion(base, counter);
    if (value == null) {
      return null;
    }
    CurrencyMatrixValueVisitor<Double> visitor = new CurrencyMatrixValueVisitor<Double>() {
      @Override
      public Double visitFixed(CurrencyMatrixValue.CurrencyMatrixFixed fixedValue) {
        return fixedValue.getFixedValue();
      }

      @SuppressWarnings("unchecked")
      @Override
      public Double visitValueRequirement(CurrencyMatrixValue.CurrencyMatrixValueRequirement req) {
        ValueRequirement valueRequirement = req.getValueRequirement();
        ExternalIdBundle idBundle = valueRequirement.getTargetReference().getRequirement().getIdentifiers();
        String dataField = valueRequirement.getValueName();
        Double spotRate = dataSource.<Double>get(idBundle, dataField).getValue();
        if (spotRate == null) {
          return null;
        }
        if (req.isReciprocal()) {
          return 1 / spotRate;
        } else {
          return spotRate;
        }
      }

      @Override
      public Double visitCross(CurrencyMatrixValue.CurrencyMatrixCross cross) {
        Double baseCrossRate = getRate(currencyMatrix, dataSource, base, cross.getCrossCurrency());
        Double crossCounterRate = getRate(currencyMatrix, dataSource, cross.getCrossCurrency(), counter);
        if (baseCrossRate == null || crossCounterRate == null) {
          return null;
        } else {
          return baseCrossRate * crossCounterRate;
        }
      }
    };
    return value.accept(visitor);
  }

  /* package */ LocalDateDoubleTimeSeries getSpotRateSeries(LocalDateRange dateRange,
                                                            CurrencyMatrix currencyMatrix,
                                                            RawMarketDataSource rawDataSource) {
    return getSpotRateSeries(dateRange, currencyMatrix, rawDataSource, _currencyPair.getBase(), _currencyPair.getCounter());
  }

  private LocalDateDoubleTimeSeries getSpotRateSeries(LocalDateRange dateRange,
                                                      CurrencyMatrix currencyMatrix,
                                                      RawMarketDataSource rawDataSource,
                                                      Currency base,
                                                      Currency counter) {
    // TODO needs to look a lot like getRate. see CurrencyMatrixSeriesSourcingFunction.getRate
    throw new UnsupportedOperationException();
  }
}
