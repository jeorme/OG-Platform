/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.trace;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.threeten.bp.Duration;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * Graph representing the calls made to calculate a single result.
 */
class CallGraphBuilder {

  /**
   * The invoked method.
   */
  private final Method _method;
  /**
   * The method arguments.
   */
  private final Object[] _args;
  /**
   * The child call graphs.
   */
  private final List<CallGraphBuilder> _callGraphBuilders = Lists.newArrayList();
  /**
   * The return value.
   */
  private Object _returnValue;
  /**
   * Execution time of the call.
   */
  private Duration _duration;
  /**
   * The throwable.
   */
  private Throwable _throwable;

  /**
   * Creates an instance.
   * 
   * @param method  the invoked method, not null
   * @param args  the method arguments, not null
   */
  CallGraphBuilder(Method method, Object... args) {
    _method = method;
    _args = args;
  }

  //-------------------------------------------------------------------------
  void called(CallGraphBuilder callGraphBuilder) {
    _callGraphBuilders.add(callGraphBuilder);
  }

  void returned(Object returnValue, Duration duration) {
    _returnValue = returnValue;
    _duration = duration;
  }

  void threw(Throwable throwable, Duration duration) {
    _throwable = throwable;
    _duration = duration;
  }

  List<CallGraphBuilder> calls() {
    return _callGraphBuilders;
  }

  public CallGraph createTrace() {
    return createTrace(this);
  }

  private static CallGraph createTrace(CallGraphBuilder callGraphBuilder) {

    List<CallGraph> calls = Lists.newArrayListWithCapacity(callGraphBuilder._callGraphBuilders.size());

    for (CallGraphBuilder childCallGraphBuilder : callGraphBuilder._callGraphBuilders) {
      calls.add(createTrace(childCallGraphBuilder));
    }

    List<Object> args = callGraphBuilder._args == null ?
        Collections.emptyList() :
        Arrays.asList(callGraphBuilder._args);

    Class<?> throwableClass;
    String errorMessage;
    String stackTrace;

    if (callGraphBuilder._throwable == null) {
      throwableClass = null;
      errorMessage = null;
      stackTrace = null;
    } else {
      throwableClass = callGraphBuilder._throwable.getClass();
      errorMessage = callGraphBuilder._throwable.getMessage();
      stackTrace = Throwables.getStackTraceAsString(callGraphBuilder._throwable);
    }

    return new CallGraph(callGraphBuilder._method.getDeclaringClass(),
                         callGraphBuilder._method.getName(),
                         Arrays.asList(callGraphBuilder._method.getParameterTypes()),
                         convertArguments(args),
                         callGraphBuilder._returnValue,
                         throwableClass,
                         errorMessage,
                         stackTrace,
                         calls,
                         callGraphBuilder._duration);
  }

  private static List<Object> convertArguments(List<Object> args) {
    return args;
  }

  //-------------------------------------------------------------------------
  /**
   * Provides a pretty-printed version of the call graph as a string.
   * 
   * @return a string representation of the call graph, not null
   */
  public String prettyPrint() {
    return prettyPrint(new StringBuilder(), this, "", "").toString();
  }

  private static StringBuilder prettyPrint(StringBuilder builder, CallGraphBuilder callGraphBuilder, String indent, String childIndent) {
    builder.append('\n').append(indent).append(callGraphBuilder.toString());
    for (Iterator<CallGraphBuilder> itr = callGraphBuilder.calls().iterator(); itr.hasNext(); ) {
      CallGraphBuilder next = itr.next();
      String newIndent;
      String newChildIndent;
      boolean isFinalChild = !itr.hasNext();
      if (!isFinalChild) {
        newIndent = childIndent + " |--";
        newChildIndent = childIndent + " |  ";
      } else {
        newIndent = childIndent + " `--";
        newChildIndent = childIndent + "    ";
      }
      // these are unicode characters for box drawing
/*
      if (!isFinalChild) {
        newIndent = childIndent + " \u251c\u2500\u2500";
        newChildIndent = childIndent + " \u2502  ";
      } else {
        newIndent = childIndent + " \u2514\u2500\u2500";
        newChildIndent = childIndent + "    ";
      }
*/
      prettyPrint(builder, next, newIndent, newChildIndent);
    }
    return builder;
  }

  //-------------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final CallGraphBuilder other = (CallGraphBuilder) obj;
    return
        Objects.equals(this._method, other._method) &&
        Arrays.deepEquals(this._args, other._args) &&
        Objects.equals(this._callGraphBuilders, other._callGraphBuilders) &&
        Objects.equals(this._returnValue, other._returnValue) &&
        Objects.equals(this._throwable, other._throwable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_method, Arrays.deepHashCode(_args), _callGraphBuilders, _returnValue, _throwable);
  }

  @Override
  public String toString() {
    return _method.getDeclaringClass().getSimpleName() + "." + _method.getName() + "()" +
        (_throwable == null ? " -> " + _returnValue : " threw " + _throwable) +
        (_args == null ? "" : ", args: " + Arrays.deepToString(_args));
  }
}
