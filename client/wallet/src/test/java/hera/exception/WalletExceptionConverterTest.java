/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import org.junit.Test;

public class WalletExceptionConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final Throwable[] parameters = {
        new HerajException(""),
        new UnsupportedOperationException()
    };
    final Class<?>[] expected = {
        HerajException.class,
        HerajException.class
    };

    final WalletExceptionConverter converter = new WalletExceptionConverter();
    for (int i = 0; i < parameters.length; ++i) {
      final Throwable parameter = parameters[i];
      final Class<?> expectedClass = expected[i];
      final HerajException actual = converter.convert(parameter);
      assertEquals(actual.getClass(), expectedClass);
    }
  }

}
