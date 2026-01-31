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
    
    private static final int ITEMS_PER_PAGE_COUNT = 40;

    @Override
    public void checkForUpdate(final UpdaterConfig config, final ISimpleLogger logger, final Version current,
        final ObjectList<IUpdate> updates) throws IOException {
        final GithubAuthenticator authenticator = new GithubAuthenticator(config);
        final boolean allowPreReleases = config.experimental();

        final String repository = config.githubRepository();
        final String releaseUrl = GITHUB_RELEASE.formatted(repository, "%s");
        final String tagsUrl = GITHUB_TAGS.formatted(repository);

        final HttpRequest request = new HttpRequest().url(tagsUrl).authenticator(authenticator);
        final HttpRequest assetRequest = new HttpRequest().authenticator(authenticator);

        int page = 1;
        loop:
        while (true) {
            final HttpResponse<IJson<?>> response = callGithub(
                request.clearParameters().readTimeout(5000).param("per_page", ITEMS_PER_PAGE_COUNT).param("page", page++), HttpContentType.JSON);
            if (response == null || response.code() == HttpCode.NOT_FOUND) {
                break;
            }
            if (logger.isDebug()) {
                logger.debug(IOUtil.asString(response.data().value()));
            }
            final JsonArray array = response.data().value().asJsonArray();
            if (array.isEmpty()) {
                break;
            }
            for (final IJson<?> arrayValue : array) {
                if (!arrayValue.isObject()) {
                    continue;
                }
                final JsonObject object = arrayValue.asJsonObject();
                final String tagName = object.getAsString("name");
                if (tagName == null) {
                    continue;
                }
                final Version version = Version.parse(tagName);
                if (current.compareTo(version) >= 0) {
                    if (isSamePatchline(current, version)) {
                        continue;
                    }
                    break loop;
                }
                final HttpResponse<IJson<?>> assetResponse = callGithub(assetRequest.url(releaseUrl.formatted(tagName)),
                    HttpContentType.JSON);
                if (assetResponse == null || assetResponse.code() == HttpCode.NOT_FOUND) {
                    continue;
                }
                final IJson<?> releaseValue = assetResponse.data().value();
                if (!releaseValue.isObject()) {
                    continue;
                }
                final JsonObject releaseObject = releaseValue.asJsonObject();
                if (releaseObject.getAsBoolean("draft") || !allowPreReleases && releaseObject.getAsBoolean("prerelease")) {
                    continue;
                }
                for (final IJson<?> value : releaseObject.getAsArray("assets")) {
                    if (!value.isObject()) {
                        continue;
                    }
                    final JsonObject assetObject = value.asJsonObject();
                    final String assetName = assetObject.getAsString("name");
                    if (assetName != null && assetName.endsWith(".zip")) {
                        final String downloadUrl = assetObject.getAsString("browser_download_url");
                        if (downloadUrl == null || downloadUrl.isBlank()) {
                            continue;
                        }
                        updates.add(new GithubUpdate(authenticator, downloadUrl, version));
                        break;
                    }
                }
            }
            // We can stop early if the array has less than 40 entries
            if (array.size() < ITEMS_PER_PAGE_COUNT) {
                break;
            }
        }
    }

    private static boolean isSamePatchline(Version a, Version b) {
        return a.major == b.major && a.minor == b.minor && a.patch == b.patch;
    }

    static <T> HttpResponse<T> callGithub(final HttpRequest request, final HttpContentType<T> responseType) throws IOException {
        final HttpResponse<T> response = request.call(responseType);
        if (response.code() == HttpCode.UNAUTHORIZED) {
            throw new IOException("Invalid github authentication token");
        }
        if (response.code() == HttpCode.FORBIDDEN || response.code() == HttpCode.TOO_MANY_REQUESTS) {
            throw new IOException("We hit a rate limit");
        }
        return response;
    }

}
