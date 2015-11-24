package org.thymeleaf.support.customattributes;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by adnan on 11/22/15.
 */
public class HaystaxsDialect extends AbstractDialect {
    @Override
    public String getPrefix() {
        return("hs");
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new AsIsAttributeProcessor());
        return processors;
    }
}
