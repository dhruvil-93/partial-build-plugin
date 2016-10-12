package com.lesfurets.maven.partial.mocks;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

public class LocalRepoMock extends RepoMock {

    private static final File REPO = RepoTest.LOCAL_DIR.toFile();
    private final Git git;
    private RemoteRepoMock remoteRepo;

    public LocalRepoMock(boolean remote) throws Exception {
        try {
            delete(REPO);
        } catch (Exception ignored) {
        }
        boolean mkdirs = REPO.mkdirs();
        if (!mkdirs) {
            throw new Exception("Cannot create directory for git repository : " + REPO.toString());
        }
        copyMockRepoTo(getRepoDir());
        git = Git.wrap(initRepositoryIn(REPO));
        if (remote) {
            remoteRepo = new RemoteRepoMock(false);
            configureRemote(remoteRepo.repoUrl);
            git.fetch().call();
        }
    }

    public void configureRemote(String repoUrl) throws URISyntaxException, IOException, GitAPIException {
        StoredConfig config = git.getRepository().getConfig();
        config.clear();
        config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("remote", "origin", "push", "+refs/heads/*:refs/remotes/origin/*");
        config.setString("branch", "master", "remote", "origin");
        config.setString("baseBranch", "master", "merge", "refs/heads/master");
        config.setString("push", null, "default", "current");
        RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
        URIish uri = new URIish(repoUrl);
        remoteConfig.addURI(uri);
        remoteConfig.addFetchRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.addPushRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
        remoteConfig.update(config);
        config.save();
        git.fetch().call();
    }

    public RemoteRepoMock getRemoteRepo() {
        return remoteRepo;
    }

    @Override
    protected File getRepoDir() {
        return REPO;
    }

    public Git getGit() {
        return git;
    }

    public void close() throws Exception {
        if (remoteRepo != null) {
            remoteRepo.close();
        }
        super.close();
    }

}
