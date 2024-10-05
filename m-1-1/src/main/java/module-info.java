import org.vmalibu.m.one.ServiceRunner;
import org.vmalibu.m.one.one.ServiceRunnerImpl;

module org.vmalibu.m.one.one {
    requires org.vmalibu.m.one;
    exports org.vmalibu.m.one.one;

    provides ServiceRunner with ServiceRunnerImpl;
}