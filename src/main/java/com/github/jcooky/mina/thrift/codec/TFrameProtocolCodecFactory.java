package com.github.jcooky.mina.thrift.codec;

import com.github.jcooky.mina.thrift.message.TMessage;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

/**
 * Created with IntelliJ IDEA.
 * User: JCooky
 * Date: 13. 1. 23
 * Time: 오전 1:39
 * To change this template use File | Settings | File Templates.
 */
public class TFrameProtocolCodecFactory extends DemuxingProtocolCodecFactory {
    public TFrameProtocolCodecFactory() {
        super();

        super.addMessageDecoder(new TFrameProtocolDecoder());
        super.addMessageEncoder(TMessage.class, new TFrameProtocolEncoder());
    }
}
