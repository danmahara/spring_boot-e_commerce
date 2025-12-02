CREATE TABLE product_combination_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    combination_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,

    FOREIGN KEY (combination_id) REFERENCES product_variant_combinations(id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES product_variant_options(id) ON DELETE CASCADE
);
