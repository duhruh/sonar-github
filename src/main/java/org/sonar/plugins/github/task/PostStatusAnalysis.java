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
import org.sonar.api.ce.posttask.QualityGate;
import org.sonar.core.config.ScannerProperties;
import org.sonar.plugins.github.DashboardHelper;
import org.sonar.plugins.github.GitHubPlugin;
import org.sonar.plugins.github.GitHubService;

import java.io.IOException;

public class PostStatusAnalysis implements PostProjectAnalysisTask {
    private static final String CONTEXT = "sonarqube";

    private final DashboardHelper dashboardHelper;

    public PostStatusAnalysis(DashboardHelper dashboardHelper){
        this.dashboardHelper = dashboardHelper;
    }

    @Override
    public void finished(ProjectAnalysis analysis) {
        if (!analysis.getScannerContext().getProperties().get(GitHubPlugin.GITHUB_ENABLED).equals("true")){
            return; // skipping
        }

        GitHubService service = null;
        try {
            service = new GitHubService(analysis.getScannerContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            service.setRepository(analysis.getScannerContext().getProperties().get(GitHubPlugin.GITHUB_REPO));
            service.setRef(analysis.getScannerContext().getProperties().get(ScannerProperties.BRANCH_NAME));

            GHCommitState state = GHCommitState.ERROR;
            String message = "SonarQube analysis error";

            QualityGate gate = analysis.getQualityGate();

            if (gate != null) {
                switch (gate.getStatus()) {
                    case OK:
                        state = GHCommitState.SUCCESS;
                        message = "SonarQube analysis succeeded";
                        break;
                    case WARN:
                        state = GHCommitState.FAILURE;
                        message = "SonarQube analysis found some warnings";
                        break;
                    case ERROR:
                        state = GHCommitState.FAILURE;
                        message = "SonarQube analysis failed";
                        break;
                }
            }

            service.createSonarQubeStatus(state, message, dashboardHelper.getReportURL(analysis), CONTEXT);

        } catch (IOException e) {
            service.createSonarQubeStatus(GHCommitState.FAILURE, "SonarQube analysis failed", dashboardHelper.getReportURL(analysis),CONTEXT);
        }
    }
}
