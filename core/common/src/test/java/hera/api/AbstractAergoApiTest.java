/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import org.junit.Test;

public class AbstractAergoApiTest {

  protected final AbstractAergoApi aergoApi = new AbstractAergoApi();

  @Test(expected = UnsupportedOperationException.class)
  public void testGetAccountOperation() {
    aergoApi.getAccountOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetKeyStoreOperation() {
    aergoApi.getKeyStoreOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetBlockOperation() {
    aergoApi.getBlockOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetBlockchainOperation() {
    aergoApi.getBlockchainOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetTransactionOperation() {
    aergoApi.getTransactionOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetContractOperation() {
    aergoApi.getContractOperation();
  }

}
