package org.gitflow.sw.service;

import org.gitflow.sw.dto.Exclude;
import org.gitflow.sw.dto.IgnorePath;
import org.gitflow.sw.dto.Include;
import org.gitflow.sw.dto.MustContain;

import java.util.List;

public interface FileReaderService {

    boolean ignorePathCheck(String fileName, List<IgnorePath> ignorePaths);

    boolean includeCheck(String fileName, List<Include> includes);

    boolean excludeCheck(String fileName, List<Exclude> excludes);

    boolean mustContainCheck(String fileName, List<MustContain> mustContains);

}
