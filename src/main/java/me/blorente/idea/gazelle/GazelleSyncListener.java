package me.blorente.idea.gazelle;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.idea.blaze.base.async.FutureUtil;
import com.google.idea.blaze.base.model.primitives.Label;
import com.google.idea.blaze.base.model.primitives.WorkspaceRoot;
import com.google.idea.blaze.base.projectview.ProjectViewManager;
import com.google.idea.blaze.base.projectview.ProjectViewSet;
import com.google.idea.blaze.base.projectview.section.sections.DirectoryEntry;
import com.google.idea.blaze.base.projectview.section.sections.DirectorySection;
import com.google.idea.blaze.base.scope.BlazeContext;
import com.google.idea.blaze.base.scope.scopes.TimingScope;
import com.google.idea.blaze.base.settings.BlazeImportSettings;
import com.google.idea.blaze.base.settings.BlazeImportSettingsManager;
import com.google.idea.blaze.base.sync.SyncListener;
import com.google.idea.blaze.base.sync.SyncMode;
import com.google.idea.blaze.base.sync.SyncScope;
import com.intellij.openapi.project.Project;

import java.util.List;
import java.util.Optional;

public class GazelleSyncListener implements SyncListener {

    public void onSyncStart(Project project, BlazeContext context, SyncMode syncMode)
            throws SyncScope.SyncFailedException, SyncScope.SyncCanceledException {
        if (syncMode == SyncMode.NO_BUILD) {
            return;
        }
        ProjectViewSet projectViewSet = ProjectViewManager.getInstance(project).getProjectViewSet();
        Optional<Label> gazelleBinary = projectViewSet.getScalarValue(GazelleSection.KEY);
        List<DirectoryEntry> importantDirectories = projectViewSet.listItems(DirectorySection.KEY);
        if (gazelleBinary.isPresent()) {
            BlazeImportSettings importSettings = BlazeImportSettingsManager.getInstance(project).getImportSettings();
            WorkspaceRoot workspaceRoot = WorkspaceRoot.fromImportSettings(importSettings);
            String binaryPath = "bazel";

            ListenableFuture<String> blazeGazelleFuture = BlazeGazelleRunner.getInstance().runBlazeGazelle(
                    context,
                    binaryPath,
                    workspaceRoot,
                    ImmutableList.of(),
                    gazelleBinary.get(),
                    importantDirectories);
            FutureUtil.waitForFuture(context, blazeGazelleFuture)
                    .withProgressMessage("Running Gazelle...")
                    .timed("GazelleRun", TimingScope.EventType.BlazeInvocation)
                    .onError("Gazelle failed")
                    .run();
        }
    }
}
