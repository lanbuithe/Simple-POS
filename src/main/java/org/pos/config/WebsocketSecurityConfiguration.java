package org.pos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebsocketSecurityConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

	/* Issue https://github.com/jhipster/generator-jhipster/issues/1370 */
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            // message types other than MESSAGE and SUBSCRIBE
            .nullDestMatcher().authenticated()
            // matches any destination that starts with /rooms/
            .simpDestMatchers("/topic/**", "/user/**").authenticated()
            // (i.e. cannot send messages directly to /topic/, /queue/)
            // (i.e. cannot subscribe to /topic/messages/* to get messages sent to
            // /topic/messages-user<id>)
            .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
            // catch all
            .anyMessage().denyAll();
    }

    /**
     * Disables CSRF for Websockets.
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
