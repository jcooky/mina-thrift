package com.github.jcooky.mina.thrift.codec;

import com.github.jcooky.mina.thrift.message.TMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/**
 * Created with IntelliJ IDEA.
 * User: JCooky
 * Date: 13. 1. 23
 * Time: 오전 12:40
 * To change this template use File | Settings | File Templates.
 */
public class TFrameProtocolDecoder implements MessageDecoder {
    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if (in.remaining() == 0 || (in.remaining() > 0 && in.remaining() < 4))
            return MessageDecoderResult.NEED_DATA;
        else if (in.remaining() > 4) {
            int frameSize = in.getInt();
            while(true) {
                if (in.remaining() >= frameSize)
                    return MessageDecoderResult.OK;
                else {
                    synchronized (in) {
                        in.position(in.position() - 4);
                    }
                    return MessageDecoderResult.NEED_DATA;
                }
            }
        }

        return MessageDecoderResult.NOT_OK;
    }

    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (!(Boolean)session.setAttribute("decode_finish")) {
            session.setAttribute("decode_finish", true);
            TMessage msg = new TMessage(in.getInt(), in);
            out.write(msg);
        }

        return MessageDecoderResult.OK;
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        session.setAttribute("decode_finish", false);
    }
}
