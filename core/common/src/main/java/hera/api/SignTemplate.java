/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.KeyPair;
import hera.api.model.BytesValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class SignTemplate implements SignOperation {

  @Override
  public BytesValue sign(
      final KeyPair key,
      final Supplier<InputStream> plainText) throws IOException {
    try (final InputStream in = plainText.get()) {
      return new BytesValue(key.sign(in));
    }
  }

  @Override
  public boolean verify(
      final KeyPair key,
      final Supplier<InputStream> plainText,
      final BytesValue signature) throws IOException {
    try (final InputStream in = plainText.get()) {
      return key.verify(in, signature.getValue());
    }
  }

}
