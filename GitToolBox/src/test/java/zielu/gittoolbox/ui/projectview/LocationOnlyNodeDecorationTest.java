package zielu.gittoolbox.ui.projectview;

import com.intellij.ide.projectView.PresentationData;
import git4idea.repo.GitRepository;
import org.junit.jupiter.api.Tag;
import zielu.gittoolbox.cache.RepoInfo;
import zielu.gittoolbox.config.GitToolBoxConfig;

@Tag("fast")
class LocationOnlyNodeDecorationTest extends NodeDecorationBaseTest {
  @Override
  NodeDecoration createDecoration(GitToolBoxConfig config, GitRepository repository, RepoInfo repoInfo) {
    return new LocationOnlyNodeDecoration(config, repository, repoInfo);
  }

  @Override
  String getStatusText(PresentationData data) {
    return data.getLocationString();
  }
}
