package test;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.text.DecimalFormat;

public class AvgScore extends GenericUDF {

    private static final String FUNC_NAME = "AVE_SCORE";
    private transient MapObjectInspector mapOi;
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public ObjectInspector initialize(ObjectInspector[] objectInspectors) throws UDFArgumentException {
        mapOi = (MapObjectInspector) objectInspectors[0];
        return PrimitiveObjectInspectorFactory.javaDoubleObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {
        Object o = deferredObjects[0].get();
        double v = mapOi.getMap(o).values().stream().mapToDouble(i -> Double.parseDouble(i.toString())).average().orElse(0.0);
        return Double.parseDouble(df.format(v));
    }

    @Override
    public String getDisplayString(String[] strings) {
        return "func(map)";
    }
}
