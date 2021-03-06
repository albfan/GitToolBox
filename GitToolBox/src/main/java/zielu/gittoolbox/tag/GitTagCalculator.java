package zielu.gittoolbox.tag;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.commands.GitCommand;
import git4idea.commands.GitHandlerUtil;
import git4idea.commands.GitSimpleHandler;
import git4idea.util.StringScanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zielu.gittoolbox.ResBundle;

public class GitTagCalculator {
  private static final Pattern SINGLE_TAG_PATTERN = Pattern.compile(".*?\\(tag: (.+?)\\).*");
  private static final Pattern TAG_PATTERN = Pattern.compile("tag: (.+?)");

  private final Project project;

  private GitTagCalculator(Project project) {
    this.project = project;
  }

  public static GitTagCalculator create(@NotNull Project project) {
    return new GitTagCalculator(Preconditions.checkNotNull(project));
  }

  public List<String> tagsForBranch(@NotNull VirtualFile gitRoot, @NotNull String branch) {
    GitSimpleHandler h = new GitSimpleHandler(project, Preconditions.checkNotNull(gitRoot), GitCommand.LOG);
    h.addParameters("--simplify-by-decoration", "--pretty=format:%d", "--encoding=UTF-8",
        Preconditions.checkNotNull(branch));
    h.setSilent(true);
    String getTagsLabel = ResBundle.getString("tag.getting.existing.tags");
    String output = GitHandlerUtil.doSynchronously(h, getTagsLabel, h.printableCommandLine());
    return handleOutput(output);
  }

  private List<String> handleOutput(@Nullable String output) {
    return Optional.ofNullable(output).map(this::parseTags).orElseGet(ArrayList::new);
  }

  private List<String> parseTags(String output) {
    List<String> tags = Lists.newArrayList();
    for (StringScanner s = new StringScanner(output); s.hasMoreData(); ) {
      String line = s.line();
      Matcher match = SINGLE_TAG_PATTERN.matcher(line);
      if (match.matches()) {
        tags.add(match.group(1));
      } else if (line.contains("tag: ")) {
        tags.addAll(parseMultipleTags(line));
      }
    }
    return tags;
  }

  private List<String> parseMultipleTags(String line) {
    List<String> tags = Lists.newArrayList();
    for (String spec : Splitter.on(", ").split(line)) {
      Matcher match = TAG_PATTERN.matcher(spec);
      if (match.matches()) {
        tags.add(match.group(1));
      }
    }
    return tags;
  }
}
