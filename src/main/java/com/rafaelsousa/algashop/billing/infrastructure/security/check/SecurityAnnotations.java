package com.rafaelsousa.algashop.billing.infrastructure.security.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

public class SecurityAnnotations {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_invoices:read') and not hasRole('CUSTOMER')")
    public @interface CanReadInvoices {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_invoices:write') and @securityChecks.isMachineAuthenticated()")
    public @interface CanWriteInvoices {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_credit-cards:read') and hasRole('CUSTOMER')")
    public @interface CanReadMyCreditCards {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_credit-cards:write') and hasRole('CUSTOMER')")
    public @interface CanWriteMyCreditCards {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @PreAuthorize("hasAuthority('SCOPE_invoices:read') and hasRole('CUSTOMER')")
    public @interface CanReadMyInvoices {}
}
