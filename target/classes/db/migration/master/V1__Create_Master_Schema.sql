-- Master Database Schema for SaaS Multi-Tenancy
-- This runs on gym_master_db

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============= TENANTS TABLE =============
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    db_name VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    owner_name VARCHAR(255),
    owner_email VARCHAR(255),
    owner_phone VARCHAR(15),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT chk_tenant_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'CANCELLED'))
);

CREATE INDEX idx_tenants_code ON tenants(code);
CREATE INDEX idx_tenants_status ON tenants(status);

-- ============= PLANS TABLE =============
CREATE TABLE plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    monthly_price DECIMAL(10, 2) NOT NULL,
    yearly_price DECIMAL(10, 2) NOT NULL,
    max_members INTEGER,
    max_trainers INTEGER,
    features JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_plans_code ON plans(code);

-- Insert default plans
INSERT INTO plans (code, name, monthly_price, yearly_price, max_members, max_trainers, features) VALUES
('BASIC', 'Basic Plan', 799.00, 8500.00, 100, 2, '["Member Management", "Attendance Tracking", "Basic Reports"]'::jsonb),
('STANDARD', 'Standard Plan', 1499.00, 16000.00, 300, 5, '["Member Management", "Attendance Tracking", "Advanced Reports", "SMS Notifications", "Trainer Management"]'::jsonb),
('PREMIUM', 'Premium Plan', 2999.00, 32000.00, 1000, 15, '["All Features", "Multi-Branch", "Biometric Integration", "RFID Access", "Store Module", "API Access"]'::jsonb);

-- ============= SUBSCRIPTIONS TABLE =============
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES plans(id),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    grace_end_date TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_payment_date TIMESTAMP,
    next_payment_due_date TIMESTAMP,
    billing_cycle VARCHAR(20) DEFAULT 'MONTHLY',
    amount_paid DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT chk_subscription_status CHECK (status IN ('ACTIVE', 'GRACE', 'SUSPENDED', 'CANCELLED')),
    CONSTRAINT chk_billing_cycle CHECK (billing_cycle IN ('MONTHLY', 'YEARLY'))
);

CREATE INDEX idx_subscriptions_tenant ON subscriptions(tenant_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscriptions_end_date ON subscriptions(end_date);

-- ============= AUDIT LOG TABLE =============
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    performed_by VARCHAR(255),
    details JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_tenant ON audit_logs(tenant_id);
CREATE INDEX idx_audit_created ON audit_logs(created_at);

-- ============= COMMENTS =============
COMMENT ON TABLE tenants IS 'Master table storing all gym tenants (clients)';
COMMENT ON TABLE plans IS 'SaaS pricing plans';
COMMENT ON TABLE subscriptions IS 'Tenant subscription status and billing info';
COMMENT ON TABLE audit_logs IS 'System-wide audit trail';