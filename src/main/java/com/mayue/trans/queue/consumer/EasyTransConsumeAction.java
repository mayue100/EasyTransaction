package com.mayue.trans.queue.consumer;

public enum EasyTransConsumeAction {
    /**
     * consume success,process the next message
     */
    CommitMessage,
    /**
     * consume fail, re-consume later,process the next message
     */
    ReconsumeLater,
}
