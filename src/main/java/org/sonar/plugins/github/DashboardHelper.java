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

import org.sonar.api.batch.fs.internal.InputModuleHierarchy;
import org.sonar.api.config.Configuration;
import org.sonar.api.platform.Server;


public class DashboardHelper {
    private static final String DASHBOARD = "dashboard";
    private static final String BRANCH = "branch";
    private static final String ID = "id";

    private final InputModuleHierarchy moduleHierarchy;
    private final Server server;
    private final Configuration settings;

    public DashboardHelper(Server server, InputModuleHierarchy moduleHierarchy, Configuration settings) {
        this.server = server;
        this.moduleHierarchy = moduleHierarchy;
        this.settings = settings;

    }

    public String getReportURL() {
        String effectiveKey = moduleHierarchy.root().getKeyWithBranch(); // project name
        return String.format("%s/%s?%s=%s&%s=%s", server.getPublicRootUrl(), DASHBOARD, ID, effectiveKey, BRANCH, "PR-"+settings.get(GitHubPlugin.GITHUB_PULL_REQUEST).orElse(""));
    }
}
