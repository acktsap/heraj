/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.concurrent.atomic.AtomicLong;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public abstract class AbstractAccount implements Account {

  protected AtomicLong nonce = new AtomicLong(0);

  @Override
  public void bindState(final AccountState state) {
    setNonce(state.getNonce());
  }

  @Override
  public void setNonce(final long nonce) {
    this.nonce = new AtomicLong(nonce < 0 ? 0 : nonce);
  }

  @Override
  public long getNonce() {
    return nonce.get();
  }

  @Override
  public long incrementAndGetNonce() {
    return nonce.incrementAndGet();
  }

}
