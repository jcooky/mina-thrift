package com.github.jcooky.mina.thrift.codec;

import com.github.jcooky.mina.thrift.message.TMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: JCooky
 * Date: 13. 1. 23
 * Time: 오전 1:32
 * To change this template use File | Settings | File Templates.
 */
public class TFrameProtocolEncoder implements MessageEncoder<TMessage> {
    @Override
    public void encode(IoSession session, TMessage message, ProtocolEncoderOutput out) throws Exception {
        IoBuffer buf = IoBuffer.allocate(message.getFrameSize() + 4, true);

        // Encode a header
        buf.putInt(message.getFrameSize());
        buf.put(message.getFrame());

        buf.flip();
        out.write(buf);
    }
}
