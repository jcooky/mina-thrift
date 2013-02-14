package com.github.jcooky.mina.thrift.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: JCooky
 * Date: 13. 1. 23
 * Time: 오전 1:41
 * To change this template use File | Settings | File Templates.
 */
public class TMessage {
    private int frameSize;
    private IoBuffer frame;


    public TMessage(int frameSize, IoBuffer frame) {
        this.frameSize = frameSize;
        this.frame = frame;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public IoBuffer getFrame() {
        return frame;
    }

}
