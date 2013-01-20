namespace java org.apache.thrift.test.gen

exception InvalidExcuteException {
	1: string why
}

service Gateway {
	bool exists(1: string interfaceName),
	oneway void put(1: string interfaceName, 2: string implClassName, 3: list<string> classNames, 4: list<binary> classes),
	oneway void remove(1: string interfaceName),
	binary execute(1: string interfaceName, 2: binary thriftBinaries) throws (1: InvalidExcuteException e)
}