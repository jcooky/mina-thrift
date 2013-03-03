package com.github.jcooky.mina.thrift;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.test.gen.Gateway;
import org.apache.thrift.test.gen.InvalidExcuteException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jcooky.mina.thrift.test.rule.TMinaServerTestRule;

public class GatewayTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private Gateway.Iface gwService ;

	private TMinaServerTestRule minaServerTestRule ;
    
    @Before
    public void setUp() throws Exception {
    	gwService = mock(Gateway.Iface.class);
    	
    	minaServerTestRule = new TMinaServerTestRule(new Gateway.Processor<Gateway.Iface>(gwService));
    	minaServerTestRule.setUp();
    }
    
    @After
    public void tearDown() throws Exception {
    	minaServerTestRule.tearDown();
    }

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
		
		verify(gwService).put("test", "test", names, binaries);
	}
}
