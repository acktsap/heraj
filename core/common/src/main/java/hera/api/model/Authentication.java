/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.HexUtils;
import hera.util.Sha256Utils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder(builderMethodName = "newBuilder")
public class Authentication {

  public static Authentication of(final Identity identity, final String password) {
    return new Authentication(identity, password);
  }

  @Getter
  @NonNull
  protected final Identity identity;

  @Getter
  @NonNull
  protected final String password;

  @Override
  public String toString() {
    return String.format("Authentication(identity=%s, password=%s)", identity.getValue(),
        HexUtils.encode(Sha256Utils.digest(password.getBytes())));
  }

}
