/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.ContextHolder;
import hera.StrategyAcceptable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAergoApi;
import hera.api.AccountOperation;
import hera.api.BlockOperation;
import hera.api.BlockchainOperation;
import hera.api.ContractOperation;
import hera.api.TransactionOperation;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class AergoClient extends AbstractAergoApi implements Closeable, AutoCloseable {

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = new ManagedChannelFactory().apply(ContextHolder.get());

  @Getter(lazy = true)
  private final AccountOperation accountOperation = resolveInjection(new AccountTemplate());

  @Getter(lazy = true)
  private final BlockOperation blockOperation = resolveInjection(new BlockTemplate());

  @Getter(lazy = true)
  private final BlockchainOperation blockchainOperation =
      resolveInjection(new BlockchainTemplate());

  @Getter(lazy = true)
  private final TransactionOperation transactionOperation =
      resolveInjection(new TransactionTemplate());

  @Getter(lazy = true)
  private final ContractOperation contractOperation = resolveInjection(new ContractTemplate());

  protected <T> T resolveInjection(final T target) {
    if (target instanceof StrategyAcceptable) {
      ((StrategyAcceptable) target).accept(StrategyChain.of(ContextHolder.get()));
    }
    if (target instanceof ChannelInjectable) {
      ((ChannelInjectable) target).injectChannel(getChannel());
    }
    return target;
  }

  @Override
  public void close() {
    try {
      // FIXME when no channel
      getChannel().shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }

}
