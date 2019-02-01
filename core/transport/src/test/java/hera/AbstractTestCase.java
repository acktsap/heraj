/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.SimpleTimeoutStrategy;
import java.util.concurrent.Executors;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({
    "javax.crypto.*",
    "javax.management.*",
    "javax.net.ssl.*",
    "javax.security.*",
    "org.bouncycastle.*"})
public abstract class AbstractTestCase {

  protected final transient Logger logger = getLogger(getClass());

  protected final ListeningExecutorService service =
      MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

  protected Context context;

  @Before
  public void setUp() {
    this.context = ContextProvider.defaultProvider.get()
        .withStrategy(new SimpleTimeoutStrategy(10000L))
        .withStrategy(new NettyConnectStrategy());
  }

}