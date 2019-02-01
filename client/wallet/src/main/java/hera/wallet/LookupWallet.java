/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BlockProducer;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.StakingInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
import hera.client.AergoClient;
import hera.exception.WalletException;
import java.io.Closeable;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;

public abstract class LookupWallet implements LookupClient, Closeable {

  protected static final int SHOW_COUNT = 23;

  protected final Logger logger = getLogger(getClass());

  @Getter(value = AccessLevel.PROTECTED)
  protected final AergoClient aergoClient;

  protected LookupWallet(final AergoClient aergoClient) {
    logger.debug("Binded client: {}", aergoClient);
    this.aergoClient = aergoClient;
  }

  @Override
  public AccountState getAccountState(final Account account) {
    return getAergoClient().getAccountOperation().getState(account);
  }

  @Override
  public AccountState getAccountState(final AccountAddress accountAddress) {
    return getAergoClient().getAccountOperation().getState(accountAddress);
  }

  @Override
  public AccountAddress getNameOwner(final String name) {
    return getAergoClient().getAccountOperation().getNameOwner(name);
  }

  @Override
  public StakingInfo getStakingInfo(final Account account) {
    return getStakingInfo(account.getAddress());
  }

  @Override
  public StakingInfo getStakingInfo(final AccountAddress accountAddress) {
    return getAergoClient().getAccountOperation().getStakingInfo(accountAddress);
  }

  @Override
  public List<BlockProducer> listElectedBlockProducers() {
    return listElectedBlockProducers(SHOW_COUNT);
  }

  @Override
  public List<BlockProducer> listElectedBlockProducers(final long showCount) {
    return getAergoClient().getBlockchainOperation().listElectedBlockProducers(showCount);
  }

  @Override
  public List<VotingInfo> listVotesOf(final Account account) {
    return listVotesOf(account.getAddress());
  }

  @Override
  public List<VotingInfo> listVotesOf(final AccountAddress accountAddress) {
    return getAergoClient().getBlockchainOperation().listVotesOf(accountAddress);
  }

  @Override
  public List<AccountAddress> listServerKeyStoreAccounts() {
    return getAergoClient().getKeyStoreOperation().list();
  }

  @Override
  public BlockHash getBestBlockHash() {
    return getAergoClient().getBlockchainOperation().getBlockchainStatus().getBestBlockHash();
  }

  @Override
  public long getBestBlockHeight() {
    return getAergoClient().getBlockchainOperation().getBlockchainStatus().getBestHeight();
  }

  @Override
  public List<Peer> listNodePeers() {
    return getAergoClient().getBlockchainOperation().listPeers();
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return getAergoClient().getBlockchainOperation().listPeerMetrics();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return getAergoClient().getBlockchainOperation().getNodeStatus();
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    return getAergoClient().getBlockOperation().getBlock(blockHash);
  }

  @Override
  public Block getBlock(final long height) {
    return getAergoClient().getBlockOperation().getBlock(height);
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final BlockHash blockHash, final int size) {
    return getAergoClient().getBlockOperation().listBlockHeaders(blockHash, size);
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final long height, final int size) {
    return getAergoClient().getBlockOperation().listBlockHeaders(height, size);
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    return getAergoClient().getTransactionOperation().getTransaction(txHash);
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    return getAergoClient().getContractOperation().getReceipt(contractTxHash);
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    return getAergoClient().getContractOperation().getContractInterface(contractAddress);
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    return getAergoClient().getContractOperation().query(contractInvocation);
  }

  @Override
  public void close() {
    try {
      getAergoClient().close();
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

}