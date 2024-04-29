package network.rpc.core.client;

import com.itranswarp.compiler.JavaStringCompiler;
import network.rpc.core.transport.Transport;

import java.util.Map;

public class DynamicStubFactory implements StubFactory {

    private final static String STUB_SOURCE_TEMPLATE =
            "package network.rpc.core.client.stubs;\n" +
                    "import network.rpc.core.serialize.SerializeSupport;\n" +
                    "\n" +
                    "public class %s extends AbstractStub implements %s {\n" +
                    "    @Override\n" +
                    "    public String %s(String arg) {\n" +
                    "        return SerializeSupport.parse(\n" +
                    "                invokeRemote(\n" +
                    "                        new RpcRequest(\n" +
                    "                                \"%s\",\n" +
                    "                                \"%s\",\n" +
                    "                                SerializeSupport.serialize(arg)\n" +
                    "                        )\n" +
                    "                )\n" +
                    "        );\n" +
                    "    }\n" +
                    "}";

    @Override
    public <T> T createStub(Transport transport, Class<T> serviceClass) {
        String stubName = serviceClass.getSimpleName() + "Stub";
        String fullName = serviceClass.getName();
        String stubFullName = "network.rpc.core.client.stubs." + stubName;
        String methodName = serviceClass.getMethods()[0].getName();

        String source = String.format(STUB_SOURCE_TEMPLATE, stubName, fullName, methodName, fullName, methodName);
        JavaStringCompiler compiler = new JavaStringCompiler();
        try {
            Map<String, byte[]> results = compiler.compile(stubName + ".java", source);
            Class<?> clazz = compiler.loadClass(stubFullName, results);
            ServiceStub stub = (ServiceStub) clazz.newInstance();
            stub.setTransport(transport);
            return (T) stub;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}