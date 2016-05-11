/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.deploy.karaf;

import javax.xml.namespace.QName;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.support.completers.StringsCompleter;
import org.switchyard.admin.Application;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;

/**
 * Shell command for uses-artifact.
 */
@Command(scope = "switchyard", name = "throttling", description = "Update throttling settings on a service.")
@org.apache.karaf.shell.api.action.lifecycle.Service
public class UpdateThrottlingCommand extends AbstractSwitchYardServiceCommand {

    /**
     * Specifies the type: enable, disable.
     */
    public static enum OperationType {
        /** enable operation. */
        enable,
        /** disable operation. */
        disable;
    }

    @Argument(index = 0, name = "operation", description = "Specifies the operation type [enable | disable].", required = true)
    @Completion(StringsCompleter.class)
    private OperationType _operation;

    @Argument(index = 1, name = "application", description = "Specifies the name of the application containing the binding.", required = true)
    @Completion(ApplicationNameCompleter.class)
    private String _application;

    @Argument(index = 2, name = "service", description = "Specifies the name of the service containing the binding.", required = true)
    @Completion(ServiceNameCompleter.class)
    private String _service;

    @Option(name = "--maxRequests", description = "The maximum number of requests per period.")
    private Integer _maxRequests;

    @Override
    protected Object doExecute(final SwitchYard switchYard) throws Exception {
        final Application application = switchYard.getApplication(QName.valueOf(_application));
        if (application == null) {
            System.err.println("Could not locate application: " + _application);
            return null;
        }
        final QName serviceName = QName.valueOf(_service);
        final Service service = application.getService(serviceName);
        if (service == null) {
            System.err.println("Could not locate service: " + _service);
            return null;
        }
        service.getThrottling().update(_operation == OperationType.enable, _maxRequests);
        return null;
    }

}
