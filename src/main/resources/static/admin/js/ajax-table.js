/**
 * ajaxTable.js
 * Usage: Add `data-ajax-url` to table and call initAjaxTable
 */

function initAjaxTable(tableSelector) {
    const table = document.querySelector(tableSelector);
    const container = document.querySelector("[data-status-route]")
    const baseRoute = container.getAttribute('data-status-route');
    // alert(baseRoute)

    if (!table) return;

    const tbody = table.querySelector("tbody");
    const url = table.dataset.ajaxUrl;

    if (!tbody || !url) return;

    // Show loading row
    tbody.innerHTML = `
        <tr class="table-row">
            <td class="table-td text-center" colspan="${table.querySelectorAll('th').length}">
                Loading...
            </td>
        </tr>
    `;

    fetch(url)
        .then(res => res.json())
        .then(data => {
            tbody.innerHTML = "";

            if (!data || data.length === 0) {
                tbody.innerHTML = `
                    <tr class="table-row">
                        <td class="table-td text-center" colspan="${table.querySelectorAll('th').length}">
                            Data not found.
                        </td>
                    </tr>`;
                return;
            }

            data.forEach((item, index) => {
                const row = document.createElement("tr");
                row.className = "table-row";
                row.innerHTML = `
                    <td class="table-td">${index + 1}</td>
                    <td class="table-td"><img src="${item.featureImage}" class="table-image" /></td>
                    <td class="table-td table-title">${item.title}</td>
                    <td class="table-td table-subtitle">${item.templateName || ""}</td>
                    <td class="table-td table-subtitle">${item.order || 0}</td>
                    <td class="table-td">
                        <div class="action-group">
                            <label class="toggle-switch">
                                <input type="checkbox" class="toggle-input" data-id="${item.id}" ${item.status == true ? "checked" : ""}>
                                <div class="toggle-slider"><div class="toggle-thumb"></div></div>
                            </label>
                            <a href="${baseRoute}/edit/${item.id}" class="btn btn-warning btn-sm">Edit</a>
                            <button class="btn btn-danger btn-sm delete-btn" data-id="${item.id}">Delete</button>
                        </div>
                    </td>`;
                tbody.appendChild(row);
            });
        })
        .catch(err => {
            console.error("Error loading table:", err);
            tbody.innerHTML = `
                <tr class="table-row">
                    <td class="table-td text-center" colspan="${table.querySelectorAll('th').length}">
                        Failed to load data.
                    </td>
                </tr>
            `;
        });
}
