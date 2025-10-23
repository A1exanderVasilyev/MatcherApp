drop table if exists users cascade;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       date_of_birth DATE NOT NULL,
                       gender VARCHAR(20) NOT NULL CHECK (gender IN ('MALE', 'FEMALE'),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       country VARCHAR(100) NOT NULL,
                       city VARCHAR(100) NOT NULL,
                       latitude DECIMAL(9,6) ,
                       longitude DECIMAL(10,6) ,
                       role VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT chk_latitude_range CHECK (latitude >= -90.0 AND latitude <= 90.0),
                       CONSTRAINT chk_longitude_range CHECK (longitude >= -180.0 AND longitude <= 180.0)
);