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
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.posttask.Branch;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.config.Configuration;
import org.sonar.api.platform.Server;
import org.sonar.core.config.ScannerProperties;


@ScannerSide
@ComputeEngineSide
public class DashboardHelper {
    private static final String DASHBOARD = "dashboard";
    private static final String BRANCH = "branch";
    private static final String ID = "id";

    private final Server server;

    public DashboardHelper(Server server) {
        this.server = server;
    }

    public String getReportURL(PostProjectAnalysisTask.ProjectAnalysis analysis) {
        String effectiveKey = analysis.getProject().getName();
        Branch branch = analysis.getBranch().get();

        return getReportURL(effectiveKey, branch.getName().orElse(""));
    }

    public String getReportURL(Configuration config){

        String effectiveKey = config.get(CoreProperties.PROJECT_KEY_PROPERTY).orElse("");
        String branch = config.get(ScannerProperties.BRANCH_NAME).orElse("");

        return getReportURL(effectiveKey, branch);
    }

    public String getReportURL(String project, String branch){

        StringBuilder url = new StringBuilder();
        url.append(String.format("%s/%s?",server.getPublicRootUrl(), DASHBOARD));
        url.append(String.format("%s=%s", ID, project));
        url.append(String.format("&%s=%s", BRANCH, branch));

        return url.toString();
    }
}
