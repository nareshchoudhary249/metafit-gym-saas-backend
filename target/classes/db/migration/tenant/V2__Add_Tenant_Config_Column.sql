-- Migration V2: Add config column to tenants table for MetaFit
-- This allows storing client-specific branding and settings

-- Add config column if not exists
ALTER TABLE tenants
ADD COLUMN IF NOT EXISTS config JSONB DEFAULT '{}'::jsonb;

-- Create GIN index for fast JSONB queries
CREATE INDEX IF NOT EXISTS idx_tenants_config_gin
ON tenants USING gin(config);

-- Set default config for all existing tenants
UPDATE tenants
SET config = jsonb_build_object(
    'branding', jsonb_build_object(
        'gym_name', name,
        'primary_color', '#10B981',
        'accent_color', '#3B82F6',
        'tagline', NULL
    ),
    'contact', jsonb_build_object(
        'address', NULL,
        'phone', owner_phone,
        'email', owner_email,
        'website', NULL
    ),
    'settings', jsonb_build_object(
        'timezone', 'Asia/Kolkata',
        'currency', 'INR',
        'working_hours', '6:00 AM - 10:00 PM',
        'sms_notifications', false,
        'email_notifications', true
    ),
    'features', jsonb_build_object(
        'biometric_enabled', false,
        'rfid_enabled', false,
        'store_module', false,
        'trainer_module', true,
        'reports_enabled', true
    )
)
WHERE config IS NULL OR config = '{}'::jsonb;

-- Add comment
COMMENT ON COLUMN tenants.config IS 'JSONB column storing tenant-specific branding, contact, settings, and feature flags';

-- Example queries to verify:

-- Get all tenant configs
-- SELECT code, name, config FROM tenants;

-- Get specific config values
-- SELECT
--     code,
--     config->'branding'->>'gym_name' as gym_name,
--     config->'branding'->>'primary_color' as primary_color,
--     config->'features'->>'trainer_module' as trainer_module_enabled
-- FROM tenants;

-- Update specific tenant config
-- UPDATE tenants
-- SET config = jsonb_set(
--     config,
--     '{branding,primary_color}',
--     '"#EF4444"'
-- )
-- WHERE code = 'fitlife';