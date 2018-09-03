/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.tupleorerror.ResultOrError;
import java.util.List;

public interface AccountOperation {

  /**
   * Get account list.
   *
   * @return account list or error
   */
  ResultOrError<List<Account>> list();

  /**
   * Create an account with password.
   *
   * @param password account password
   * @return created account or error
   */
  ResultOrError<Account> create(String password);

  /**
   * Get account by address.
   *
   * @param address account address
   * @return an account or error
   */
  ResultOrError<Account> get(AccountAddress address);

  /**
   * Lock an account.
   *
   * @param authentication account authentication
   * @return lock result or error
   */
  ResultOrError<Boolean> lock(Authentication authentication);

  /**
   * Unlock an account.
   *
   * @param authentication account authentication
   * @return unlock result or error
   */
  ResultOrError<Boolean> unlock(Authentication authentication);
}
