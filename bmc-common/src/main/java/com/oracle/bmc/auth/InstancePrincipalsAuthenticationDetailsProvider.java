/**
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
 */
package com.oracle.bmc.auth;

import com.google.common.base.Optional;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.internal.FederationClient;
import com.oracle.bmc.auth.internal.X509FederationClient;
import lombok.Getter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

/**
 * Implementation of {@link BasicAuthenticationDetailsProvider} that integrates
 * with instance principal authentication endpoints to generate service tokens
 * used for actual signing.
 * <p>
 * Also uses {@link AuthCachingPolicy} to disable caching (as the values for signing requests
 * may be rotated periodically).
 */
@AuthCachingPolicy(cacheKeyId = false, cachePrivateKey = false)
public class InstancePrincipalsAuthenticationDetailsProvider
        extends AbstractRequestingAuthenticationDetailsProvider
        implements RegionProvider, RefreshableOnNotAuthenticatedProvider<String> {

    @Getter(onMethod = @__({@Override}))
    private final Region region;

    private InstancePrincipalsAuthenticationDetailsProvider(
            FederationClient federationClient,
            SessionKeySupplier sessionKeySupplier,
            Region region) {
        super(federationClient, sessionKeySupplier);
        this.region = region;
    }

    /**
     * Creates a new InstancePrincipalsAuthenticationDetailsProviderBuilder.
     * @return A new builder instance.
     */
    public static InstancePrincipalsAuthenticationDetailsProviderBuilder builder() {
        return new InstancePrincipalsAuthenticationDetailsProviderBuilder();
    }

    /**
     * Refreshes the authentication data used by the provider
     *
     * @return the refreshed authentication data
     *
     * @deprecated use {@link #refresh()} instead
     */
    @Deprecated
    public String refreshSecurityToken() {
        return this.federationClient.refreshAndGetSecurityToken();
    }

    @Override
    public String refresh() {
        return this.federationClient.refreshAndGetSecurityToken();
    }

    /**
     * Builder for InstancePrincipalsAuthenticationDetailsProviderBuilder.
     */
    public static class InstancePrincipalsAuthenticationDetailsProviderBuilder
            extends AbstractFederationClientAuthenticationDetailsProviderBuilder<
                    InstancePrincipalsAuthenticationDetailsProviderBuilder,
                    InstancePrincipalsAuthenticationDetailsProvider> {

        @Override
        protected InstancePrincipalsAuthenticationDetailsProvider buildProvider(
                SessionKeySupplier sessionKeySupplierToUse) {
            return new InstancePrincipalsAuthenticationDetailsProvider(
                    federationClient, sessionKeySupplierToUse, region);
        }

        @Override
        public InstancePrincipalsAuthenticationDetailsProvider build() {
            autoDetectUsingMetadataUrl();

            return super.build();
        }

        @Override
        public InstancePrincipalsAuthenticationDetailsProviderBuilder federationEndpoint(
                String federationEndpoint) {
            return super.federationEndpoint(federationEndpoint);
        }

        @Override
        public InstancePrincipalsAuthenticationDetailsProviderBuilder leafCertificateSupplier(
                X509CertificateSupplier leafCertificateSupplier) {
            return super.leafCertificateSupplier(leafCertificateSupplier);
        }
    }
}
