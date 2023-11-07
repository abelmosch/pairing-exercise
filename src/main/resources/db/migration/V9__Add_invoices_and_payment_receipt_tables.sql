CREATE TABLE IF NOT EXISTS organisations_schema.invoices (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    buyer_id VARCHAR(255) NOT NULL,
    merchant_id UUID NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    total_invoice_price DECIMAL(10, 2) NOT NULL, -- Assuming Price is a decimal with precision and scale
    currency VARCHAR(8) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP NOT NULL
);
CREATE TABLE IF NOT EXISTS organisations_schema.payment_receipts (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL, -- Assuming Price is a decimal with precision and scale
    currency VARCHAR(8) NOT NULL,
    invoice_id UUID NOT NULL,
    payment_type VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL
    );

ALTER TABLE organisations_schema.payment_receipts
ADD CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) REFERENCES organisations_schema.invoices(id);

ALTER TABLE organisations_schema.invoices
ADD CONSTRAINT fk_invoice_orgs FOREIGN KEY (merchant_id) REFERENCES organisations_schema.organisations(id);
