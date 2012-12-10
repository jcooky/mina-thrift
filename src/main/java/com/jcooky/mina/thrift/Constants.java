package com.jcooky.mina.thrift;

import org.apache.mina.core.session.AttributeKey;

public class Constants {
	public static final AttributeKey TRANSPORT = new AttributeKey(Constants.class, "transport");
	public static final AttributeKey BUFFER = new AttributeKey(Constants.class, "buffer");
}
