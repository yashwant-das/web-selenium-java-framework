package com.qaframework.assertions;

import org.assertj.core.api.SoftAssertions;

/**
 * Helper class wrapping AssertJ's {@link SoftAssertions} to accumulate multiple failures.
 *
 * <p>By extending {@link SoftAssertions}, this class exposes all fluent AssertJ assertions (such as
 * {@code assertThat}) while allowing us to capture multiple failures during a single test.
 *
 * @author QA Framework Team
 * @since 1.0
 */
public class SoftAssertionHelper extends SoftAssertions {}
