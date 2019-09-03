/*
 * SonarQube :: GitHub Plugin
 * Copyright (C) 2015-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.github;

import org.sonar.api.CoreProperties;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.plugins.github.sensor.StatusSensor;
import org.sonar.plugins.github.task.PostStatusAnalysis;

@Properties({
        @Property(
                key = GitHubPlugin.GITHUB_ENABLED,
                name = "GitHub Status Enabled",
                description = "If set to true will update pr status",
                project = true,
                global = true,
                defaultValue = "false",
                type = PropertyType.BOOLEAN),
        @Property(
                key = GitHubPlugin.GITHUB_ENDPOINT,
                defaultValue = "https://api.github.com",
                name = "GitHub API Endpoint",
                description = "URL to access GitHub WS API. Default value is fine for public GitHub. Can be modified for GitHub enterprise.",
                global = true),
        @Property(
                key = GitHubPlugin.GITHUB_OAUTH,
                name = "GitHub OAuth token",
                description = "Authentication token",
                project = true,
                global = true,
                type = PropertyType.PASSWORD),
        @Property(
                key = GitHubPlugin.GITHUB_REPO,
                name = "GitHub repository",
                description = "GitHub repository for this project. Will be guessed from '" + CoreProperties.LINKS_SOURCES_DEV + "' if present",
                project = false,
                global = false),
        @Property(
                key = GitHubPlugin.GITHUB_DISABLE_INLINE_COMMENTS,
                defaultValue = "false",
                name = "Disable issue reporting as inline comments",
                description = "Issues will not be reported as inline comments but only in the global summary comment",
                project = true,
                global = true,
                type = PropertyType.BOOLEAN)
})
public class GitHubPlugin implements Plugin {

  public static final String GITHUB_ENDPOINT = "sonar.github.endpoint";
  public static final String GITHUB_OAUTH = "sonar.github.oauth";
  public static final String GITHUB_REPO = "sonar.github.repository";
  public static final String GITHUB_ENABLED = "sonar.github.enabled";
  public static final String GITHUB_DISABLE_INLINE_COMMENTS = "sonar.github.disableInlineComments";

  @Override
  public void define(Context context) {
    context.addExtensions(
            PostStatusAnalysis.class,
            StatusSensor.class,
            GitHubService.class,
            DashboardHelper.class);
  }

}
