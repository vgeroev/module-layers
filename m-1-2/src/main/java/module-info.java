import org.vmalibu.m.one.ServiceRunner;
import org.vmalibu.m.one.two.ServiceRunnerImpl;

module org.vmalibu.m.one.two {
    requires org.vmalibu.m.one;
    exports org.vmalibu.m.one.two;

    provides ServiceRunner with ServiceRunnerImpl;
}