package com.eric.codejam.multithread;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eric.codejam.main.AbstractInputData;

public class Consumer<InputData extends AbstractInputData> implements Runnable {

    public static interface TestCaseHandler<InputData> {
        String handleCase(int testCase, InputData data);
    }

    final static Logger log = LoggerFactory.getLogger(Consumer.class);

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
                if (input.testCase < 0) {
                    return;
                }
                String ans = testCaseHandler.handleCase(input.testCase, input);
                answers[input.testCase - 1] = ans;
            }
        } catch (InterruptedException ex) {
            log.error("Prob", ex);
        }
    }

}