/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import com.opengamma.engine.analytics.AnalyticFunctionRepository;
import com.opengamma.engine.livedata.LiveDataAvailabilityProvider;
import com.opengamma.engine.livedata.LiveDataSnapshotProvider;
import com.opengamma.engine.position.PositionMaster;
import com.opengamma.engine.security.SecurityMaster;
import com.opengamma.engine.view.calcnode.CalculationJobSink;
import com.opengamma.engine.view.calcnode.JobCompletionRetriever;

/**
 * A collection for everything relating to processing a particular view.
 *
 * @author kirk
 */
public class ViewProcessingContext {
  private final LiveDataAvailabilityProvider _liveDataAvailabilityProvider;
  private final LiveDataSnapshotProvider _liveDataSnapshotProvider;
  private final AnalyticFunctionRepository _analyticFunctionRepository;
  private final PositionMaster _positionMaster;
  private final SecurityMaster _securityMaster;
  private final ViewComputationCacheSource _computationCacheSource;
  private final CalculationJobSink _jobSink;
  private final JobCompletionRetriever _jobCompletionRetriever;

  public ViewProcessingContext(
      LiveDataAvailabilityProvider liveDataAvailabilityProvider,
      LiveDataSnapshotProvider liveDataSnapshotProvider,
      AnalyticFunctionRepository analyticFunctionRepository,
      PositionMaster positionMaster,
      SecurityMaster securityMaster,
      ViewComputationCacheSource computationCacheSource,
      CalculationJobSink jobSink,
      JobCompletionRetriever jobCompletionRetriever
      ) {
    // TODO kirk 2009-09-25 -- Check Inputs
    _liveDataAvailabilityProvider = liveDataAvailabilityProvider;
    _liveDataSnapshotProvider = liveDataSnapshotProvider;
    _analyticFunctionRepository = analyticFunctionRepository;
    _positionMaster = positionMaster;
    _securityMaster = securityMaster;
    _computationCacheSource = computationCacheSource;
    _jobSink = jobSink;
    _jobCompletionRetriever = jobCompletionRetriever;
  }

  /**
   * @return the liveDataAvailabilityProvider
   */
  public LiveDataAvailabilityProvider getLiveDataAvailabilityProvider() {
    return _liveDataAvailabilityProvider;
  }

  /**
   * @return the liveDataSnapshotProvider
   */
  public LiveDataSnapshotProvider getLiveDataSnapshotProvider() {
    return _liveDataSnapshotProvider;
  }

  /**
   * @return the analyticFunctionRepository
   */
  public AnalyticFunctionRepository getAnalyticFunctionRepository() {
    return _analyticFunctionRepository;
  }

  /**
   * @return the positionMaster
   */
  public PositionMaster getPositionMaster() {
    return _positionMaster;
  }

  /**
   * @return the securityMaster
   */
  public SecurityMaster getSecurityMaster() {
    return _securityMaster;
  }

  /**
   * @return the computationCacheSource
   */
  public ViewComputationCacheSource getComputationCacheSource() {
    return _computationCacheSource;
  }

  /**
   * @return the jobSink
   */
  public CalculationJobSink getJobSink() {
    return _jobSink;
  }

  /**
   * @return the jobCompletionRetriever
   */
  public JobCompletionRetriever getJobCompletionRetriever() {
    return _jobCompletionRetriever;
  }

}
