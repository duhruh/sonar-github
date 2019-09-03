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
package org.sonar.plugins.github.sensor;

import org.kohsuke.github.GHCommitState;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.core.config.ScannerProperties;
import org.sonar.plugins.github.ContextCopy;
import org.sonar.plugins.github.DashboardHelper;
import org.sonar.plugins.github.GitHubPlugin;
import org.sonar.plugins.github.GitHubService;

import java.io.IOException;

public class StatusSensor implements Sensor {

    private final GitHubService service;

    private final DashboardHelper dashboardHelper;

    public StatusSensor(GitHubService service, DashboardHelper dashboardHelper){
        this.service = service;
        this.dashboardHelper = dashboardHelper;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("GitHub Status Sensor");
    }

    @Override
    public void execute(SensorContext context) {
        ContextCopy.copyIntoContext(context);
        if (!context.config().get(GitHubPlugin.GITHUB_ENABLED).orElse("false").equals("true")) {
            return; // skipping sensor
        }
        try {
            service.setRepository(context.config().get(GitHubPlugin.GITHUB_REPO).orElse(""));
            service.setRef(context.config().get(ScannerProperties.BRANCH_NAME).orElse(""));
            service.createSonarQubeStatus(GHCommitState.PENDING, "SonarQube analysis in progress", dashboardHelper.getReportURL(context.config()),"sonarqube");
        } catch (IOException e) {
            service.createSonarQubeStatus(GHCommitState.FAILURE, "SonarQube analysis failed", dashboardHelper.getReportURL(context.config()),"sonarqube");
        }
    }
}
