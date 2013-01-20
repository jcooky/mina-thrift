package org.apache.thrift.test;

import com.github.jcooky.mina.thrift.rule.ThriftServerTestRule;
import org.apache.commons.io.FileUtils;
import org.apache.thrift.test.gen.Gateway;
import org.apache.thrift.test.gen.InvalidExcuteException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: JCooky
 * Date: 13. 1. 21
 * Time: 오전 5:06
 * To change this template use File | Settings | File Templates.
 */
public class GatewayTest {
    private Gateway.Iface gwService = mock(Gateway.Iface.class);

    @Rule
    public ThriftServerTestRule thriftServerTestRule = new ThriftServerTestRule(new Gateway.Processor(gwService));

    @Before
    public void setUp() throws Exception {

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


        client.put("test", "test", names, binaries);

        verify(gwService).put("test", "test", names, binaries);
    }
}
