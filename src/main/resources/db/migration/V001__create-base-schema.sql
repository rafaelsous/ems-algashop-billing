CREATE TABLE IF NOT EXISTS public.credit_card (
    id uuid NOT NULL,
    brand varchar(255),
    created_at timestamp with time zone,
    customer_id uuid,
    exp_month integer,
    exp_year integer,
    gateway_code varchar(255),
    last_numbers varchar(255),

    primary key (id)
);

CREATE INDEX IF NOT EXISTS idx_credit_card_customer_id ON public.credit_card (customer_id);

CREATE TABLE IF NOT EXISTS public.payment_settings (
    id uuid NOT NULL,
    credit_card_id uuid,
    gateway_code varchar(255),
    "method" varchar(255),

    primary key (id)
);

CREATE INDEX IF NOT EXISTS idx_payment_settings_credit_card_id ON public.payment_settings (credit_card_id);
ALTER TABLE public.payment_settings ADD CONSTRAINT fk_payment_settings_credit_card_id FOREIGN KEY (credit_card_id) REFERENCES public.credit_card(id);

CREATE TABLE IF NOT EXISTS public.invoice (
    id uuid NOT NULL,
    created_at timestamp with time zone,
    created_by_user_id uuid,
    last_modified_by_user_id uuid,
    last_modified_at timestamp with time zone,
    version bigint NOT NULL,
    cancel_reason varchar(255),
    canceled_at timestamp with time zone,
    customer_id uuid,
    expires_at timestamp with time zone,
    issued_at timestamp with time zone,
    order_id varchar(255),
    paid_at timestamp with time zone,
    payer_address_city varchar(255),
    payer_address_complement varchar(255),
    payer_address_neighborhood varchar(255),
    payer_address_number varchar(255),
    payer_address_state varchar(255),
    payer_address_street varchar(255),
    payer_address_zip_code varchar(255),
    payer_document varchar(255),
    payer_email varchar(255),
    payer_full_name varchar(255),
    payer_phone varchar(255),
    status varchar(255),
    total_amount numeric(38,2),
    payment_settings_id uuid,

    primary key (id),
    constraint uk_invoice_payment_settings_id unique (payment_settings_id)
);

CREATE INDEX IF NOT EXISTS idx_invoice_customer_id ON public.invoice (customer_id);
CREATE INDEX IF NOT EXISTS idx_invoice_order_id ON public.invoice (order_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_invoice_payment_settings_id ON public.invoice (payment_settings_id);

ALTER TABLE public.invoice ADD CONSTRAINT fk_invoice_payment_settings_id FOREIGN KEY (payment_settings_id) REFERENCES public.payment_settings(id);

CREATE TABLE IF NOT EXISTS public.invoice_line_item (
    invoice_id uuid NOT NULL,
    items_amount numeric(38,2),
    items_name varchar(255),
    items_number integer,

    primary key (invoice_id, items_number)
);

CREATE INDEX IF NOT EXISTS idx_invoice_line_item_invoice_id ON public.invoice_line_item (invoice_id);
ALTER TABLE public.invoice_line_item ADD CONSTRAINT fk_invoice_line_item_invoice_id FOREIGN KEY (invoice_id) REFERENCES public.invoice(id);