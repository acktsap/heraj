/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

import hera.exception.HerajException;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ContractInterface {

  @Getter
  protected final ContractAddress address;

  @Getter
  protected final String version;

  @Getter
  protected final String language;

  @Getter
  protected final List<ContractFunction> functions;

  /**
   * ContractInterface constructor.
   * 
   * @param contractAddress a contract address
   * @param version a contract version
   * @param language a contract language
   * @param functions a contract functions to invoke
   */
  public ContractInterface(final ContractAddress contractAddress, final String version,
      final String language, final List<ContractFunction> functions) {
    assertNotNull(contractAddress, new HerajException("Contract address must not null"));
    assertNotNull(version, new HerajException("Version must not null"));
    assertNotNull(language, new HerajException("Language must not null"));
    assertNotNull(functions, new HerajException("Functions must not null"));
    // TODO : contractAddress is mutable
    this.address = contractAddress;
    this.version = version;
    this.language = language;
    this.functions = unmodifiableList(functions);
  }

  /**
   * Find a contract function with the given function name.
   *
   * @param functionName function name to find
   * @return {@code ContractFunction} if found. Otherwise, null
   */
  public ContractFunction findFunction(final String functionName) {
    ContractFunction foundFunction = null;
    for (final ContractFunction contractFunction : functions) {
      if (contractFunction.getName().equals(functionName)) {
        foundFunction = contractFunction;
        break;
      }
    }
    return foundFunction;
  }

  public ContractInvocationWithNothing newInvocationBuilder() {
    return new ContractInterface.InvocationBuilder(this);
  }

  public interface ContractInvocationWithNothing {
    ContractInvocationWithFunction function(String functionName);
  }

  public interface ContractInvocationWithFunction extends hera.util.Builder<ContractInvocation> {
    ContractInvocationWithFunctionAndArgs args(Object... args);
  }

  public interface ContractInvocationWithFunctionAndArgs
      extends hera.util.Builder<ContractInvocation> {
  }

  @RequiredArgsConstructor
  protected static class InvocationBuilder implements ContractInvocationWithNothing,
      ContractInvocationWithFunction, ContractInvocationWithFunctionAndArgs {

    @NonNull
    protected final ContractInterface contractInterface;

    protected ContractFunction function;

    protected Object[] args;

    @Override
    public ContractInvocationWithFunction function(final String functionName) {
      this.function = contractInterface.findFunction(functionName);
      return this;
    }

    @Override
    public ContractInvocationWithFunctionAndArgs args(final Object... args) {
      this.args = args;
      return this;
    }

    @Override
    public hera.api.model.ContractInvocation build() {
      return new ContractInvocation(contractInterface.getAddress(), function, args);
    }
  }

}
