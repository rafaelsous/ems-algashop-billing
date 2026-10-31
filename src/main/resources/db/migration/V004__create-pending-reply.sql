CREATE TABLE IF NOT EXISTS public.pending_reply (
    invoice_id uuid NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    correlation_id VARCHAR(255) NOT NULL,
    reply_topic VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (invoice_id),
    CONSTRAINT fk_pending_reply_invoice_id FOREIGN KEY (invoice_id) REFERENCES public.invoice (id)
);