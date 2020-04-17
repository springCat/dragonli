import cn.hutool.core.date.TimeInterval;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Resilience4jTest {


    //@Test
    public void testRetry(){
        TimeInterval timeInterval = new TimeInterval();
        Retry retry = Retry.ofDefaults("backendName");
        retry.executeSupplier(() -> {
            System.out.println("try try try cost:"+timeInterval.intervalRestart());
            throw new RuntimeException();
        });

    }

    //@Test
    public void testCircuitBreaker(){
        // Create a CircuitBreaker (use default configuration)
        TimeInterval timeInterval = new TimeInterval();
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .build();

        CircuitBreaker circuitBreaker = CircuitBreaker
                .of("backendName",circuitBreakerConfig);

        String result = circuitBreaker.executeSupplier(() -> {
            System.out.println("try try try cost:"+timeInterval.intervalRestart());
            throw new RuntimeException();
        });
        System.out.println(result);
    }


    //@Test
    public void testReyCircuitBreaker(){

        TimeInterval timeInterval = new TimeInterval();

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .minimumNumberOfCalls(4)
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .build();

        CircuitBreaker circuitBreaker = CircuitBreaker
                .of("backendName",circuitBreakerConfig);

        Retry retry = Retry.ofDefaults("backendName");
        AtomicInteger i = new AtomicInteger(0);
        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    System.out.println(i.incrementAndGet()+ "try try try cost:"+timeInterval.intervalRestart());
                    throw new RuntimeException("1111");
                });


        decoratedSupplier = Retry
                .decorateSupplier(retry, decoratedSupplier);


            try {
                String result = decoratedSupplier.get();
                System.out.println(result);
            }catch (CallNotPermittedException e){
                System.out.println("CallNotPermittedException");
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
    }
}

