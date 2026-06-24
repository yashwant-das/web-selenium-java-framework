package com.qaframework.listeners;

import com.qaframework.retry.RetryAnalyzer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * Annotation transformer listener that wires the {@link RetryAnalyzer} to all test methods
 * automatically at runtime.
 *
 * <p>This avoids having to manually annotate every single test method with retry.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class RetryAnalyzerListener implements IAnnotationTransformer {

  @Override
  @SuppressWarnings("rawtypes")
  public void transform(
      ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
    annotation.setRetryAnalyzer(RetryAnalyzer.class);
  }
}
