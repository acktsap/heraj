/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.api.function.Function1;
import hera.client.internal.FinishableFuture;
import hera.client.internal.FutureChain;
import hera.exception.RpcException;
import org.junit.Test;

public class FutureChainTest extends AbstractTestCase {

  @Test
  public void testOnSuccess() {
    FinishableFuture<Integer> nextFuture = new FinishableFuture<Integer>();
    FutureChain<String, Integer> callback = new FutureChain<String, Integer>(nextFuture, context);
    callback.setSuccessHandler(new Function1<String, Integer>() {
      @Override
      public Integer apply(final String s) {
        return s.length();
      }
    });
    callback.onSuccess(randomUUID().toString());
    nextFuture.get();
  }

  @Test(expected = RpcException.class)
  public void testOnSuccessWithoutHandler() {
    FinishableFuture<Integer> nextFuture = new FinishableFuture<Integer>();
    FutureChain<String, Integer> callback = new FutureChain<String, Integer>(nextFuture, context);
    callback.onSuccess(randomUUID().toString());
    nextFuture.get();
  }

  @Test(expected = Exception.class)
  public void testOnFailure() {
    FinishableFuture<Integer> nextFuture = new FinishableFuture<Integer>();
    FutureChain<String, Integer> callback = new FutureChain<String, Integer>(nextFuture, context);
    callback.setSuccessHandler(new Function1<String, Integer>() {
      @Override
      public Integer apply(final String s) {
        return s.length();
      }
    });
    callback.onFailure(new UnsupportedOperationException());
    nextFuture.get();
  }

  @Test(expected = NullPointerException.class)
  public void testCreation() {
    new FutureChain<String, Integer>(null, null);
  }

}