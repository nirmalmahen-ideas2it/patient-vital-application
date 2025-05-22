CREATE TABLE vital_signs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pulse INT,
    blood_pressure VARCHAR(255),
    temperature DOUBLE,
    respirations INT,
    blood_sugar DOUBLE,
    weight DOUBLE,
    height DOUBLE,
    spo2_sat DOUBLE,
    pt_inr DOUBLE,
    patient_id BIGINT NOT NULL,
    documented_by BIGINT NOT NULL,
    created_date TIMESTAMP,
    last_modified_date TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255)
);
