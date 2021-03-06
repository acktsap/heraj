/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import org.slf4j.Logger;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockchainStatusConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<BlockchainStatus, Rpc.BlockchainStatus> domainConverter =
      new Function1<BlockchainStatus, Rpc.BlockchainStatus>() {

        @Override
        public Rpc.BlockchainStatus apply(final BlockchainStatus domainBlockchainStatus) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.BlockchainStatus, BlockchainStatus> rpcConverter =
      new Function1<Rpc.BlockchainStatus, BlockchainStatus>() {

        @Override
        public BlockchainStatus apply(final Rpc.BlockchainStatus rpcBlockchainStatus) {
          logger.trace("Rpc blockchain status to convert: {}", rpcBlockchainStatus);
          final BlockchainStatus domainBlockchainStatus = BlockchainStatus.newBuilder()
              .bestHeight(rpcBlockchainStatus.getBestHeight())
              .bestBlockHash(
                  new BlockHash(of(rpcBlockchainStatus.getBestBlockHash().toByteArray())))
              .consensus(rpcBlockchainStatus.getConsensusInfo())
              .chainIdHash(
                  new ChainIdHash(of(rpcBlockchainStatus.getBestChainIdHash().toByteArray())))
              .build();
          logger.trace("Domain blockchain status converted: {}", domainBlockchainStatus);
          return domainBlockchainStatus;
        }
      };

  public ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> create() {
    return new ModelConverter<>(domainConverter,
        rpcConverter);
  }

}
