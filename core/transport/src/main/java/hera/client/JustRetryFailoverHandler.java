/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Invocation;
import hera.Response;
import hera.api.model.Time;
import hera.exception.HerajException;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
class JustRetryFailoverHandler extends ComparableFailoverHandler {

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final int priority = 2;

  protected final int count;

  protected final long interval; // milliseconds

  JustRetryFailoverHandler(final int count, final Time interval) {
    assertTrue(0 < count, "Retry count must be positive");
    this.count = count;
    this.interval = interval.toMilliseconds();
  }

  @Override
  public <T> Response<T> handle(final Invocation<T> invocation, final Response<T> response) {
    logger.debug("Handle {} with {}", response.getError(), this);

    Response<T> next = response;
    int countDown = this.count;
    while (null != next.getError() && 0 < countDown) {
      try {
        logger.debug("Just retry with {} after sleep {}ms (count left: {})", invocation,
            this.interval, countDown);
        Thread.sleep(this.interval);
      } catch (Exception e) {
        throw new HerajException("Unexpected error", e);
      }

      try {
        final T ret = invocation.invoke();
        next = Response.success(ret);
      } catch (Exception e) {
        next = Response.fail(e);
      }
      --countDown;
    }
    return next;
  }

}
