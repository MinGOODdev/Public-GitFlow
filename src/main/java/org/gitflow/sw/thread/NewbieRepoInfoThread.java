package org.gitflow.sw.thread;

import org.gitflow.sw.service.AsyncService;

/**
 * ### 주의 : 동시 접속자를 위한 비동기 처리는 안되어 있다. 추후 Queue를 사용하여 동시 접속자 까지 처리.
 * <p>
 * 1. 회원가입 시도 => 2. 적절한 가입 정보(username, password) => 3. Thread 실행(insertNewbieInfo)
 * <p>
 * - 스레드를 특정 시점(2번)에서 동작하도록 하기 위해서 thread를 new 로 구현했다.
 * - 신규회원의 정보로 git api 돌리고, 'username'을 획득하기 위해서 생성자를 사용.
 */
public class NewbieRepoInfoThread extends Thread {

    public AsyncService asyncService;

    public String username;
    public String password;

    /**
     * # Service 클래스를 생성자로 받은 이유.
     * <p>
     * - 스레드가 new 로 생성 되어, NewbieRepoInfoThread 클래스에서 @Autowired가 불가 => NullPointerException 발생.
     * - controller에서 AsyncService 생성해서 bean 으로 등록 시키고, 그 객체를 넘겨 받아서 메소드 사용하기 위한 방법.
     *
     * @param username
     * @param password
     * @param asyncService
     */
    public NewbieRepoInfoThread(String username, String password, AsyncService asyncService) {
        this.username = username;
        this.password = password;
        this.asyncService = asyncService;
    }

    @Override
    public void run() {
        asyncService.insertNewbieInfo(this.username, this.password);
    }
}
