/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame;

import static com.opengamma.sesame.marketdata.MarketDataStatus.AVAILABLE;
import static com.opengamma.sesame.marketdata.MarketDataStatus.PENDING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.opengamma.sesame.marketdata.MarketDataItem;
import com.opengamma.sesame.marketdata.MarketDataProvider;
import com.opengamma.sesame.marketdata.MarketDataRequirement;
import com.opengamma.sesame.marketdata.MarketDataSingleResult;
import com.opengamma.sesame.marketdata.SingleMarketDataValue;
import com.opengamma.util.test.TestGroup;


@Test(groups = TestGroup.UNIT)
public class MarketDataProviderTest {

  private ResettableMarketDataProviderFunction _resettableMarketDataProviderFunction;
  private MarketDataRequirement _mdReqmt1 = mock(MarketDataRequirement.class);
  private MarketDataRequirement _mdReqmt2 = mock(MarketDataRequirement.class);

  @BeforeMethod
  public void setUp() {
    _resettableMarketDataProviderFunction = new MarketDataProvider();
  }

  @Test
  public void emptyProviderReturnsPendingResultAndRequestIsRecorded() {

    MarketDataSingleResult result = _resettableMarketDataProviderFunction.requestData(_mdReqmt1);
    assertThat(result.getStatus(_mdReqmt1), is(PENDING));
    assertThat(_resettableMarketDataProviderFunction.getCollectedRequests(), contains(_mdReqmt1));
  }

  @Test
  public void alreadyPendingDataReturnsPendingResultButNoRequest() {

    Map<MarketDataRequirement, MarketDataItem<?>> data =
        ImmutableMap.<MarketDataRequirement, MarketDataItem<?>>of(_mdReqmt1, MarketDataItem.PENDING);
    _resettableMarketDataProviderFunction.resetMarketData(data);

    MarketDataSingleResult result = _resettableMarketDataProviderFunction.requestData(_mdReqmt1);
    assertThat(result.getStatus(_mdReqmt1), is(PENDING));
    assertThat(_resettableMarketDataProviderFunction.getCollectedRequests(), is(empty()));
  }

  @Test
  public void availableDataReturnsResultButNoRequest() {

    MarketDataItem<SingleMarketDataValue> item = MarketDataItem.available(new SingleMarketDataValue(123.45));
    Map<MarketDataRequirement, MarketDataItem<?>> data =
        ImmutableMap.<MarketDataRequirement, MarketDataItem<?>>of(_mdReqmt1, item);
    _resettableMarketDataProviderFunction.resetMarketData(data);

    MarketDataSingleResult result = _resettableMarketDataProviderFunction.requestData(_mdReqmt1);
    assertThat(result.getStatus(_mdReqmt1), is(AVAILABLE));
    assertThat(result.getValue(_mdReqmt1).getValue(), is((Object) 123.45));
    assertThat(_resettableMarketDataProviderFunction.getCollectedRequests(), is(empty()));
  }

  @Test
  public void resettingRemovesRequestsAndAvailableData() {

    MarketDataItem<SingleMarketDataValue> item = MarketDataItem.available(new SingleMarketDataValue(123.45));
    ImmutableMap<MarketDataRequirement, MarketDataItem<?>> data =
        ImmutableMap.<MarketDataRequirement, MarketDataItem<?>>of(_mdReqmt1, item);
    _resettableMarketDataProviderFunction.resetMarketData(data);

    MarketDataSingleResult result1 = _resettableMarketDataProviderFunction.requestData(ImmutableSet.of(_mdReqmt1, _mdReqmt2));
    assertThat(result1.getStatus(_mdReqmt1), is(AVAILABLE));
    assertThat(result1.getStatus(_mdReqmt2), is(PENDING));
    assertThat(_resettableMarketDataProviderFunction.getCollectedRequests(), contains(_mdReqmt2));

    _resettableMarketDataProviderFunction.resetMarketData(Collections.<MarketDataRequirement, MarketDataItem<?>>emptyMap());

    assertThat(_resettableMarketDataProviderFunction.getCollectedRequests(), is(empty()));
    MarketDataSingleResult result2 = _resettableMarketDataProviderFunction.requestData(ImmutableSet.of(_mdReqmt1, _mdReqmt2));

    assertThat(result2.getStatus(_mdReqmt1), is(PENDING));
    assertThat(result2.getStatus(_mdReqmt2), is(PENDING));

    assertThat(_resettableMarketDataProviderFunction.getCollectedRequests(), containsInAnyOrder(_mdReqmt1, _mdReqmt2));
  }

  @Test
  public void requestsAreAggregatedBetweenCalls() {

    _resettableMarketDataProviderFunction.requestData(_mdReqmt1);
    _resettableMarketDataProviderFunction.requestData(_mdReqmt2);
    _resettableMarketDataProviderFunction.requestData(_mdReqmt1);

    assertThat(_resettableMarketDataProviderFunction.getCollectedRequests(), containsInAnyOrder(_mdReqmt1, _mdReqmt2));
  }
}
