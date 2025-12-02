function initAjaxTable(tableSelector) {
    const table = document.querySelector(tableSelector);

    if (!table) return;

    const tbody = table.querySelector("tbody");
    const thead = table.querySelector("thead");
    const url = table.dataset.ajaxUrl;

    if (!tbody || !url) return;

    // Show loading state
    showLoading();
    loadTableData();

    function showLoading() {
        tbody.innerHTML = `
            <tr class="table-row">
                <td class="table-td text-center" colspan="10">
                    Loading...
                </td>
            </tr>
        `;
    }

    function loadTableData() {
        fetch(url)
            .then(res => res.json())
            .then(data => {
                // data should contain { columns: [...], rows: [...], config: {...} }
                if (!data || !data.columns || data.columns.length === 0) {
                    showError("No columns configured");
                    return;
                }

                const config = data.config || {};
                renderTableHeaders(data.columns);
                renderTableRows(data.columns, data.rows || [], config);
                attachEventListeners(config);
            })
            .catch(err => {
                console.error("Error loading table:", err);
                showError("Failed to load data");
            });
    }

    function renderTableHeaders(columns) {
        const headerRow = thead.querySelector("tr");
        headerRow.innerHTML = "";

        // Serial number column
        const snTh = document.createElement("th");
        snTh.className = "table-th";
        snTh.textContent = "S.N";
        headerRow.appendChild(snTh);

        // Dynamic columns
        columns.forEach(col => {
            const th = document.createElement("th");
            th.className = "table-th";
            th.textContent = col.label;
            headerRow.appendChild(th);
        });

        // Actions column
        const actionsTh = document.createElement("th");
        actionsTh.className = "table-th text-center";
        actionsTh.textContent = "Actions";
        headerRow.appendChild(actionsTh);
    }

    function renderTableRows(columns, rows, config) {
        tbody.innerHTML = "";

        if (!rows || rows.length === 0) {
            tbody.innerHTML = `
                <tr class="table-row">
                    <td class="table-td text-center" colspan="${columns.length + 2}">
                        Data not found.
                    </td>
                </tr>
            `;
            return;
        }

        rows.forEach((item, index) => {
            const row = document.createElement("tr");
            row.className = "table-row";

            // Serial number
            const snTd = document.createElement("td");
            snTd.className = "table-td";
            snTd.textContent = index + 1;
            row.appendChild(snTd);

            // Dynamic columns
            columns.forEach(col => {
                const td = document.createElement("td");
                td.className = "table-td";

                const value = getNestedValue(item, col.field);

                // Handle different column types
                if (col.type === "image") {
                    if (value) {
                        td.innerHTML = `<img src="${value}" class="table-image" alt="image" />`;
                    } else {
                        td.innerHTML = '<span class="text-gray-400">No image</span>';
                    }
                } else if (col.type === "date") {
                    td.textContent = value ? new Date(value).toLocaleDateString() : "-";
                } else if (col.type === "boolean") {
                    td.innerHTML = value ?
                        '<span class="badge badge-success">Yes</span>' :
                        '<span class="badge badge-secondary">No</span>';
                } else if (col.type === "currency") {
                    td.textContent = value ? `$${parseFloat(value).toFixed(2)}` : "-";
                } else {
                    td.textContent = value || "-";
                }

                row.appendChild(td);
            });

            // Actions column
            const actionsTd = document.createElement("td");
            actionsTd.className = "table-td";

            let actionsHtml = '<div class="action-group">';

            // Toggle status (only if hasStatus is true)
            if (config.hasStatus === true) {
                actionsHtml += `
                    <label class="toggle-switch">
                        <input type="checkbox" class="toggle-input" data-id="${item.id}" 
                            ${item.status == true ? "checked" : ""}>
                        <div class="toggle-slider"><div class="toggle-thumb"></div></div>
                    </label>
                `;
            }

            // Edit button (only if editRoute is provided)
            if (config.editRoute) {
                const editUrl = config.editRoute.replace('{id}', item.id);
                actionsHtml += `<a href="${editUrl}" class="btn btn-warning btn-sm">Edit</a>`;
            }

            // Delete button (only if deleteRoute is provided)
            if (config.deleteRoute) {
                actionsHtml += `<button class="btn btn-danger btn-sm delete-btn" data-id="${item.id}">Delete</button>`;
            }

            actionsHtml += '</div>';
            actionsTd.innerHTML = actionsHtml;
            row.appendChild(actionsTd);

            tbody.appendChild(row);
        });
    }

    function getNestedValue(obj, path) {
        return path.split('.').reduce((current, prop) => current?.[prop], obj);
    }

    function showError(message) {
        tbody.innerHTML = `
            <tr class="table-row">
                <td class="table-td text-center" colspan="10">
                    ${message}
                </td>
            </tr>
        `;
    }

    function attachEventListeners(config) {
        // Toggle status (only if hasStatus is true and statusRoute is provided)
        if (config.hasStatus === true && config.statusRoute) {
            const toggleInputs = tbody.querySelectorAll(".toggle-input");
            toggleInputs.forEach(input => {
                input.addEventListener("change", function () {
                    const itemId = this.getAttribute("data-id");
                    const newStatus = this.checked;
                    updateStatus(itemId, newStatus, this, config.statusRoute);
                });
            });
        }

        // Delete buttons (only if deleteRoute is provided)
        if (config.deleteRoute) {
            const deleteButtons = tbody.querySelectorAll(".delete-btn");
            deleteButtons.forEach(button => {
                button.addEventListener("click", function () {
                    const itemId = this.getAttribute("data-id");
                    deleteItem(itemId, config.deleteRoute);
                });
            });
        }
    }

    function updateStatus(itemId, status, element, statusRoute) {
        const statusUrl = statusRoute.replace('{id}', itemId);
        const originalChecked = element.checked;

        fetch(statusUrl, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": getCsrfToken()
            },
            body: JSON.stringify({ status: status })
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    showFlashMessage(data.message || 'Status updated', 'success', 'Success');
                } else {
                    element.checked = !originalChecked;
                    showFlashMessage(data.message || 'Failed to update status', 'error', 'Failed');
                }
            })
            .catch(err => {
                console.error("Error updating status:", err);
                element.checked = !originalChecked;
                showFlashMessage('Error updating status', 'error', 'Failed');
            });
    }

    function deleteItem(itemId, deleteRoute) {
        if (!confirm("Are you sure you want to delete this item?")) {
            return;
        }

        const deleteUrl = deleteRoute.replace('{id}', itemId);
        const deleteBtn = tbody.querySelector(`[data-id="${itemId}"]`).closest(".action-group").querySelector(".delete-btn");
        const originalText = deleteBtn.textContent;

        deleteBtn.textContent = "Deleting...";
        deleteBtn.disabled = true;

        fetch(deleteUrl, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": getCsrfToken()
            }
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    showFlashMessage(data.message || 'Item deleted', 'success', 'Success');
                    loadTableData();
                } else {
                    showFlashMessage(data.message || 'Failed to delete', 'error', 'Failed');
                    deleteBtn.textContent = originalText;
                    deleteBtn.disabled = false;
                }
            })
            .catch(err => {
                console.error("Error deleting item:", err);
                showFlashMessage('Error deleting item', 'error', 'Failed');
                deleteBtn.textContent = originalText;
                deleteBtn.disabled = false;
            });
    }

    function getCsrfToken() {
        const token = document.querySelector('meta[name="_csrf"]');
        return token ? token.getAttribute('content') : '';
    }
}