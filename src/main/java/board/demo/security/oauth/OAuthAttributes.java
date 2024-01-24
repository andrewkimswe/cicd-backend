package board.demo.security.oauth;

import com.github.andrewkimswe.chat.model.user.Role;
import com.github.andrewkimswe.chat.model.user.SocialType;
import com.github.andrewkimswe.chat.model.user.UserJpaEntity;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class OAuthAttributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String email;
    private final String name;
    private final SocialType provider;

    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String email, String name, SocialType provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.email = email;
        this.name = name;
        this.provider = provider;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }
        throw new IllegalArgumentException("Unsupported provider: " + registrationId);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(
                attributes,
                userNameAttributeName,
                (String) attributes.get("email"),
                (String) attributes.get("name"),
                SocialType.GOOGLE
        );
    }

    public UserJpaEntity toEntity() {
        return new UserJpaEntity(this.email, this.provider, Set.of(Role.USER));
    }
}
