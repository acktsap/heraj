/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Name;
import hera.api.model.Peer;
import hera.api.model.StakeInfo;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.exception.CommitException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AccountOperationIT extends AbstractIT {

  protected static AergoClient aergoClient;

  protected final NonceProvider nonceProvider = new SimpleNonceProvider();
  protected final AergoKey rich = AergoKey
      .of("47qD7YfyaZADwyShHWAWegf1ZqYKPT2aSJ9mDFr3qUzBENTLSNZQ7YxC9xqzDdmUTrpEPQUAS", "1234");
  protected AergoKey key;

  @BeforeClass
  public static void before() {
    final TestClientFactory clientFactory = new TestClientFactory();
    aergoClient = clientFactory.get();
  }

  @AfterClass
  public static void after() throws Exception {
    aergoClient.close();
  }

  @Before
  public void setUp() {
    key = new AergoKeyGenerator().create();
    final AccountState state = aergoClient.getAccountOperation().getState(rich.getAddress());
    logger.debug("Rich state: {}", state);
    nonceProvider.bindNonce(state);
    aergoClient.getTransactionOperation()
        .sendTx(rich, key.getAddress(), Aer.of("10000", Unit.AERGO),
            nonceProvider.incrementAndGetNonce(rich.getPrincipal()), Fee.INFINITY,
            BytesValue.EMPTY);
    waitForNextBlockToGenerate();
  }

  @Test
  public void shouldCreateName() {
    // when
    final Name name = randomName();
    final long preHeight = aergoClient.getBlockchainOperation().getBlockchainStatus()
        .getBestHeight();
    aergoClient.getAccountOperation().createNameTx(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final long currentHeight = aergoClient.getBlockchainOperation().getBlockchainStatus()
        .getBestHeight();
    final AccountAddress owner = aergoClient.getAccountOperation().getNameOwner(name);
    assertEquals(key.getAddress(), owner);
    final AccountAddress shouldBeNull = aergoClient.getAccountOperation()
        .getNameOwner(name, preHeight);
    assertNull(shouldBeNull);
    final AccountAddress shouldNotNull = aergoClient.getAccountOperation()
        .getNameOwner(name, currentHeight);
    assertNotNull(shouldNotNull);
  }

  @Test
  public void shouldCreateNameFailOnInvalidNonce() {
    try {
      // when
      final Name name = randomName();
      aergoClient.getAccountOperation().createNameTx(key, name, 0L);
      fail();
    } catch (CommitException e) {
      // then
      assertEquals(CommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldUpdateName() {
    // given
    final Name name = randomName();
    aergoClient.getAccountOperation().createNameTx(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
    aergoClient.getAccountOperation().updateNameTx(key, name, newOwner,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final AccountAddress owner = aergoClient.getAccountOperation().getNameOwner(name);
    assertEquals(newOwner, owner);
  }

  @Test
  public void shouldUpdateNameFailOnInvalidName() {
    // given
    final Name name = randomName();
    aergoClient.getAccountOperation().createNameTx(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    try {
      // when
      final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
      final Name invalidName = randomName();
      aergoClient.getAccountOperation().updateNameTx(key, invalidName, newOwner,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (CommitException e) {
      // then
    }
  }

  @Test
  public void shouldUpdateNameFailOnInvalidNonce() {
    // given
    final Name name = randomName();
    aergoClient.getAccountOperation().createNameTx(key, name,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    try {
      // when
      final AccountAddress newOwner = new AergoKeyGenerator().create().getAddress();
      aergoClient.getAccountOperation().updateNameTx(key, name, newOwner, 0L);
      fail();
    } catch (CommitException e) {
      // then
      assertEquals(CommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldReturnNullOnNameWithoutOwner() {
    // when
    final Name name = randomName();
    final AccountAddress nameOwner = aergoClient.getAccountOperation().getNameOwner(name);

    // then
    assertNull(nameOwner);
  }

  @Test
  public void shouldStake() {
    // when
    final AccountState beforeState = aergoClient.getAccountOperation().getState(key.getAddress());
    final Aer minimumAmount =
        aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
    aergoClient.getAccountOperation().stakeTx(key, minimumAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final StakeInfo stakingInfo =
        aergoClient.getAccountOperation().getStakeInfo(key.getAddress());
    assertEquals(key.getAddress(), stakingInfo.getAddress());
    assertEquals(minimumAmount, stakingInfo.getAmount());
    final AccountState afterState = aergoClient.getAccountOperation().getState(key.getAddress());
    assertEquals(beforeState.getBalance(), afterState.getBalance().add(minimumAmount));
  }

  @Test
  public void shouldStakeFailWithLessThanMinimumAmount() {
    try {
      // when
      final Aer minimumAmount =
          aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
      aergoClient.getAccountOperation().stakeTx(key, minimumAmount.subtract(Aer.ONE),
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (CommitException e) {
      // then
    }
  }

  @Test
  public void shouldStakeFailWithPoor() {
    try {
      // when
      final AergoKey key = new AergoKeyGenerator().create();
      final Aer minimumAmount =
          aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
      aergoClient.getAccountOperation().stakeTx(key, minimumAmount,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (CommitException e) {
      // then
      assertEquals(CommitException.CommitStatus.INSUFFICIENT_BALANCE, e.getCommitStatus());
    }
  }

  @Test
  public void shouldStakeFailWithInvalidNonce() {
    try {
      // when
      final Aer minimumAmount =
          aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
      aergoClient.getAccountOperation().stakeTx(key, minimumAmount, 0L);
      fail();
    } catch (CommitException e) {
      // then
      assertEquals(CommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldUnstakeFailOnRightAfterStake() {
    // given
    final Aer minimumAmount =
        aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
    aergoClient.getAccountOperation().stakeTx(key, minimumAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    try {
      aergoClient.getAccountOperation().unstakeTx(key, minimumAmount,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
    } catch (Exception e) {
      // then : not enough time has passed to unstake
    }
  }

  @Test
  public void shouldUnstakeFailOnInvalidNonce() {
    // given
    final Aer minimumAmount =
        aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
    aergoClient.getAccountOperation().stakeTx(key, minimumAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    try {
      aergoClient.getAccountOperation().unstakeTx(key, minimumAmount, 0L);
    } catch (CommitException e) {
      // then
      assertEquals(CommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }

  @Test
  public void shouldVoteOnStakedOne() {
    // given
    final Aer minimumAmount =
        aergoClient.getBlockchainOperation().getChainInfo().getMinimumStakingAmount();
    aergoClient.getAccountOperation().stakeTx(key, minimumAmount,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // when
    final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers(true, true);
    final List<String> expected = new ArrayList<>();
    expected.add(peers.get(0).getPeerId());
    aergoClient.getAccountOperation().voteTx(key, "voteBP", expected,
        nonceProvider.incrementAndGetNonce(key.getAddress()));
    waitForNextBlockToGenerate();

    // then
    final AccountTotalVote keyVoteTotal =
        aergoClient.getAccountOperation().getVotesOf(key.getAddress());
    final List<String> actual = keyVoteTotal.getVoteInfos().get(0).getCandidateIds();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldVoteFailOnUnstakedOne() {
    try {
      // when
      final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers(true, true);
      final List<String> candidates = new ArrayList<>();
      candidates.add(peers.get(0).getPeerId());
      aergoClient.getAccountOperation().voteTx(key, "voteBP", candidates,
          nonceProvider.incrementAndGetNonce(key.getAddress()));
      fail();
    } catch (CommitException e) {
      // then
    }
  }

}
