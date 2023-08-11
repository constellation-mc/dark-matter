/*
 This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 If a copy of the MPL was not distributed with this file,
 You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.melontini.dark_matter.impl.danger.instrumentation;

import java.lang.instrument.Instrumentation;

public class InstrumentationAgent {
    public static Instrumentation instrumentation;

    public static void agentmain(final String argument, final Instrumentation instrumentation) {
        InstrumentationAgent.instrumentation = instrumentation;
    }
}