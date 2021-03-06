/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import java.util.List;

/**
 * Provide block related operations. It provides followings:
 *
 * @author bylee, Taeik Lim
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface BlockOperation {

  /**
   * Get block meta data by hash.
   *
   * @param blockHash block hash
   * @return block meta data. null if no matching one.
   */
  BlockMetadata getBlockMetadata(BlockHash blockHash);

  /**
   * Get block meta data by height.
   *
   * @param height block's height
   * @return block meta data. null if no matching one.
   */
  BlockMetadata getBlockMetadata(long height);

  /**
   * Get list of block meta data of {@code size} backward starting from block for provided hash.
   *
   * @param blockHash block hash
   * @param size      block list size whose upper bound is 1000
   * @return list of block meta data. empty list if no matching one.
   */
  List<BlockMetadata> listBlockMetadatas(BlockHash blockHash, int size);

  /**
   * Get list of block meta data of {@code size} backward starting from block for provided height.
   *
   * @param height block's height
   * @param size   block list size whose upper bound is 1000
   * @return list of block meta data. empty list if no matching one.
   */
  List<BlockMetadata> listBlockMetadatas(long height, int size);

  /**
   * Get block by hash.
   *
   * @param blockHash block hash
   * @return block. null if no matching one.
   */
  Block getBlock(BlockHash blockHash);

  /**
   * Get block by height.
   *
   * @param height block's height
   * @return block. null if no matching one.
   */
  Block getBlock(long height);

  /**
   * Subscribe block metadata stream which is triggered everytime new block is generated.
   *
   * @param observer a stream observer which is invoked on new block metadata
   * @return a block subscription
   * @deprecated use {@link #subscribeBlockMetadata(StreamObserver)} instead.
   */
  @Deprecated
  Subscription<BlockMetadata> subscribeNewBlockMetadata(StreamObserver<BlockMetadata> observer);

  /**
   * Subscribe block metadata stream which is triggered everytime new block is generated.
   *
   * @param observer a stream observer which is invoked on new block metadata
   * @return a block subscription
   */
  Subscription<BlockMetadata> subscribeBlockMetadata(StreamObserver<BlockMetadata> observer);

  /**
   * Subscribe block stream which is triggered everytime new block is generated.
   *
   * @param observer a stream observer which is invoked on new block
   * @return a block subscription
   * @deprecated use {@link #subscribeBlock(StreamObserver)} instead.
   */
  @Deprecated
  Subscription<Block> subscribeNewBlock(StreamObserver<Block> observer);

  /**
   * Subscribe block stream which is triggered everytime new block is generated.
   *
   * @param observer a stream observer which is invoked on new block
   * @return a block subscription
   */
  Subscription<Block> subscribeBlock(StreamObserver<Block> observer);

}
