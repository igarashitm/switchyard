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

import org.apache.karaf.shell.api.console.CommandLine;
import org.apache.karaf.shell.api.console.Completer;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.support.completers.StringsCompleter;
import org.switchyard.admin.Application;
import org.switchyard.admin.SwitchYard;
import org.switchyard.config.model.switchyard.ArtifactModel;

/**
 * Generates completion set for artifact arguments.
 */
public class ArtifactCompleter implements Completer {

    private SwitchYard _switchYard;

    @Override
    public int complete(Session session, CommandLine commandLine, List<String> candidates) {
        final StringsCompleter delegate = new StringsCompleter();
        final UsesArtifactCommand.SearchType type = getType(session, commandLine);
        for (Application application : _switchYard.getApplications()) {
            if (application.getConfig().getArtifacts() == null) {
                continue;
            }
            for (ArtifactModel artifact : application.getConfig().getArtifacts().getArtifacts()) {
                switch (type) {
                case name:
                    delegate.getStrings().add(artifact.getName());
                    break;
                case url:
                    delegate.getStrings().add(artifact.getURL());
                    break;
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

    private UsesArtifactCommand.SearchType getType(Session session, CommandLine commandLine) {
        if (session == null) {
            return null;
        }
        
        if (commandLine == null || commandLine.getArguments() == null || commandLine.getArguments().length == 0) {
            return null;
        }
        final List<String> arguments = Arrays.asList(commandLine.getArguments());
        int argumentOffset = 1; // command is first argument
        for (int index = 0, count = arguments.size(); index < count; ++index) {
            if (arguments.get(index).startsWith("-")) {
                argumentOffset = index;
            }
        }
        // XXX: assuming the last option does not accept a value here
        if (argumentOffset < arguments.size()) {
            return UsesArtifactCommand.SearchType.valueOf(arguments.get(argumentOffset));
        }
        return null;
    }

}
