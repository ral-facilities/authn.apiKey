package org.icatproject.authn_apikey;

import jakarta.persistence.*;
import org.jboss.logging.Logger;

import java.io.Serializable;

@Entity
@Table(name = "APP_KEYS")
public class ApiKey implements Serializable {

    private static final Logger logger = Logger.getLogger(ApiKey.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", nullable = false)
    private String userName;

    @Column(name = "API_KEY", nullable = false)
    private String key;

    @Column(name = "EXPIRY", nullable = false)
    private Long expiry;

}
