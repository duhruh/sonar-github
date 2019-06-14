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
package org.sonar.plugins.github.task;

import org.kohsuke.github.GHCommitState;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.core.config.ScannerProperties;
import org.sonar.plugins.github.DashboardHelper;
import org.sonar.plugins.github.GitHubPlugin;
import org.sonar.plugins.github.GitHubService;

import java.io.IOException;

public class PostStatusAnalysis implements PostProjectAnalysisTask {

    private final DashboardHelper dashboardHelper;

    public PostStatusAnalysis(DashboardHelper dashboardHelper){
        this.dashboardHelper = dashboardHelper;
    }

    @Override
    public void finished(ProjectAnalysis analysis) {
        GitHubService service = null;
        try {
            service = new GitHubService(analysis.getScannerContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            service.setRepository(analysis.getScannerContext().getProperties().get(GitHubPlugin.GITHUB_REPO));
            service.setRef(analysis.getScannerContext().getProperties().get(ScannerProperties.BRANCH_NAME));
            service.createSonarQubeStatus(GHCommitState.PENDING, "SonarQube analysis in progress", dashboardHelper.getReportURL(analysis),"sonarqube");
        } catch (IOException e) {
            service.createSonarQubeStatus(GHCommitState.FAILURE, "SonarQube analysis failed", dashboardHelper.getReportURL(analysis),"sonarqube");
        }
    }
}
