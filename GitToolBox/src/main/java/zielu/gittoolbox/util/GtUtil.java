package zielu.gittoolbox.util;

import com.intellij.dvcs.DvcsUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.impl.HashImpl;
import git4idea.GitUtil;
import git4idea.GitVcs;
import git4idea.config.GitVcsSettings;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.CalledInAwt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum GtUtil {
    ;

    public static String name(@NotNull GitRepository repository) {
        return DvcsUtil.getShortRepositoryName(repository);
    }

    public static GitVcs vcs(@NotNull GitRepository repository) {
        return (GitVcs) repository.getVcs();
    }

    public static GitLock lock(@NotNull GitRepository repository) {
        return new GitLock(vcs(repository));
    }

    public static Hash hash(String hash) {
        return HashImpl.build(hash);
    }

    public static boolean hasRemotes(@NotNull GitRepository repository) {
        return !repository.getRemotes().isEmpty();
    }

    public static List<GitRepository> sort(Collection<GitRepository> repositories) {
        return DvcsUtil.sortRepositories(new ArrayList<>(repositories));
    }

    @Nullable
    @CalledInAwt
    public static GitRepository getCurrentRepositoryQuick(@NotNull Project myProject) {
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(myProject);
        return DvcsUtil.guessCurrentRepositoryQuick(myProject, repositoryManager, GitVcsSettings.getInstance(myProject).getRecentRootPath());
    }

    public static boolean isNotDumb(Project project) {
        return !DumbService.isDumb(project);
    }
}
