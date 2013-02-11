package com.github.jcooky.mina.thrift.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcooky.mina.thrift.message.TMessage;

/**
 * Created with IntelliJ IDEA.
 * User: JCooky
 * Date: 13. 1. 23
 * Time: 오전 12:40
 * To change this template use File | Settings | File Templates.
 */
public class TFrameProtocolDecoder implements MessageDecoder {
	private static final Logger logger = LoggerFactory.getLogger(TFrameProtocolDecoder.class);
    private static final String DEC_FIN_KEY = "decode-finish";
    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if (in.remaining() == 0 || (in.remaining() > 0 && in.remaining() < 4))
            return MessageDecoderResult.NEED_DATA;
        else if (in.remaining() > 4) {
            int frameSize = in.getInt();
            if (in.remaining() >= frameSize)
                return MessageDecoderResult.OK;
            else {
                return MessageDecoderResult.NEED_DATA;
            }
        }

        return MessageDecoderResult.NOT_OK;
    }

    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
    	int frameSize = in.getInt();
    	int limit = in.limit();
    	in.limit(4 + frameSize);
    	IoBuffer frame = IoBuffer.allocate(frameSize, true);
    	frame.clear();
    	frame.put(in);
    	frame.flip();
    	in.limit(limit);
    	
        TMessage msg = new TMessage(frameSize, frame);
        out.write(msg);

        return MessageDecoderResult.OK;
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
    }
}
