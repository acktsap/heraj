/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.Authentication;
import hera.client.AergoClient;
import hera.key.Signer;

/**
 * A wallet api holding single identity. It interact with {@link hera.keystore.KeyStore}. Has a
 * signing role.
 *
 * @author taeiklim
 *
 */
public interface WalletApi extends Signer {

  /**
   * Bind an aergo client to use.
   *
   * @param aergoClient an aergo client
   */
  void bind(AergoClient aergoClient);

  /**
   * Unlock an account and bind it to wallet api.
   *
   * @param authentication an authentication to unlock account
   * @return an unlock result
   */
  boolean unlock(Authentication authentication);

  /**
   * Lock an account.
   *
   * @param authentication an authentication to lock account binded to wallet api
   * @return a lock result
   */
  boolean lock(Authentication authentication);

  /**
   * Get transaction api.
   *
   * @return a transaction api
   */
  TransactionApi transactionApi();

  /**
   * Get query api.
   *
   * @return a query api
   */
  QueryApi queryApi();

}
