package com.github.jcooky.mina.thrift;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcooky.mina.thrift.message.TMessage;

public class TIoSessionTransport extends TTransport {
	private static final Logger logger = LoggerFactory.getLogger(TIoSessionTransport.class);
	
	private IoSession session;
	
	public static class InputTransportFactory extends TTransportFactory {

		public TTransport getTransport(final TTransport trans) {
            final IoSession session = ((TIoSessionTransport)trans).getSession();
			
			return new TTransport() {
				
				public boolean isOpen() {
					return true;
				}

				public void open() throws TTransportException {
				}

				public void close() {
				}

				public int read(byte[] buf, int off, int len)
						throws TTransportException {
                    TMessage message = (TMessage)session.getAttribute(TMinaServer.MESSAGE);
                    IoBuffer frame = message.getFrame();

					int readLen = frame.remaining() > len ? len : frame.remaining();
					frame.get(buf, off, readLen);
					
					return readLen;
				}

				public void write(byte[] buf, int off, int len)
						throws TTransportException {
					throw new UnsupportedOperationException();
				}
				
			};
		}
		
	}
	
	public TIoSessionTransport(IoSession session) {
		this.session = session;
	}
	
	public IoSession getSession() {
		return session;
	}
	
	public boolean isOpen() {
		return session.isConnected();
	}

	public void open() throws TTransportException {
	}

	public void close() {
		if (session.isConnected() && !session.isClosing()) {
			try {
				flush();
			} catch(TTransportException e) {
				logger.error(e.getMessage(), e);
			}
			session.close(false);
		}
	}

	public int read(byte[] buf, int off, int len) throws TTransportException {
		throw new UnsupportedOperationException();
	}

	public void write(byte[] buf, int off, int len) throws TTransportException {
		IoBuffer buffer = IoBuffer.allocate(len);

		buffer.clear();
		buffer.put(buf, off, len);
		buffer.flip();
		session.write(new TMessage(len, buffer));
		buffer.free();
	}


}
