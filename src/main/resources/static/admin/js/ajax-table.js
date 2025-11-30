/**
 * ajaxTable.js
 * Usage: Add `data-ajax-url` to table and call initAjaxTable
 */



// function initAjaxTable(tableSelector) {
//     const table = document.querySelector(tableSelector);
//     const container = document.querySelector("[data-status-route]")
//     const baseRoute = container.getAttribute('data-status-route');
//     // alert(baseRoute)

//     if (!table) return;

//     const tbody = table.querySelector("tbody");
//     const url = table.dataset.ajaxUrl;

//     if (!tbody || !url) return;

//     // Show loading row
//     tbody.innerHTML = `
//         <tr class="table-row">
//             <td class="table-td text-center" colspan="${table.querySelectorAll('th').length}">
//                 Loading...
//             </td>
//         </tr>
//     `;

//     fetch(url)
//         .then(res => res.json())
//         .then(data => {
//             tbody.innerHTML = "";

//             if (!data || data.length === 0) {
//                 tbody.innerHTML = `
//                     <tr class="table-row">
//                         <td class="table-td text-center" colspan="${table.querySelectorAll('th').length}">
//                             Data not found.
//                         </td>
//                     </tr>`;
//                 return;
//             }

//             data.forEach((item, index) => {
//                 const row = document.createElement("tr");
//                 row.className = "table-row";
//                 row.innerHTML = `
//                     <td class="table-td">${index + 1}</td>
//                     <td class="table-td"><img src="${item.featureImage}" class="table-image" /></td>
//                     <td class="table-td table-title">${item.title}</td>
//                     <td class="table-td table-subtitle">${item.templateName || ""}</td>
//                     <td class="table-td table-subtitle">${item.order || 0}</td>
//                     <td class="table-td">
//                         <div class="action-group">
//                             <label class="toggle-switch">
//                                 <input type="checkbox" class="toggle-input" data-id="${item.id}" ${item.status == true ? "checked" : ""}>
//                                 <div class="toggle-slider"><div class="toggle-thumb"></div></div>
//                             </label>
//                             <a href="${baseRoute}/edit/${item.id}" class="btn btn-warning btn-sm">Edit</a>
//                             <button class="btn btn-danger btn-sm delete-btn" data-id="${item.id}">Delete</button>
//                         </div>
//                     </td>`;
//                 tbody.appendChild(row);
//             });
//         })
//         .catch(err => {
//             console.error("Error loading table:", err);
//             tbody.innerHTML = `
//                 <tr class="table-row">
//                     <td class="table-td text-center" colspan="${table.querySelectorAll('th').length}">
//                         Failed to load data.
//                     </td>
//                 </tr>
//             `;
//         });
// }


function initAjaxTable(tableSelector) {
    const table = document.querySelector(tableSelector);
    const container = document.querySelector("[data-status-route]")
    const baseRoute = container.getAttribute('data-status-route');

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

    loadTableData();

    function loadTableData() {
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

                // Attach delete event listeners
                attachDeleteListeners();
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

    function attachDeleteListeners() {
        const deleteButtons = tbody.querySelectorAll(".delete-btn");
        deleteButtons.forEach(button => {
            button.addEventListener("click", function () {
                const itemId = this.getAttribute("data-id");
                deleteItem(itemId);
            });
        });
    }

    function deleteItem(itemId) {
        // Show confirmation dialog
        if (!confirm("Are you sure you want to delete this item?")) {
            return;
        }

        // Build delete URL
        const deleteUrl = `${baseRoute}/delete/${itemId}`;

        // Show loading state on button
        const deleteBtn = tbody.querySelector(`[data-id="${itemId}"]`);
        const originalText = deleteBtn.textContent;
        deleteBtn.textContent = "Deleting...";
        deleteBtn.disabled = true;

        // Send delete request
        fetch(deleteUrl, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "X-Requested-With": "XMLHttpRequest",
                // Add CSRF token if using Spring Security
                "X-CSRF-TOKEN": getCsrfToken()
            }
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then(data => {
                if (data.success) {
                    showNotification("Item deleted successfully", "success");
                    // Reload table data
                    loadTableData();
                } else {
                    showNotification(data.message || "Failed to delete item", "error");
                    deleteBtn.textContent = originalText;
                    deleteBtn.disabled = false;
                }
            })
            .catch(err => {
                console.error("Error deleting item:", err);
                showNotification("Error deleting item", "error");
                deleteBtn.textContent = originalText;
                deleteBtn.disabled = false;
            });
    }

    // Helper function to get CSRF token (for Spring Security)
    function getCsrfToken() {
        const token = document.querySelector('meta[name="_csrf"]');
        return token ? token.getAttribute('content') : '';
    }

    // Helper function to show notifications
    function showNotification(message, type) {
        // You can use your existing notification system here
        // Or create a simple alert
        if (type === 'success') {
            console.log("✓ " + message);
            // Customize this based on your notification library (toastr, sweetalert, etc.)
        } else {
            console.error("✗ " + message);
        }
    }
}