/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.proxy;


import static com.opengamma.sesame.StandardResultGenerator.failure;
import static com.opengamma.sesame.StandardResultGenerator.marketDataFailure;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.opengamma.sesame.FailureStatus;
import com.opengamma.sesame.FunctionResult;
import com.opengamma.sesame.marketdata.MarketDataSingleResult;
import com.opengamma.util.ArgumentChecker;

/**
 * Generates a proxy which
 */
public class ProxyGenerator {

  @SuppressWarnings("unchecked")
  public <T> T generate(final T delegate, Class<T> iface) {

    ArgumentChecker.notNull(delegate, "delegate");
    ArgumentChecker.notNull(iface, "iface");
    ArgumentChecker.isTrue(iface.isInterface(), "Can only generate proxies for interfaces");

    Class<?> delegateClass = delegate.getClass();

    InvocationHandler handler = new InvocationHandler() {

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
          return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
          // We got an exception from the underlying call but the proxy mechanism
          // automatically wraps it, so pull it out
          Throwable cause = e.getCause();

          if (method.getReturnType() == FunctionResult.class) {
            return failure(FailureStatus.ERROR, "Received exception: {}", cause);
          } else if (method.getReturnType() == MarketDataSingleResult.class) {
            return marketDataFailure(FailureStatus.ERROR, "Received exception: {}", cause);
          } else {
            throw cause;
          }
        }
      }
    };

    return (T) Proxy.newProxyInstance(delegateClass.getClassLoader(), new Class[] {iface}, handler);
  }
}
