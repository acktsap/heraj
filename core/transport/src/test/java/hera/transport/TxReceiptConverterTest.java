/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractTxReceipt;
import hera.api.model.TxReceipt;
import org.junit.Test;
import types.Blockchain;

public class TxReceiptConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<TxReceipt, Blockchain.Receipt> converter =
        new TxReceiptConverterFactory().create();

    final Blockchain.Receipt rpcAccount = Blockchain.Receipt.newBuilder().build();
    final TxReceipt domainReceipt = converter.convertToDomainModel(rpcAccount);
    assertNotNull(domainReceipt);
  }

}
