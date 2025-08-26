CREATE TABLE maintenance_tasks (
    id SERIAL PRIMARY KEY,
    category_id INT NOT NULL,
    bike_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_maintenance_tasks_category_id
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_maintenance_tasks_bike_id
        FOREIGN KEY (bike_id) REFERENCES bikes(id)
        ON DELETE RESTRICT
);

CREATE TRIGGER update_maintenance_tasks_updated_at
BEFORE UPDATE ON maintenance_tasks
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_to_now();