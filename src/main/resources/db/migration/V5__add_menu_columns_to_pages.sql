ALTER TABLE pages
    ADD COLUMN is_main_menu BOOLEAN NOT NULL DEFAULT FALSE after sort_order,
    ADD COLUMN is_dropdown_menu BOOLEAN NOT NULL DEFAULT FALSE after is_main_menu;
