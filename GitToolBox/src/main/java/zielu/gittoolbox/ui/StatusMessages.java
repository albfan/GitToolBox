package zielu.gittoolbox.ui;

import static zielu.gittoolbox.status.Status.CANCEL;
import static zielu.gittoolbox.status.Status.FAILURE;
import static zielu.gittoolbox.status.Status.NO_REMOTE;
import static zielu.gittoolbox.status.Status.SUCCESS;

import com.google.common.collect.Iterables;
import com.intellij.openapi.components.ServiceManager;
import git4idea.repo.GitRepository;
import git4idea.util.GitUIUtil;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import jodd.util.StringBand;
import zielu.gittoolbox.ResBundle;
import zielu.gittoolbox.config.GitToolBoxConfig;
import zielu.gittoolbox.status.GitAheadBehindCount;
import zielu.gittoolbox.status.RevListCount;
import zielu.gittoolbox.status.Status;
import zielu.gittoolbox.util.GtUtil;
import zielu.gittoolbox.util.Html;

public class StatusMessages {
  private final EnumMap<Status, String> commonStatuses = new EnumMap<>(Status.class);

  public StatusMessages() {
    commonStatuses.put(CANCEL, ResBundle.getString("message.cancelled"));
    commonStatuses.put(FAILURE, ResBundle.getString("message.failure"));
    commonStatuses.put(NO_REMOTE, ResBundle.getString("message.no.remote"));
  }

  public static StatusMessages getInstance() {
    return ServiceManager.getService(StatusMessages.class);
  }

  private StatusPresenter presenter() {
    return GitToolBoxConfig.getInstance().getPresenter();
  }

  public String behindStatus(RevListCount behindCount) {
    if (SUCCESS == behindCount.status()) {
      if (behindCount.value() > 0) {
        return presenter().behindStatus(behindCount.value());
      } else {
        return ResBundle.getString("message.up.to.date");
      }
    } else {
      return commonStatus(behindCount.status());
    }
  }

  public String aheadBehindStatus(GitAheadBehindCount count) {
    if (SUCCESS == count.status()) {
      if (count.isNotZero()) {
        return presenter().aheadBehindStatus(count.ahead.value(), count.behind.value());
      } else {
        return ResBundle.getString("message.up.to.date");
      }
    } else {
      return commonStatus(count.status());
    }
  }

  private String commonStatus(Status status) {
    return commonStatuses.getOrDefault(status, ResBundle.getString("message.unknown"));
  }

  private String repoNamePrefix(GitRepository repository) {
    return GitUIUtil.code(GtUtil.name(repository) + ": ");
  }

  private StringBand prepareSingleLineMessage(GitRepository repository, RevListCount status, boolean forceRepoName) {
    StringBand message = new StringBand();
    if (forceRepoName) {
      message.append(repoNamePrefix(repository));
    }
    message.append(behindStatus(status));
    return message;
  }

  private StringBand prepareMultiLineMessage(Map<GitRepository, RevListCount> statuses) {
    StringBand result = new StringBand();
    boolean first = true;
    for (Entry<GitRepository, RevListCount> status : statuses.entrySet()) {
      if (!first) {
        result.append(Html.BR);
      } else {
        first = false;
      }
      result.append(repoNamePrefix(status.getKey()))
          .append(behindStatus(status.getValue()));
    }
    return result;
  }

  public String prepareBehindMessage(Map<GitRepository, RevListCount> statuses) {
    return prepareBehindMessage(statuses, false);
  }

  public String prepareBehindMessage(Map<GitRepository, RevListCount> statuses, boolean forceRepoNames) {
    StringBand message = new StringBand();
    if (statuses.size() == 1) {
      Entry<GitRepository, RevListCount> entry = Iterables.getOnlyElement(statuses.entrySet());
      message.append(prepareSingleLineMessage(entry.getKey(), entry.getValue(), forceRepoNames));
    } else {
      message.append(prepareMultiLineMessage(statuses));
    }
    return message.toString();
  }
}
