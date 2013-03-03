package org.apache.thrift.test;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.test.gen.Gateway;
import org.apache.thrift.test.gen.InvalidExcuteException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jcooky.mina.thrift.test.rule.ThriftServerTestRule;

/**
 * Created with IntelliJ IDEA.
 * User: JCooky
 * Date: 13. 1. 21
 * Time: 오전 5:06
 * To change this template use File | Settings | File Templates.
 */
public class GatewayTest {
    private @Mock Gateway.Iface gwService ;

    public ThriftServerTestRule thriftServerTestRule ;

    @Before
    public void setUp() throws Exception {
    	MockitoAnnotations.initMocks(this);
    	thriftServerTestRule = new ThriftServerTestRule(new Gateway.Processor(gwService));
    	thriftServerTestRule.setUp();
    }
    
    @After
    public void tearDown() throws Exception {
    	thriftServerTestRule.tearDown();
    }

    @Test
    public void testPut() throws Exception {
        Gateway.Iface client = new Gateway.Client(thriftServerTestRule.getClientProtocol());
        List<String> names = new ArrayList<String>();

        names.add(Gateway.class.getName());
        names.add(InvalidExcuteException.class.getName());

        List<ByteBuffer> binaries = new ArrayList<ByteBuffer>();
        binaries.add(ByteBuffer.wrap(FileUtils.readFileToByteArray(new File("target/test-classes/org/apache/thrift/test/gen/Gateway.class"))));
        binaries.add(ByteBuffer.wrap(FileUtils.readFileToByteArray(new File("target/test-classes/org/apache/thrift/test/gen/InvalidExcuteException.class"))));


        assertFalse(client.put("test", "test", names, binaries));

        verify(gwService).put("test", "test", names, binaries);
    }
}
