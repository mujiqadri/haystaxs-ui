package org.thymeleaf.support.customattributes;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by adnan on 11/22/15.
 */
public class AsIsAttributeProcessor extends AbstractUnescapedTextChildModifierAttrProcessor {
    public AsIsAttributeProcessor() {
        super("asis");
    }

    @Override
    public int getPrecedence() {
        return 12000;
    }


    @Override
    protected String getText(Arguments arguments, Element element, String s) {
        String path = arguments.getContext().getVariables().get("appRealPath") + element.getAttributeValue("hs:asis");

        String result = null;

        try {
            result = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
