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

import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.core.config.ScannerProperties;

public class ContextCopy {


    public static void copyIntoContext(SensorContext context){
        context.addContextProperty(GitHubPlugin.GITHUB_OAUTH, context.config().get(GitHubPlugin.GITHUB_OAUTH).orElse(""));
        context.addContextProperty(GitHubPlugin.GITHUB_ENDPOINT, context.config().get(GitHubPlugin.GITHUB_ENDPOINT).orElse(""));
        context.addContextProperty(GitHubPlugin.GITHUB_REPO, context.config().get(GitHubPlugin.GITHUB_REPO).orElse(""));
        context.addContextProperty(ScannerProperties.BRANCH_NAME, context.config().get(ScannerProperties.BRANCH_NAME).orElse(""));
    }
}
