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

import org.kohsuke.github.GHCommitState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.posttask.ScannerContext;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.FileNotFoundException;
import java.io.IOException;


@ScannerSide
@ComputeEngineSide
public class GitHubService {
    private static final Logger LOG = Loggers.get(GitHubService.class);

    private static final String PR_PREFIX = "PR-";
    private GitHub github;

    private GHRepository repository = null;

    private String sha1;

    private String ref;

    public GitHubService(Configuration config) throws IOException {
        this(config.get(GitHubPlugin.GITHUB_ENDPOINT).orElse(""), config.get(GitHubPlugin.GITHUB_OAUTH).orElse(""));
    }


    public GitHubService(ScannerContext context) throws IOException {
        this(context.getProperties().get(GitHubPlugin.GITHUB_ENDPOINT), context.getProperties().get(GitHubPlugin.GITHUB_OAUTH));
    }

    public GitHubService(String endpoint, String oauth) throws IOException {
        this.github = new GitHubBuilder()
                .withEndpoint(endpoint)
                .withOAuthToken(oauth)
                .build();
    }

    public void setRepository(String repository) throws IOException {
        this.repository = github.getRepository(repository);
    }

    public boolean isPR(){
        return ref.startsWith(PR_PREFIX);
    }

    public void setRef(String ref){
        if (this.repository == null) {
            throw new IllegalStateException("repository not set");
        }

        this.ref = ref;

        try{
            if (!isPR()){
                throw new Exception("not a pr");
            }

            String prStr = ref.substring(PR_PREFIX.length());
            int pr = Integer.parseInt(prStr);
            this.sha1 = repository.getPullRequest(pr).getHead().getSha();
        }catch (Exception e){
            try {
                this.sha1 = repository.getBranch(ref).getSHA1();
            } catch (IOException ex) {
                throw new IllegalStateException("unknown reference:"+ref, e);
            }
        }
    }

    public void createSonarQubeStatus(
            GHCommitState status,
            String statusDescription,
            String targetUrl,
            String context) {

        try {
            repository.createCommitStatus(sha1, status, targetUrl, statusDescription, context);
        } catch (FileNotFoundException e) {
            String msg = "Unable to set pull request status.";
            if (LOG.isDebugEnabled()) {
                LOG.warn(msg, e);
            } else {
                LOG.warn(msg);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to update commit status", e);
        }
    }
}
