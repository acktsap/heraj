/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.TxSigner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Private
@ApiStability.Unstable
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AccountWithAddressAndSigner extends AbstractAccount implements TxSigner {

  @NonNull
  @Getter
  protected final AccountAddress address;

  @NonNull
  @Getter
  protected final TxSigner delegate;

  @Override
  public AergoKey getKey() {
    return null;
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return delegate.sign(rawTransaction);
  }

}
