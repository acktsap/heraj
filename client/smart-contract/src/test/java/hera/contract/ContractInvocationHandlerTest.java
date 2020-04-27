/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.ContractOperation;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.api.model.StateVariable;
import hera.client.AergoClient;
import hera.contract.ContractInvocationHandler;
import hera.contract.ContractInvocationPreparable;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.keystore.InMemoryKeyStore;
import hera.model.KeyAlias;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ContractInvocationHandlerTest extends AbstractTestCase {

  private interface ContractTest extends ContractInvocationPreparable {

    void execute(int arg);

    int query();

  }

  private final ContractAddress contractAddress = ContractAddress
      .of("AmgSvgaUXFb4PFLn5MU5wiYNihcQrwNhNMzaG5h68q99gBNTTkWK");

  private final ClassLoader classLoader = getClass().getClassLoader();

  @Test
  public void testPrepare() {
    final InvocationHandler invocationHandler = new ContractInvocationHandler(contractAddress);
    ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);

    final WalletApi walletApi = supplyWalletApi();
    final Fee fee = Fee.ZERO;
    proxy.setWalletApi(walletApi);
    proxy.setFee(fee);
  }

  @Test
  public void testWithReturn() {
    final InvocationHandler invocationHandler = new ContractInvocationHandler(contractAddress);
    ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);

    final WalletApi walletApi = supplyWalletApi();
    proxy.setWalletApi(walletApi);
    proxy.setFee(Fee.ZERO);
    proxy.execute(0);
  }

  @Test
  public void testWithoutReturn() {
    final InvocationHandler invocationHandler = new ContractInvocationHandler(contractAddress);
    ContractTest proxy = (ContractTest) Proxy.newProxyInstance(classLoader,
        new Class<?>[]{ContractTest.class}, invocationHandler);

    final WalletApi walletApi = supplyWalletApi();
    proxy.setWalletApi(walletApi);
    proxy.query();
  }

  protected WalletApi supplyWalletApi() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    final KeyAlias alias = KeyAlias.of(randomUUID().toString().replaceAll("-", ""));
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(alias, password);
    final AergoKey key = new AergoKeyGenerator().create();
    keyStore.save(authentication, key);

    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    walletApi.bind(supplyAergoClient());
    walletApi.unlock(authentication);

    return walletApi;
  }

  @SuppressWarnings("unchecked")
  protected AergoClient supplyAergoClient() {
    try {
      final ContractOperation mockContractOperation = mock(ContractOperation.class);

      final ContractInterface contractInterface = supplyContractInterface();
      when(mockContractOperation.getContractInterface(any(ContractAddress.class)))
          .thenReturn(contractInterface);
      final ContractTxHash contractTxHash = ContractTxHash.of(BytesValue.EMPTY);
      when(mockContractOperation
          .executeTx(any(Signer.class), any(ContractInvocation.class), anyLong(), any(Fee.class)))
          .thenReturn(contractTxHash);
      final ContractResult contractResult = mock(ContractResult.class);
      when(contractResult.bind(any(Class.class))).thenReturn(1);
      when(mockContractOperation.query(any(ContractInvocation.class))).thenReturn(contractResult);

      final AergoClient mockClient = mock(AergoClient.class);
      when(mockClient.getContractOperation()).thenReturn(mockContractOperation);

      return mockClient;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  protected ContractInterface supplyContractInterface() {
    final List<ContractFunction> contractFunctions = asList(
        new ContractFunction("execute", asList("arg")),
        new ContractFunction("query", false, true, false)
    );
    final List<StateVariable> stateVariables = new ArrayList<>();
    return ContractInterface.newBuilder()
        .address(contractAddress)
        .version("1.0")
        .language("lua")
        .functions(contractFunctions)
        .stateVariables(stateVariables)
        .build();
  }

}
