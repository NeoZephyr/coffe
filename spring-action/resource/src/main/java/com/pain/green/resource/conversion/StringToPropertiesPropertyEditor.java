package com.pain.green.resource.conversion;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class StringToPropertiesPropertyEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Properties properties = new Properties();

        try {
            properties.load(new StringReader(text));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setValue(properties);
    }

    @Override
    public String getAsText() {
        Properties properties = (Properties) getValue();
        StringBuilder textBuilder = new StringBuilder();

        properties.forEach((k, v) -> {
            textBuilder.append(k).append("=").append(v).append(System.getProperty("line.separator"));
        });

        return textBuilder.toString();
    }
}
