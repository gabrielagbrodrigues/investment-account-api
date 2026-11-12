
package investment_account_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    private static final long SPEED_FACTOR = 720;

    @Value("${app.clock.fixed-date:}")
    private String fixedDate;

    @Bean
    public Clock clock() {

        ZoneId zone = ZoneId.systemDefault();

        LocalDate startDate;

        if (fixedDate == null || fixedDate.isBlank()) {
            startDate = LocalDate.now(zone);
        } else {
            startDate = LocalDate.parse(fixedDate);
        }

        Clock realClock = Clock.system(zone);

        Instant realStart = realClock.instant();

        Instant simulatedStart =
                startDate.atStartOfDay(zone).toInstant();

        return new AcceleratedClock(
                realClock,
                realStart,
                simulatedStart,
                SPEED_FACTOR
        );
    }

    private static class AcceleratedClock extends Clock {

        private final Clock realClock;
        private final Instant realStart;
        private final Instant simulatedStart;
        private final long speedFactor;

        public AcceleratedClock(
                Clock realClock,
                Instant realStart,
                Instant simulatedStart,
                long speedFactor) {

            this.realClock = realClock;
            this.realStart = realStart;
            this.simulatedStart = simulatedStart;
            this.speedFactor = speedFactor;
        }

        @Override
        public ZoneId getZone() {
            return realClock.getZone();
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new AcceleratedClock(
                    realClock.withZone(zone),
                    realStart,
                    simulatedStart,
                    speedFactor
            );
        }

        @Override
        public Instant instant() {

            Duration elapsed = Duration.between(
                    realStart,
                    realClock.instant()
            );

            return simulatedStart.plus(
                    elapsed.multipliedBy(speedFactor)
            );
        }
    }
}
