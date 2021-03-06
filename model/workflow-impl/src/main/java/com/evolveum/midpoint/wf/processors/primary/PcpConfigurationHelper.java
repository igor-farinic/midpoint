/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.wf.processors.primary;

import com.evolveum.midpoint.util.exception.SystemException;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.wf.processors.BaseConfigurationHelper;
import com.evolveum.midpoint.wf.processors.primary.aspect.PrimaryChangeAspect;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mederly
 */
@Component
public class PcpConfigurationHelper {
    private static final Trace LOGGER = TraceManager.getTrace(PcpConfigurationHelper.class);

    @Autowired
    private BaseConfigurationHelper baseConfigurationHelper;

    // configuration
    private static final String KEY_ASPECT = "aspect";
    private static final List<String> LOCALLY_KNOWN_KEYS = Arrays.asList(KEY_ASPECT);

    public void configure(PrimaryChangeProcessor primaryChangeProcessor) {
        baseConfigurationHelper.configureProcessor(primaryChangeProcessor, LOCALLY_KNOWN_KEYS);
        setPrimaryChangeProcessorAspects(primaryChangeProcessor);
    }

    private void setPrimaryChangeProcessorAspects(PrimaryChangeProcessor primaryChangeProcessor) {

        List<PrimaryChangeAspect> wrappers = new ArrayList<>();

        Configuration c = primaryChangeProcessor.getProcessorConfiguration();
        if (c != null) {
            String[] wrappersNames = c.getStringArray(KEY_ASPECT);
            if (wrappersNames == null || wrappersNames.length == 0) {
                LOGGER.warn("No wrappers defined for primary change processor " + primaryChangeProcessor.getBeanName());
            } else {
                for (String wrapperName : wrappersNames) {
                    LOGGER.trace("Searching for aspect " + wrapperName);
                    try {
                        PrimaryChangeAspect wrapper = (PrimaryChangeAspect) primaryChangeProcessor.getBeanFactory().getBean(wrapperName);
                        wrappers.add(wrapper);
                    } catch (BeansException e) {
                        throw new SystemException("Change aspect " + wrapperName + " could not be found.", e);
                    }
                }
                LOGGER.debug("Resolved " + wrappers.size() + " process wrappers for primary change processor " + primaryChangeProcessor.getBeanName());
            }
        }
        primaryChangeProcessor.setProcessWrappers(wrappers);
    }

}
