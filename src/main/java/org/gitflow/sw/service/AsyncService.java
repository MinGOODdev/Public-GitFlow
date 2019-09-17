package org.gitflow.sw.service;

import org.gitflow.sw.thread.NewbieRepoInfoCallableThread;

/**
 * 비동기 처리 로직 따로 뺐는데, 나중에 RabbotMQ + docker 사용해서 Thread 없이 작업 분배할 수 있도록 코드 리펙토링하기 위함.
 */
public interface AsyncService {

    boolean insertNewbieInfo(String username, String password);

    void runThread(NewbieRepoInfoCallableThread thread);

}
