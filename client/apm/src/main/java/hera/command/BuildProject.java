/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import hera.Builder;
import hera.FileContent;
import hera.FileSet;
import hera.ProjectFile;
import hera.build.res.Project;
import hera.exception.NoBuildTargetException;
import lombok.Getter;
import lombok.Setter;

public class BuildProject extends AbstractCommand {

  @Getter
  @Setter
  protected FileContent output;

  @Override
  public void execute() throws Exception {
    logger.trace("Starting {}...", this);

    final ProjectFile projectFile = readProject();
    final Project project = new Project(".", projectFile);
    final Builder builder = new BuilderFactory().create(project);
    final String buildTarget = projectFile.getTarget();
    if (null == buildTarget) {
      throw new NoBuildTargetException();
    }
    final FileSet fileSet = builder.build(buildTarget);
    logger.info("Fileset: {}", fileSet);
    fileSet.copyTo(project.getPath());
  }
}
