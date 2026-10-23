ALTER TABLE IF EXISTS public.invoice
    ADD CONSTRAINT uk_invoice_order_id UNIQUE (order_id);