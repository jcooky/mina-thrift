package com.github.jcooky.mina.thrift;

import com.github.jcooky.mina.thrift.rule.TMinaServerTestRule;
import org.apache.commons.io.FileUtils;
import org.apache.thrift.test.gen.Gateway;
import org.apache.thrift.test.gen.InvalidExcuteException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GatewayTest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Gateway.Iface gwService = mock(Gateway.Iface.class);
    public @Rule
    TMinaServerTestRule serverTestRule = new TMinaServerTestRule(new Gateway.Processor(gwService));

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testThriftServer() throws Exception {
        Gateway.Iface client = new Gateway.Client(serverTestRule.getClientProtocol());
        List<String> names = new ArrayList<String>();

        names.add(Gateway.class.getName());
        names.add(InvalidExcuteException.class.getName());

        List<ByteBuffer> binaries = new ArrayList<ByteBuffer>();
        binaries.add(ByteBuffer.wrap(FileUtils.readFileToByteArray(new File("target/test-classes/org/apache/thrift/test/gen/Gateway.class"))));
        binaries.add(ByteBuffer.wrap(FileUtils.readFileToByteArray(new File("target/test-classes/org/apache/thrift/test/gen/InvalidExcuteException.class"))));


        client.put("test", "test", names, binaries);

        verify(gwService).put("test", "test", names, binaries);
    }
}
