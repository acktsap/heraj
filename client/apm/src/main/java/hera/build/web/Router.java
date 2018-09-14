/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web;

import static hera.util.ExceptionUtils.getStackTraceOf;
import static org.slf4j.LoggerFactory.getLogger;

import hera.build.web.exception.HttpException;
import hera.build.web.exception.ResourceNotFoundException;
import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.build.web.model.ContractInput;
import hera.build.web.model.DeploymentResult;
import hera.build.web.model.ExecutionResult;
import hera.build.web.model.QueryResult;
import hera.build.web.model.ServiceError;
import hera.build.web.service.BuildService;
import hera.build.web.service.ContractService;
import hera.build.web.service.LiveUpdateService;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@ControllerAdvice
public class Router {

  protected final transient Logger logger = getLogger(getClass());

  @Inject
  protected BuildService buildService;

  @Inject
  protected ContractService contractService;

  @Inject
  protected LiveUpdateService liveUpdateService;

  @PostConstruct
  public void initialize() {
    buildService.addListener(liveUpdateService::notifyChange);
  }

  @GetMapping("contract")
  public DeploymentResult getLatestContract() {
    return contractService.getLatestContractInformation();
  }

  @GetMapping("builds")
  public List<BuildSummary> getLatestBuilds() {
    return buildService.list(null, 5);
  }

  @GetMapping("build/{uuid}")
  public BuildDetails getBuild(@PathVariable("uuid") final String buildUuid) {
    return buildService.get(buildUuid).orElseThrow(ResourceNotFoundException::new);
  }

  /**
   * Deploy result of build with {@code buildUuid}.
   *
   * @param buildUuid build uuid
   *
   * @return deployment result
   *
   * @throws Exception Fail to deploy
   */
  @PostMapping("build/{uuid}/deploy")
  public DeploymentResult deploy(@PathVariable("uuid") final String buildUuid) throws Exception {
    final BuildDetails buildDetails = buildService.get(buildUuid)
        .orElseThrow(() -> new ResourceNotFoundException(buildUuid + " not found"));
    return contractService.deploy(buildDetails);
  }

  @PostMapping(value = "contract/{tx}/{function}")
  public ExecutionResult execute(
      @PathVariable("tx") final String contractTransactionHash,
      @PathVariable("function") final String functionName,
      @RequestBody final ContractInput contractInput) throws IOException {
    final String[] arguments = contractInput.getArguments();
    return contractService.execute(contractTransactionHash, functionName, arguments);
  }

  /**
   * Query contract.
   *
   * @param contractTransactionHash transaction hash
   * @param functionName            function'name to call
   * @param arguments               argument for function
   *
   * @return function's result
   *
   * @throws IOException Fail to invoke function
   */
  @GetMapping(value = "contract/{tx}/{function}")
  public QueryResult query(
      @PathVariable("tx") final String contractTransactionHash,
      @PathVariable("function") final String functionName,
      @RequestParam(value = "arguments", required = false) final String[] arguments
  ) throws IOException {
    logger.trace("Transaction Hash: {}, Function: {}, Arguments: {}",
        contractTransactionHash, functionName, arguments);
    if (null == arguments) {
      return contractService.query(contractTransactionHash, functionName, new String[0]);
    } else {
      return contractService.query(contractTransactionHash, functionName, arguments);
    }
  }

  @ExceptionHandler(value = { HttpException.class })
  protected ResponseEntity handleHttpException(HttpException ex, WebRequest request) {
    final ServiceError serviceError = new ServiceError(ex.getMessage(), getStackTraceOf(ex));
    return ResponseEntity.status(ex.getStatusCode()).body(serviceError);
  }

  @ExceptionHandler(value = { Throwable.class })
  @ResponseStatus()
  @ResponseBody
  protected Object handleThrowable(Throwable ex, WebRequest request) {
    return new ServiceError(ex.getMessage(), getStackTraceOf(ex));
  }
}
