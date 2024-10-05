package org.vmalibu.m.one.two;

import org.vmalibu.m.one.ServiceRunner;

public class ServiceRunnerImpl implements ServiceRunner {

    @Override
    public void run() {
        System.out.println("Running m-1-2");
    }
}
