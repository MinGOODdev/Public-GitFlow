package org.gitflow.sw.thread;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.service.AsyncService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread 생성에는 3가지 방법이 있다.
 * - Thread(extends), Runnable(implements), Callable(implements)
 */
@Slf4j
public class NewbieRepoInfoCallableThread implements Callable<String> {

    private AsyncService asyncService;

    /*
    newCachedThreadPool() 메소드로 생성된 스레드 풀의 특징은 초기 스레드 수와 코어 스레드 수는 0개이고,
    스레드 개수보다 작업 개수가 많으면 새 스레드를 생성시켜 작업을 처리합니다. 이론적으로는 int 값이 가질 수 있는
    최대값만큼 스레드가 추가되지만, 운영체제의 성능과 상황에 따라 달라집니다. 1개 이상의 스레드가 추가되었을 경우
    60초 동안 추가된 스레드가 아무 작업을 하지 않으면 추가된 스레드를 종료하고 풀에서 제거합니다.

    newFixedThreadPool(int nThreads) 메소드로 생성된 스레드 풀의 초기 스레드 개수는 0개이고, 코어 스레드 수는 nThreads입니다.
    스레드 개수보다 작업 개수가 많으면 새 스레드를 생성시키고 작업을 처리합니다. 최대 스레드 개수는 파라미터로 준 nThreads 입니다.
    이 스레드 풀은 스레드가 작업을 처리하지 않고 놀고 있더라도 스레드 개수가 줄지 않습니다.
    다음은 CPU 코어의 수만큼 최대 스레드를 사용하는 스레드 풀을 생성합니다.
     */
    public ExecutorService executorService;
    private ThreadPoolExecutor threadPoolExecutor;

    private String username;
    private String password;

    public NewbieRepoInfoCallableThread(AsyncService asyncService, String username, String password) {
        this.asyncService = asyncService;
        this.username = username;
        this.password = password;

        this.executorService = Executors.newFixedThreadPool(10);
        this.threadPoolExecutor = (ThreadPoolExecutor) this.executorService;
    }

    @Override
    public String call() {

        try {
            log.info("[" + Thread.currentThread().getName() + "]" + this.username + " 시작");

            boolean insertSuccessed = asyncService.insertNewbieInfo(this.username, this.password);
            if (!insertSuccessed) {
                return "비동기 로직 에러";
            }

        } catch (Exception e) {
            e.getCause().printStackTrace();
        }

        return this.username;
    }
}
