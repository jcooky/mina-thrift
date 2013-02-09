package com.github.jcooky.mina.thrift;

import com.github.jcooky.mina.thrift.test.rule.TMinaServerTestRule;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class GatewayTest {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Gateway.Iface gwService = mock(Gateway.Iface.class);
    @Rule
    public TMinaServerTestRule serverTestRule = new TMinaServerTestRule(new Gateway.Processor<Gateway.Iface>(gwService));

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

        verify(gwService, times(1)).put("test", "test", names, binaries);
    }

    @Test
    public void testRepeatedThriftServer() throws Exception {
        int c = 2;
        ExecutorService threadPool = Executors.newFixedThreadPool(c);
        for (int i = 0 ; i < c ; ++i) {
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        testThriftServer();
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        threadPool.awaitTermination(3, TimeUnit.SECONDS);
    }
}
