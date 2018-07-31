/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import org.junit.Test;
import types.Blockchain;

public class TransactionConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Transaction, Blockchain.Tx> converter = new TransactionConverterFactory()
        .create();

    final Transaction domainTransaction = new Transaction();
    domainTransaction.setSender(AccountAddress.of(randomUUID().toString().getBytes()));
    domainTransaction.setRecipient(AccountAddress.of(randomUUID().toString().getBytes()));
    final Blockchain.Tx rpcTransaction = converter.convertToRpcModel(domainTransaction);
    final Transaction actualDomainTransaction = converter.convertToDomainModel(rpcTransaction);
    assertNotNull(actualDomainTransaction);
  }

}
