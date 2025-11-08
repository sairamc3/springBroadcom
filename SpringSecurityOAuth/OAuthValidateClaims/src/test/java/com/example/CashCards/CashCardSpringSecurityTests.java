package com.example.CashCards;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@SpringBootTest
@AutoConfigureMockMvc
public class CashCardSpringSecurityTests {

    @Autowired
    JwtEncoder encoder;

    @Autowired
    private MockMvc mvc;

    private String mint(){
        return mint(consumer -> {});
    }

    private String mint(Consumer<JwtClaimsSet.Builder> consumer){
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(1000000))
                .subject("sarah1")
                .issuer("http://localhost:9000")
                .audience(Arrays.asList("cashcard-client"))
                .claim("scp", Arrays.asList("cashcard:read", "cashcard:write"));

        consumer.accept(builder);

        JwtEncoderParameters parameters = JwtEncoderParameters.from(builder.build());

        return this.encoder.encode(parameters).getTokenValue();
    }

    @TestConfiguration
    static class TestJwtConfiguration{

        @Bean
        JwtEncoder jwtEncoder(@Value("classpath:authz.pub") RSAPublicKey pub,
                              @Value("classpath:authz.pem") RSAPrivateKey pem){
            RSAKey key = new RSAKey.Builder(pub).privateKey(pem).build();
            return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
        }
    }

    @Test
    void shouldBeForbidden() throws Exception {
        String token = mint();

        this.mvc.perform(MockMvcRequestBuilders.get("/cashcards/100"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void shouldRequireValidTokens() throws Exception {
        String token = mint();

        this.mvc.perform(MockMvcRequestBuilders.get("/cashcards/100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldNotAllowTokensWithInvalidAudience() throws Exception{

        String token = mint(claims -> claims.audience(List.of("https://wrong")));

        this.mvc.perform(MockMvcRequestBuilders.get("/cashcards/100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().string("WWW-Authenticate", Matchers.containsString("The aud claim is not valid")));

    }

    @Test
    void shouldNotAllowTokensThatAreExpired() throws Exception{

        String token = mint(claims ->
                claims.issuedAt(Instant.now().minusSeconds(3600))
                        .expiresAt(Instant.now().minusSeconds(3599)));

        this.mvc.perform(MockMvcRequestBuilders.get("/cashcards/100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().string("WWW-Authenticate", Matchers.containsString("Jwt expired")));

    }
}
