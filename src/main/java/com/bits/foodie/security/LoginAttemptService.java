package com.bits.foodie.security;

import com.bits.foodie.config.AppProperties;
import com.bits.foodie.exception.ApiException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory brute-force guard: too many failed logins for an email locks that email out for a while. */
@Service
public class LoginAttemptService {

    private record Attempts(int failures, Instant lockedUntil) {
    }

    private final ConcurrentHashMap<String, Attempts> attempts = new ConcurrentHashMap<>();
    private final int maxFailures;
    private final Duration lockout;

    public LoginAttemptService(AppProperties props) {
        this.maxFailures = props.getSecurity().getMaxFailedLogins();
        this.lockout = Duration.ofMinutes(props.getSecurity().getLockoutMinutes());
    }

    private static String key(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    /** Throws 429 while the email is locked. */
    public void checkAllowed(String email) {
        Attempts a = attempts.get(key(email));
        if (a == null || a.lockedUntil() == null) return;
        if (Instant.now().isBefore(a.lockedUntil())) {
            long minutes = Math.max(1, Duration.between(Instant.now(), a.lockedUntil()).toMinutes() + 1);
            throw ApiException.tooManyRequests("Too many failed attempts. Try again in " + minutes + " minute(s).");
        }
        attempts.remove(key(email));
    }

    public void recordFailure(String email) {
        attempts.compute(key(email), (k, a) -> {
            int failures = (a == null ? 0 : a.failures()) + 1;
            return failures >= maxFailures ? new Attempts(0, Instant.now().plus(lockout)) : new Attempts(failures, null);
        });
    }

    public void recordSuccess(String email) {
        attempts.remove(key(email));
    }
}
