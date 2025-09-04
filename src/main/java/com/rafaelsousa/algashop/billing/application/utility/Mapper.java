package com.rafaelsousa.algashop.billing.application.utility;

public interface Mapper {
    <T> T convert(Object source, Class<T> destinationType);
}