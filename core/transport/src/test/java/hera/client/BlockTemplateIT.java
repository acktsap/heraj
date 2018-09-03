/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BlockchainStatus;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class BlockTemplateIT extends AbstractIT {

  protected BlockTemplate blockTemplate = null;

  protected BlockChainTemplate blockChainTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    blockTemplate = new BlockTemplate(channel);
    blockChainTemplate = new BlockChainTemplate(channel);
  }

  @Test
  public void testGetBlockByHash() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final BlockHash bestBlockHash = status.getBestBlockHash();
    final Block block = blockTemplate.getBlock(bestBlockHash).getResult();
    assertNotNull(block);
    assertTrue(0 < block.getHash().getBytesValue().length);
    assertTrue(0 < block.getPreviousHash().getBytesValue().length);
  }

  @Test
  public void testGetBlockByHeight() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final long bestBlockHeight = status.getBestHeight();
    final Block block = blockTemplate.getBlock(bestBlockHeight).getResult();
    assertNotNull(block);
    assertTrue(0 < block.getHash().getBytesValue().length);
    assertTrue(0 < block.getPreviousHash().getBytesValue().length);
  }

  @Test
  public void testListBlockHeadersByHash() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final BlockHash bestBlockHash = status.getBestBlockHash();
    final int size = 3;
    final List<BlockHeader> blockHeaders =
        blockTemplate.listBlockHeaders(bestBlockHash, size).getResult();
    assertNotNull(blockHeaders);
    assertTrue(blockHeaders.size() == size);
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final long bestBlockHeight = status.getBestHeight();
    final int size = 3;
    final List<BlockHeader> blockHeaders =
        blockTemplate.listBlockHeaders(bestBlockHeight, size).getResult();
    assertNotNull(blockHeaders);
    assertTrue(blockHeaders.size() == size);
  }
}
