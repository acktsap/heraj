/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.build.Concatenator;
import hera.build.Resource;
import hera.build.ResourceManager;
import hera.build.res.BuildResource;
import hera.build.res.TestResource;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import lombok.Getter;
import org.slf4j.Logger;

public class Builder {

  protected final transient Logger logger = getLogger(getClass());

  @Getter
  protected final ResourceManager resourceManager;

  /**
   * Constructor with project file(aergo.json).
   *
   * @param resourceManager manager for resource
   *
   * @throws Exception Fail to initialize build topology
   */
  public Builder(final ResourceManager resourceManager) {
    this.resourceManager = resourceManager;
  }

  /**
   * Build {@link BuildResource} or {@link TestResource}.
   *
   * @param resourcePath path to resource
   *
   * @return Build result fileset
   */
  public FileSet build(final String resourcePath) {
    final Resource resource = resourceManager.getResource(resourcePath);
    logger.trace("{}: {}", resourcePath, resource);
    final Concatenator concatenator = new Concatenator(resourceManager);
    final Optional<BuildResource> buildResourceOpt = resource.adapt(BuildResource.class);
    final Optional<TestResource> testResourceOpt = resource.adapt(TestResource.class);
    if (buildResourceOpt.isPresent()) {
      final byte[] contents = concatenator.visit(buildResourceOpt.get());
      return new FileSet(asList(
          new FileContent(resourcePath, () -> new ByteArrayInputStream(contents))));
    } else if (testResourceOpt.isPresent()) {
      final byte[] contents = concatenator.visit(testResourceOpt.get());
      return new FileSet(asList(
          new FileContent(resourcePath, () -> new ByteArrayInputStream(contents))));
    } else {
      return new FileSet();
    }
  }
}
