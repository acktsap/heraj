/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertNull;
import static hera.util.ValidationUtils.assertTrue;
import static java.lang.System.currentTimeMillis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestReporter {

  protected TestSuite currentTestSuite;

  protected TestCase currentTestCase;

  protected Map<String, TestSuite> testSuites = new LinkedHashMap<>();

  /**
   * Start test suite.
   *
   * @param suiteName test suite name
   */
  public void startSuite(final String suiteName) {
    assertNull(this.currentTestSuite);
    assertNotNull(suiteName);
    currentTestSuite = new TestSuite();
    currentTestSuite.setName(suiteName);
    testSuites.put(suiteName, currentTestSuite);
  }

  /**
   * End test suite.
   *
   * @param suiteName test suite name
   */
  public void endSuite(final String suiteName) {
    assertNotNull(this.currentTestSuite);
    assertTrue(this.currentTestSuite.getName().equals(suiteName));
    this.currentTestSuite = null;
  }

  /**
   * Start test case.
   *
   * @param testCaseName test case name
   */
  public void start(final String testCaseName) {
    assertNull(this.currentTestCase);
    assertNotNull(testCaseName);
    currentTestCase = new TestCase(testCaseName);
    this.currentTestSuite.addTestCase(currentTestCase);
  }

  /**
   * Catch error from test case.
   *
   * @param testCaseName test case name
   *
   * @param error error message
   */
  public void error(final String testCaseName, final String error) {
    assertNotNull(currentTestCase);
    assertTrue(currentTestCase.getName().equals(testCaseName));
    currentTestCase.setErrorMessage(error);
  }

  /**
   * End test case.
   *
   * @param testCaseName test case name
   *
   * @param success test case result
   */
  public void end(final String testCaseName, final boolean success) {
    assertNotNull(currentTestCase);
    assertTrue(currentTestCase.getName().equals(testCaseName));
    currentTestCase.setSuccess(success);
    currentTestCase.setEndTime(currentTimeMillis());
    this.currentTestCase = null;
  }

  /**
   * Result test suites.
   *
   * @return test suites
   */
  public Collection<TestSuite> getResults() {
    return testSuites.values();
  }

}