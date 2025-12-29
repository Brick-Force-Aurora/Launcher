package de.brickforceaurora.launcher.updater.github;

import java.io.IOException;

import de.brickforceaurora.launcher.updater.IUpdate;
import de.brickforceaurora.launcher.updater.IUpdater;
import de.brickforceaurora.launcher.updater.UpdaterConfig;
import de.brickforceaurora.launcher.util.IOUtil;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.JsonArray;
import me.lauriichan.laylib.json.JsonObject;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;
import me.lauriichan.snowframe.util.http.HttpCode;
import me.lauriichan.snowframe.util.http.HttpRequest;
import me.lauriichan.snowframe.util.http.HttpResponse;
import me.lauriichan.snowframe.util.http.type.HttpContentType;

public final class GithubUpdater implements IUpdater {

    private static final String GITHUB_RELEASE = "https://api.github.com/repos/%s/releases/tags/%s";
    private static final String GITHUB_TAGS = "https://api.github.com/repos/%s/tags";

    @Override
    public void checkForUpdate(UpdaterConfig config, ISimpleLogger logger, Version current, ObjectList<IUpdate> updates)
        throws IOException {
        GithubAuthenticator authenticator = new GithubAuthenticator(config);
        boolean allowPreReleases = config.experimental();

        String repository = config.githubRepository();
        String releaseUrl = GITHUB_RELEASE.formatted(repository, "%s");
        String tagsUrl = GITHUB_TAGS.formatted(repository);

        HttpRequest request = new HttpRequest().url(tagsUrl).authenticator(authenticator);
        HttpRequest assetRequest = new HttpRequest().authenticator(authenticator);

        int page = 1;
        loop:
        while (true) {
            HttpResponse<IJson<?>> response = callGithub(
                request.clearParameters().readTimeout(7500).param("per_page", 40).param("page", page++), HttpContentType.JSON);
            if (response == null || response.code() == HttpCode.NOT_FOUND) {
                break;
            }
            logger.debug("Code: {0} ({1})", response.code().name(), response.code().code());
            if (logger.isDebug()) {
                logger.debug(IOUtil.asString(response.data().value()));
            }
            JsonArray array = response.data().value().asJsonArray();
            if (array.isEmpty()) {
                break;
            }
            for (IJson<?> arrayValue : array) {
                if (!arrayValue.isObject()) {
                    continue;
                }
                JsonObject object = arrayValue.asJsonObject();
                String tagName = object.getAsString("name");
                if (tagName == null) {
                    continue;
                }
                Version version = Version.parse(tagName);
                if (current.isHigher(version)) {
                    break loop;
                }
                HttpResponse<IJson<?>> assetResponse = callGithub(assetRequest.url(releaseUrl.formatted(tagName)), HttpContentType.JSON);
                if (assetResponse == null || assetResponse.code() == HttpCode.NOT_FOUND) {
                    continue;
                }
                IJson<?> releaseValue = assetResponse.data().value();
                if (!releaseValue.isObject()) {
                    continue;
                }
                JsonObject releaseObject = releaseValue.asJsonObject();
                if (releaseObject.getAsBoolean("draft") || (!allowPreReleases && releaseObject.getAsBoolean("prerelease"))) {
                    continue;
                }
                for (IJson<?> value : releaseObject.getAsArray("assets")) {
                    if (!value.isObject()) {
                        continue;
                    }
                    JsonObject assetObject = value.asJsonObject();
                    String assetName = assetObject.getAsString("name");
                    if (assetName != null && assetName.endsWith(".zip")) {
                        String downloadUrl = assetObject.getAsString("browser_download_url");
                        if (downloadUrl == null || downloadUrl.isBlank()) {
                            continue;
                        }
                        updates.add(new GithubUpdate(authenticator, downloadUrl, version));
                        break;
                    }
                }
            }
            // We can stop early if the array has less than 40 entries
            if (array.size() < 40) {
                break;
            }
        }
    }

    static <T> HttpResponse<T> callGithub(HttpRequest request, HttpContentType<T> responseType) throws IOException {
        HttpResponse<T> response = request.call(responseType);
        if (response.code() == HttpCode.UNAUTHORIZED) {
            throw new IOException("Invalid github authentication token");
        }
        if (response.code() == HttpCode.FORBIDDEN || response.code() == HttpCode.TOO_MANY_REQUESTS) {
            throw new IOException("We hit a rate limit");
        }
        return response;
    }

}
