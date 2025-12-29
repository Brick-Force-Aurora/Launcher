package de.brickforceaurora.launcher.updater.github;

import java.net.HttpURLConnection;

import de.brickforceaurora.launcher.updater.UpdaterConfig;
import me.lauriichan.snowframe.util.http.IHttpAuthenticator;

final class GithubAuthenticator implements IHttpAuthenticator {

    private final UpdaterConfig config;

    public GithubAuthenticator(final UpdaterConfig config) {
        this.config = config;
    }

    @Override
    public void authenticate(HttpURLConnection connection) {
        if (config.githubAuthToken() == null || config.githubAuthToken().isBlank()) {
            return;
        }
        connection.setRequestProperty("Authorization", "Bearer " + config.githubAuthToken());
    }

}
