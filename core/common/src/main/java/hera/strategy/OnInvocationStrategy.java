/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.FunctionDecorator;

public interface OnInvocationStrategy extends Strategy, FunctionDecorator {

  <R> R action(hera.api.tupleorerror.Function originFunction, Function0<R> functionWithArgs);

  @Override
  default <R> Function0<R> apply(Function0<R> f) {
    return () -> action(f, f);
  }

  @Override
  default <T, R> Function1<T, R> apply(Function1<T, R> f) {
    return (T t) -> action(f, () -> f.apply(t));
  }

  @Override
  default <T1, T2, R> Function2<T1, T2, R> apply(Function2<T1, T2, R> f) {
    return (T1 t1, T2 t2) -> action(f, () -> f.apply(t1, t2));
  }

  @Override
  default <T1, T2, T3, R> Function3<T1, T2, T3, R> apply(Function3<T1, T2, T3, R> f) {
    return (T1 t1, T2 t2, T3 t3) -> action(f, () -> f.apply(t1, t2, t3));
  }

  @Override
  default <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> apply(
      Function4<T1, T2, T3, T4, R> f) {
    return (T1 t1, T2 t2, T3 t3, T4 t4) -> action(f, () -> f.apply(t1, t2, t3, t4));
  }

}