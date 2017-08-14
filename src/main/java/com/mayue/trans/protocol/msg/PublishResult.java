package com.mayue.trans.protocol.msg;

import java.io.Serializable;

/**
 * 消息发布结果
 */
public class PublishResult implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1232457773783124714L;
    /**
     * when it's BestEffortMessage,it has no value
     */
    private Integer messageContentId;

    public Integer getMessageContentId() {
        return messageContentId;
    }

    public void setMessageContentId(Integer messageContentId) {
        this.messageContentId = messageContentId;
    }

}
