/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Transaction;
import org.junit.Test;
import types.Blockchain;

public class TransactionInBlockConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Transaction, Blockchain.TxInBlock> converter =
        new TransactionInBlockConverterFactory().create();

    final Transaction domainTransaction = new Transaction();
    final Blockchain.TxInBlock rpcTransaction = converter.convertToRpcModel(domainTransaction);
    final Transaction actualDomainTransaction = converter.convertToDomainModel(rpcTransaction);
    assertNotNull(actualDomainTransaction);
  }

}