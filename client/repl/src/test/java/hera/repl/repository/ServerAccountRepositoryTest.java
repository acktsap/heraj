/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.repository;

import static hera.util.HexUtils.encode;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.AccountOperation;
import hera.api.AergoApi;
import hera.api.TransactionOperation;
import hera.AbstractTestCase;
import hera.repl.SecuredAccount;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ServerAccountRepositoryTest extends AbstractTestCase {

  @Mock
  protected AergoApi aergoApi;

  @Mock
  protected AccountOperation accountOperation;

  @Mock
  protected TransactionOperation transactionOperation;

  protected ServerAccountRepository accountRepository;

  protected final String address = encode(randomUUID().toString().getBytes());
  protected final String password = randomUUID().toString();

  @Before
  public void setUp() {
    when(aergoApi.getAccountOperation()).thenReturn(accountOperation);
    when(aergoApi.getTransactionOperation()).thenReturn(transactionOperation);

    accountRepository = new ServerAccountRepository(aergoApi);
  }

  @Test
  public void testList() throws IOException {
    when(accountOperation.list()).thenReturn(emptyList());
    accountRepository.list();
  }

  @Test
  public void testFind() throws IOException {
    assertNotNull(accountRepository.find(address));
  }

  @Test
  public void testCreate() throws Exception {
    accountRepository.create(password);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testDelete() throws IOException {
    accountRepository.delete(address);
  }

  @Test
  public void testSave() throws IOException {
    final SecuredAccount account = SecuredAccount.of(address, password);
    accountRepository.save(account);
  }

  @Test
  public void testUnlock() throws IOException {
    accountRepository.unlock(address, password);
  }

  @Test
  public void testLock() throws IOException {
    accountRepository.lock(address, password);
  }

  @Test
  public void testSendTransaction() throws IOException {
    when(transactionOperation.sign(any(Transaction.class))).thenReturn(new Signature());
    final Transaction transaction = new Transaction();
    accountRepository.sendTransaction(transaction);
  }
}