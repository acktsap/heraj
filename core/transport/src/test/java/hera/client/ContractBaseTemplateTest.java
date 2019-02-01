/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.ContextProvider;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKeyGenerator;
import hera.util.Base58Utils;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class ContractBaseTemplateTest extends AbstractTestCase {

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final ContractAddress contractAddress =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final Fee fee = Fee.getDefaultFee();

  protected final AergoKeyGenerator generator = new AergoKeyGenerator();

  @Override
  public void setUp() {
    super.setUp();
  }

  protected ContractBaseTemplate supplyContractBaseTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final ContractBaseTemplate contractBaseTemplate = new ContractBaseTemplate();
    contractBaseTemplate.aergoService = aergoService;
    contractBaseTemplate.contextProvider = new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    };
    return contractBaseTemplate;
  }

  @Test
  public void testGetReceipt() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.Receipt> mockListenableFuture =
        service.submit(new Callable<Blockchain.Receipt>() {
          @Override
          public Blockchain.Receipt call() throws Exception {
            return Blockchain.Receipt.newBuilder().build();
          }
        });
    when(aergoService.getReceipt(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);

    final FinishableFuture<ContractTxReceipt> receipt = contractBaseTemplate
        .getReceiptFunction().apply(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(receipt.get());
  }

  @Test
  public void testDeployWithClientManagedAccount() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
            future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
            return future;
          }
        });

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Account account = new AccountFactory().create(generator.create());
    String encoded = Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});
    final FinishableFuture<ContractTxHash> deployTxHash = contractBaseTemplate
        .getDeployFunction().apply(account, ContractDefinition.of(encoded), 0L, fee);
    assertNotNull(deployTxHash.get());
  }

  @Test
  public void testDeployWithsServerManagedAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    AccountBaseTemplate mockAccountBaseTemplate = mock(AccountBaseTemplate.class);
    final Transaction mockSignedTransaction = mock(Transaction.class);
    when(mockAccountBaseTemplate.getSignFunction())
        .thenReturn(new Function2<Account, RawTransaction, FinishableFuture<Transaction>>() {
          @Override
          public FinishableFuture<Transaction> apply(Account t1, RawTransaction t2) {
            final FinishableFuture<Transaction> future = new FinishableFuture<Transaction>();
            future.success(mockSignedTransaction);
            return future;
          }
        });
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
            future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
            return future;
          }
        });

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);
    contractBaseTemplate.accountBaseTemplate = mockAccountBaseTemplate;
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    Account account = new AccountFactory().create(accountAddress);
    String encoded = Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});
    final FinishableFuture<ContractTxHash> deployTxHash = contractBaseTemplate
        .getDeployFunction().apply(account, ContractDefinition.of(encoded), 0L, fee);
    assertNotNull(deployTxHash.get());
  }

  @Test
  public void testGetContractInterface() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.ABI> mockListenableFuture =
        service.submit(new Callable<Blockchain.ABI>() {
          @Override
          public Blockchain.ABI call() throws Exception {
            return Blockchain.ABI.newBuilder().build();
          }
        });
    when(aergoService.getABI(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);

    final FinishableFuture<ContractInterface> contractInterface =
        contractBaseTemplate.getContractInterfaceFunction().apply(contractAddress);
    assertNotNull(contractInterface.get());
  }

  @Test
  public void testExecuteWithClientManagedAccount() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
            future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
            return future;
          }
        });

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Account account = new AccountFactory().create(generator.create());
    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final FinishableFuture<ContractTxHash> executionTxHash = contractBaseTemplate
        .getExecuteFunction()
        .apply(account, new ContractInvocation(contractAddress, contractFunction), 0L, fee);
    assertNotNull(executionTxHash.get());
  }

  @Test
  public void testExecuteWithServerManagedAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    AccountBaseTemplate mockAccountBaseTemplate = mock(AccountBaseTemplate.class);
    final Transaction mockSignedTransaction = mock(Transaction.class);
    when(mockAccountBaseTemplate.getSignFunction())
        .thenReturn(new Function2<Account, RawTransaction, FinishableFuture<Transaction>>() {
          @Override
          public FinishableFuture<Transaction> apply(Account t1, RawTransaction t2) {
            final FinishableFuture<Transaction> future = new FinishableFuture<Transaction>();
            future.success(mockSignedTransaction);
            return future;
          }
        });
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
            future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
            return future;
          }
        });

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);
    contractBaseTemplate.accountBaseTemplate = mockAccountBaseTemplate;
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Account account = new AccountFactory().create(accountAddress);
    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final FinishableFuture<ContractTxHash> executionTxHash =
        contractBaseTemplate.getExecuteFunction().apply(account,
            new ContractInvocation(contractAddress, contractFunction), 0L, fee);
    assertNotNull(executionTxHash.get());
  }

  @Test
  public void testQuery() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.SingleBytes> mockListenableFuture =
        service.submit(new Callable<Rpc.SingleBytes>() {
          @Override
          public Rpc.SingleBytes call() throws Exception {
            return Rpc.SingleBytes.newBuilder().build();
          }
        });
    when(aergoService.queryContract(any(Blockchain.Query.class))).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);

    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final FinishableFuture<ContractResult> contractResult = contractBaseTemplate
        .getQueryFunction().apply(new ContractInvocation(contractAddress, contractFunction));

    assertNotNull(contractResult.get());
  }

}