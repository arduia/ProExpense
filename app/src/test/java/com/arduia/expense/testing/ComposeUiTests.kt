package com.arduia.expense.testing

/**
 * Marks Robolectric Compose UI tests that launch a real activity (`createAndroidComposeRule`,
 * `ActivityScenarioRule`) — these need `compose-ui-test-manifest`, which is `debugImplementation`
 * only, so they fail to resolve an activity under a release unit-test variant. Excluded alongside
 * [ScreenshotTests] from `*ReleaseUnitTest` (see `app/build.gradle.kts`).
 */
interface ComposeUiTests
