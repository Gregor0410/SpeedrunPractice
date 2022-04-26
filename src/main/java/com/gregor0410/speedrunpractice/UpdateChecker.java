package com.gregor0410.speedrunpractice;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStreamReader;

public class UpdateChecker {
    private String versionName = null;
    private String changelogs = null;

    public void checkUpdate() {
        new Thread(() -> {
            try {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpGet request = new HttpGet("https://api.github.com/repos/gregor0410/speedrunpractice/releases/latest");
                JsonObject jsonObject = client.execute(request, res -> SpeedrunPractice.gson.fromJson(new InputStreamReader(res.getEntity().getContent()), JsonObject.class));
                String latestVersion = jsonObject.get("name").getAsString().substring(1); //get rid of the leading v
                String patchNotes = jsonObject.get("body").getAsString();

                this.versionName = latestVersion;
                this.changelogs = patchNotes;
            } catch (Exception e) {
                // Failed getting GitHub rest API
            }
        }).start();
    }

    public boolean isCheckedUpdate() {
        return versionName != null && changelogs != null;
    }

    public boolean isOutdatedVersion() {
        try {
            return isCheckedUpdate() && SpeedrunPractice.version.compareTo(new SemanticVersionImpl(this.getVersionName(),false))<0;
        } catch (VersionParsingException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getChangelogs() {
        return changelogs;
    }

    public String getVersionName() {
        return versionName;
    }
}
