-- 이미 지급된 뱃지는 확인한 것으로 처리한다.
ALTER TABLE badge ADD COLUMN checked BIT(1) NOT NULL DEFAULT b'1' AFTER period_key;

ALTER TABLE badge ALTER COLUMN checked DROP DEFAULT;
