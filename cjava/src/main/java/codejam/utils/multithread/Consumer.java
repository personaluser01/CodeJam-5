package codejam.utils.multithread;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codejam.utils.main.AbstractInputData;

public class Consumer<InputData extends AbstractInputData> implements Runnable {

    public static interface TestCaseHandler<InputData> {
        String handleCase(InputData in);
    }

    final static Logger log = LoggerFactory.getLogger("main");

    private final BlockingQueue<InputData> queue;
    private final String[] answers;
    private final TestCaseHandler<InputData> testCaseHandler;

    public Consumer(BlockingQueue<InputData> q, String[] answers,
            TestCaseHandler<InputData> testCaseHandler) {
        queue = q;
        this.answers = answers;
        this.testCaseHandler = testCaseHandler;
    }

    public void run() {
        try {
            while (true) {
                
                InputData input = queue.take();
                log.debug("Consumer thread loop.  TestCase #{}", input.testCase);
                if (input.testCase < 0) {
                    log.debug("Consumer Thread ending");
                    return;
                }
                String ans = testCaseHandler.handleCase(input);
                log.info(ans);
                answers[input.testCase - 1] = ans;
            }
        } catch (InterruptedException ex) {
            log.error("Prob", ex);
        }
    }

}
