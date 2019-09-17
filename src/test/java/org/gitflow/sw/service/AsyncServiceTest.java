//package org.gitflow.sw.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.gitflow.sw.serviceImpl.AsyncServiceImpl;
//import org.gitflow.sw.thread.NewbieRepoInfoCallableThread;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Future;
//import java.util.concurrent.ThreadPoolExecutor;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Slf4j
//public class AsyncServiceTest {
//
//    private AsyncService asyncService;
//    private ExecutorService executorService;
//    private NewbieRepoInfoCallableThread thread;
//
//    @Before
//    public void setup() {
//        this.asyncService = new AsyncServiceImpl();
//        this.thread = new NewbieRepoInfoCallableThread();
//        this.executorService = this.thread.executorService;
//
//        for (int i = 0; i < 100; i++) {
//            NewbieRepoInfoCallableThread.NEWBIE_QUEUE.add("park" + i);
//        }
//    }
//
//    @Test
//    public void test() throws Exception {
//        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) this.executorService;
//
//        while (!NewbieRepoInfoCallableThread.NEWBIE_QUEUE.isEmpty()) {
//      /*
//      Future 객체는 작업 결과가 아니라 작업이 완료될 때까지 기다렸다가 최종 결과를 얻는데 사용합니다.
//      그래서 Future는 지연 완료(pending Completion) 객체라고 합니다. Future의 get() 메소드를
//      호출하면 스레드가 작업을 완료할 때까지 블로킹되었다가 작업을 완료하면 처리 결과를 리턴합니다.
//      이것이 블로킹을 사용하는 작업 완료 통보 방식입니다.
//
//      Future를 이용한 블로킹 방식의 작업 완료 통보에서 주의할 점은 작업을 처리하는 스레드가 작업을 완료하기 전까지는 get() 메소드가 블로킹되므로
//      다른 코드를 실행할 수 없습니다. 다른 코드를 하는 스레드가 get() 메소드를 호출하면 작업을 완료하기 전까지 다른 코드를 처리할 수 없게 됩니다.
//      그렇기 때문에 get() 메소드를 호출하는 스레드는 새로운 스레드이거나 스레드 풀의 또 다른 스레드가 되어야 합니다.
//       */
//
//            //메인 스레드는 "main"이라는 이름을 가지고 있고, 직접 생성한 스레드는 자동적으로 "Thread-n" 형식으로 설정됩니다.
//            Future<String> future = this.executorService.submit(this.thread);
//
//            /**
//             * future 클래스의 get() 으로 호출 진행 시, 작업이 끝나야지 get()으로 반환을 받기 때문에,
//             * get() 이후의 코드는 작업이 진행되지 않는다.
//             * 따라서, get() 메소드 또한 스레드로 관리해서 작업이 종료되는데로 결과 값을 받아 반환하게 한다.
//             * 아래의 매소드는 기존에 만들어 놓은 Newbie ThreadPool을 사용해서 pool 안에 스래드를 사용하는 코드.
//             */
//            this.executorService.submit(new Runnable() {
//                String result = "";
//
//                @Override
//                public void run() {
//                    try {
//                        result = future.get();
//                        boolean isWorkCancelled = future.isCancelled();
//                        boolean isWorkDone = future.isDone();
//
//                        System.out.format("결과 : %s , 작업 스레드 : %s , 작업 결과 : %s, %s \n",
//                                result, Thread.currentThread().getName(), isWorkCancelled, isWorkDone);
//                    } catch (Exception e) {
//
//                    }
//                }
//            });
//
//        }
//
//        this.executorService.shutdown();
//    }
//
//}
