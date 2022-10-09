package com.pain.rock.antlr.script;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Play {

    public static void main(String[] args) {
        Map params = null;

        try {
            params = parseParams(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        String scriptFile = params.containsKey("scriptFile") ? (String) params.get("scriptFile") : null;
        String script = "";

        if (scriptFile != null) {
            try {
                script = readTextFile(scriptFile);
            } catch (IOException e) {
                System.out.println("unable to read from: " + scriptFile);
                return;
            }
        }

        boolean genAsm = params.containsKey("genAsm") ? (Boolean) params.get("genAsm") : false;
        boolean genByteCode = params.containsKey("genByteCode") ? (Boolean) params.get("genByteCode") : false;
        boolean verbose = params.containsKey("verbose") ? (Boolean) params.get("verbose") : false;
        boolean astDump = params.containsKey("astDump") ? (Boolean) params.get("astDump") : false;

        if (StringUtils.isBlank(script)) {
            REPL(verbose, astDump);
        } else if (genAsm) {
            String outputFile = params.containsKey("outputFile") ? (String) params.get("outputFile") : null;
            generateAsm(script, outputFile);
        } else if (genByteCode) {
            byte[] bytes = generateByteCode(script);
            runJavaClass("App", bytes);
        } else {
            ScriptCompiler compiler = new ScriptCompiler();
            AnnotatedTree tree = compiler.compile(script, verbose, astDump);

            if (!tree.hasCompilationError()) {
                Object result = compiler.execute(tree);
                System.out.println(result);
            }
        }
    }

    private static void REPL(boolean verbose, boolean astDump) {
        System.out.println("Enjoy play script!");

        ScriptCompiler compiler = new ScriptCompiler();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String script = "";
        String scriptLet = "";
        System.out.print("\n>");

        while (true) {
            try {
                String line = reader.readLine().trim();

                if (StringUtils.equals("exit();", line)) {
                    System.out.println("bye!");
                    break;
                }

                scriptLet += line + "\n";

                if (line.endsWith(";")) {
                    AnnotatedTree tree = compiler.compile(script + scriptLet, verbose, astDump);

                    if (!tree.hasCompilationError()) {
                        Object result = compiler.execute(tree);
                        System.out.println(result);
                        script = script + scriptLet;
                    }

                    System.out.print("\n>");
                    scriptLet = "";
                }
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
                System.out.print("\n>");
                scriptLet = "";
            }
        }
    }

    private static void generateAsm(String script, String outputFile) {
        ScriptCompiler compiler = new ScriptCompiler();
        AnnotatedTree tree = compiler.compile(script);
        AsmGenerator asmGenerator = new AsmGenerator(tree);
        String asm = asmGenerator.generate();

        if (outputFile != null) {
            try {
                writeTextFile(outputFile, asm);
            } catch (IOException e) {
                System.out.println("unable to write to: " + outputFile);
                return;
            }
        } else {
            System.out.println(asm);
        }
    }

    private static byte[] generateByteCode(String script) {
        ScriptCompiler compiler = new ScriptCompiler();
        AnnotatedTree tree = compiler.compile(script);
        ByteCodeGenerator byteCodeGenerator = new ByteCodeGenerator(tree);
        byte[] bytes = byteCodeGenerator.generate();
        String outputFile = "App.class";

        try {
            File file = new File(outputFile);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            System.out.println("unable to write to : " + outputFile);
        }

        return bytes;
    }

    private static void runJavaClass(String clazzName, byte[] bytes) {
        try {
            Class clazz = loadClass(clazzName, bytes);
            Method method = clazz.getMethod("main", String[].class);
            method.invoke(null, (Object) new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class loadClass(String className, byte[] bytes) {
        Class clazz = null;
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            Class cls = Class.forName("java.lang.ClassLoader");
            Method method = cls.getDeclaredMethod(
                    "defineClass",
                    new Class[]{String.class, byte[].class, int.class, int.class});
            method.setAccessible(true);
            try {
                Object[] args = new Object[]{className, bytes, 0, bytes.length};
                clazz = (Class) method.invoke(loader, args);
            } finally {
                method.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return clazz;
    }

    private static String readTextFile(String pathName) throws IOException {
        StringBuilder builder = new StringBuilder();

        try (FileReader reader = new FileReader(pathName);
             BufferedReader br = new BufferedReader(reader)) {
            String line;

            while ((line = br.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }

        return builder.toString();
    }

    private static void writeTextFile(String pathName, String text) throws IOException {
        File file = new File(pathName);
        file.createNewFile();

        try (FileWriter writer = new FileWriter(file);
             BufferedWriter out = new BufferedWriter(writer)) {
            StringReader reader = new StringReader(text);
            BufferedReader br = new BufferedReader(reader);
            String line;

            while ((line = br.readLine()) != null) {
                out.write(line);
                out.newLine();
            }
            out.flush();
        }
    }

    private static Map parseParams(String[] args) throws Exception {
        Map<String, Object> params = new HashMap<>();

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-S")) { // 输出汇编代码
                params.put("genAsm", true);
            } else if (args[i].equals("-b")) { // 生成字节码
                params.put("genByteCode", true);
            } else if (args[i].equals("-h") || args[i].equals("--help")) { // 显示作用域和符号
                params.put("help", true);
            } else if (args[i].equals("-v")) { // 显示作用域和符号
                params.put("verbose", true);
            } else if (args[i].equals("-ast-dump")) { // 显示作用域和符号
                params.put("astDump", true);
            } else if (args[i].equals("-o")) { // 输出文件
                if (i + 1 < args.length) {
                    params.put("outputFile", args[++i]);
                } else {
                    throw new Exception("Expecting a filename after -o");
                }
            } else if (args[i].startsWith("-")) { // 不认识的参数
                throw new Exception("Unknown parameter : " + args[i]);
            } else { // 脚本文件
                params.put("scriptFile", args[i]);
            }
        }

        return params;
    }

}
