/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Completer;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.StringsCompleter;
import org.switchyard.admin.Application;
import org.switchyard.admin.Binding;
import org.switchyard.admin.Reference;
import org.switchyard.admin.Service;
import org.switchyard.admin.SwitchYard;

/**
 * Generates completion set for Application arguments.
 */
@org.apache.karaf.shell.api.action.lifecycle.Service
public class BindingNameCompleter implements Completer {

    private SwitchYard _switchYard;

    @Override
    public int complete(Session session, CommandLine commandLine, List<String> candidates) {
        final StringsCompleter delegate = new StringsCompleter();
        if (session == null) {
            return delegate.complete(session, commandLine, candidates);
        }
        if (commandLine == null || commandLine.getArguments() == null || commandLine.getArguments().length == 4) {
            return delegate.complete(session, commandLine, candidates);
        }
        final List<String> arguments = Arrays.asList(commandLine.getArguments());
        final Application application = _switchYard.getApplication(QName.valueOf(arguments.get(2)));
        if (application == null) {
            return delegate.complete(session, commandLine, candidates);
        }
        final QName serviceOrReferenceName = QName.valueOf(arguments.get(3));
        final Service service = application.getService(serviceOrReferenceName);
        if (service == null) {
            final Reference reference = application.getReference(serviceOrReferenceName);
            if (reference == null) {
                return delegate.complete(session, commandLine, candidates);
            }
            for (Binding binding : reference.getGateways()) {
                if (binding.getName() != null) {
                    delegate.getStrings().add(binding.getName());
                }
            }
        } else {
            for (Binding binding : service.getGateways()) {
                if (binding.getName() != null) {
                    delegate.getStrings().add(binding.getName());
                }
            }
        }
        return delegate.complete(session, commandLine, candidates);
    }

    /**
     * @param switchYard the SwitchYard admin service
     */
    public void setSwitchYard(SwitchYard switchYard) {
        _switchYard = switchYard;
    }

}
