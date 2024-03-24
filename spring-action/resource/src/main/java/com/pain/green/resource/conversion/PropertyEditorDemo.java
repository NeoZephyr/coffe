package com.pain.green.resource.conversion;

import java.beans.PropertyEditor;

public class PropertyEditorDemo {

    public static void main(String[] args) {
        String text = "name = 杜罗西";
        PropertyEditor propertyEditor = new StringToPropertiesPropertyEditor();
        propertyEditor.setAsText(text);

        System.out.println(propertyEditor.getValue());
        System.out.println(propertyEditor.getAsText());
    }
}
