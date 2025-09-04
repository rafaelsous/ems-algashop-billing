package com.rafaelsousa.algashop.billing.infrastructure.persistence.invoice;

import com.rafaelsousa.algashop.billing.application.invoice.query.InvoiceOutput;
import com.rafaelsousa.algashop.billing.application.invoice.query.InvoiceQueryService;
import com.rafaelsousa.algashop.billing.application.utility.Mapper;
import com.rafaelsousa.algashop.billing.domain.model.invoice.Invoice;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceNotFoundException;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvoiceQueryServiceImpl implements InvoiceQueryService {
    private final InvoiceRepository invoiceRepository;
    private final Mapper mapper;

    @Override
    public InvoiceOutput findByOrderId(String orderId) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new InvoiceNotFoundException(orderId));

        return mapper.convert(invoice, InvoiceOutput.class);
    }
}