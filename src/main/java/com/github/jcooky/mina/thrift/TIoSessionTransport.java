package com.github.jcooky.mina.thrift;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TIoSessionTransport extends TTransport {
	private static final Logger logger = LoggerFactory.getLogger(TIoSessionTransport.class);
	
	private IoSession session;
	
	public static class InputTransportFactory extends TTransportFactory {

		public TTransport getTransport(TTransport trans) {
			final TIoSessionTransport transport = (TIoSessionTransport)trans;
			final IoBuffer buffer = (IoBuffer)transport.getSession().getAttribute(Constants.BUFFER);
			
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
					int readLen = buffer.remaining() > len ? len : buffer.remaining();
					buffer.get(buf, off, len);
					
					StringBuilder sb = new StringBuilder();
					sb.append("pos=").append(off).append(" len=").append(len).append(": ");
					for (int i = off ; i < len ; ++i) {
						sb.append(buf[i]);
						if (i != len - 1)
							sb.append(' ');
					}
					logger.info("ReadBuffer[{}]", sb);
					
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
		StringBuilder sb = new StringBuilder();
		sb.append("pos=").append(off).append(" len=").append(len).append(": ");
		for (int i = off ; i < len ; ++i) {
			sb.append(buf[i]);
			if (i != len - 1)
				sb.append(' ');
		}
		logger.info("WriteBuffer[{}]", sb);
		IoBuffer buffer = IoBuffer.allocate(1024).setAutoExpand(true);
		buffer.clear();
		buffer.put(buf, off, len);
		buffer.flip();
		session.write(buffer);
		buffer.free();
	}

}
