package com.github.jcooky.mina.thrift;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.test.gen.Gateway;
import org.apache.thrift.test.gen.InvalidExcuteException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.internal.matchers.Any;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcooky.mina.thrift.test.rule.TMinaServerTestRule;

public class GatewayTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private Boolean calledPut = false;
	private Gateway.Iface gwService = mock(Gateway.Iface.class, new Answer() {

		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			if ("put".equals(invocation.getMethod().getName())) {
				calledPut = true;
			}
			return null;
		}
		
	});

    @Rule
	public TMinaServerTestRule minaServerTestRule = new TMinaServerTestRule(new Gateway.Processor<Gateway.Iface>(gwService));
    
    @Before
    public void setUp() throws Exception {
    	
    }

	@Test(timeout=1000)
	public void testThriftServer() throws Exception {
		TProtocol protocol = minaServerTestRule.getClientProtocol();
		assertNotNull(protocol);
		Gateway.Iface client = new Gateway.Client(protocol);
		List<String> names = new ArrayList<String>();

		names.add(Gateway.class.getName());
		names.add(InvalidExcuteException.class.getName());

		List<ByteBuffer> binaries = new ArrayList<ByteBuffer>();
		binaries.add(ByteBuffer.wrap(FileUtils.readFileToByteArray(new File(
				"target/test-classes/org/apache/thrift/test/gen/Gateway.class"))));
		binaries.add(ByteBuffer.wrap(FileUtils.readFileToByteArray(new File(
				"target/test-classes/org/apache/thrift/test/gen/InvalidExcuteException.class"))));

		client.put("test", "test", names, binaries);
		
		while(!calledPut) Thread.sleep(1);
		verify(gwService).put("test", "test", names, binaries);
	}
}
