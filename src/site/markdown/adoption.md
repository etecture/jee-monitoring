## Adopting Monitoring in your Application

### Default KPI's

Most of the default KPI's are working out-of-the-box. So there is no neccesarity for a developer here.

An exception to this is the "version" KPI, which should be configured inside the application.xml via an env-entry "Monitoring.Version".

### Request based KPI's

The Request-KPI's are based on the CDI-Event `RequestEvent`. So when an application wants to tell the monitoring facility, that a request was made to this service, it has to fire an instance of this event:

~~~~~
public class AnyService {
    @Inject
    Events<RequestEvent> events;
	
    public void methodThatWillBeTracked() {
        // do whatever you should do in this method
        events.fire(new RequestEvent(...).finished());
    }
}
~~~~~

Another way to fire RequestEvents is the way by using the `RequestTrackingInterceptor` and its corresponding Interceptor-Binding with `@RequestTracking` Annotation:

~~~~~
@RequestTracking
public class AnyService {

    public void methodThatWillBeTracked() {
        // do whatever you should do in this method
    }
}
~~~~~

This interceptor ensures, that all methods inside the service will be tracked as a request. Beside this, the duration of the request will be tracked too.

If there is a failure (a method throws an exception, that is not an `@ApplicationException`) inside the request call, the interceptor recognizes this failure and tracks it inside the RequestEvent (so that MTBF or failureCount can do it's statistics).
