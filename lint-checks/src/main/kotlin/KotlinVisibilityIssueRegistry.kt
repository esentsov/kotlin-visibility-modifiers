package io.github.esentsov.kotlinvisibility

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

/*
 * Provides checks for usage of members annotated with FilePrivate or PackagePrivate.
 */
class KotlinVisibilityIssueRegistry : IssueRegistry() {
    override val api: Int = CURRENT_API
    override val minApi: Int = 5
    override val issues: List<Issue> = listOf(PrivateMembersUsageDetector.FilePrivateIssue, PrivateMembersUsageDetector.PackagePrivateIssue)
}

