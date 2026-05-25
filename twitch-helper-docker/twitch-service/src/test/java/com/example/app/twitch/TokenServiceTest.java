package com.example.app.twitch;

import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password");
        user.setClientId("client-id");
        user.setClientSecret("client-secret");
    }

    @Test
    void getAccessToken_returnsExistingTokenWhenStillValid() {
        // Токен есть и ещё не истёк (expiredAt в будущем)
        user.setAccessToken("valid-token");
        user.setExpiredAt(System.currentTimeMillis() + 100_000L);

        String token = tokenService.getAccessToken(user);

        assertEquals("valid-token", token);
        // userRepository.save НЕ должен вызываться — HTTP-запроса не было
    }

    @Test
    void getAccessToken_throwsWhenTokenNullAndHttpFails() {
        // Токена нет — будет попытка HTTP к Twitch, которая упадёт без сети
        user.setAccessToken(null);
        user.setExpiredAt(null);

        assertThrows(RuntimeException.class, () -> tokenService.getAccessToken(user));
    }

    @Test
    void getAccessToken_throwsWhenTokenExpiredAndHttpFails() {
        // Токен просрочен — тоже попытка HTTP
        user.setAccessToken("expired-token");
        user.setExpiredAt(System.currentTimeMillis() - 1000L);

        assertThrows(RuntimeException.class, () -> tokenService.getAccessToken(user));
    }

    @Test
    void getAccessToken_throwsWhenTokenEmptyAndHttpFails() {
        // Пустой токен — тоже должен обновляться
        user.setAccessToken("");
        user.setExpiredAt(System.currentTimeMillis() + 100_000L);

        assertThrows(RuntimeException.class, () -> tokenService.getAccessToken(user));
    }
}
